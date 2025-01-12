/*
 * Copyright 2025 The Blazing Games Maintainers
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.blazemcworld.blazinggames.computing.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record APIDocs(
    String title,
    RequestMethod method,
    String description,
    HashMap<String, String> incomingArgs,
    HashMap<String, String> outgoingArgs,
    HashMap<Integer, String> responseCodes,
    List<Permission> permissions
) {
    public static APIDocs.Builder builder() {
        return new APIDocs.Builder();
    }

    public static class Builder {
        private String title = null;
        private RequestMethod method = null;
        private String description = null;
        private final HashMap<String, String> incomingArgs = new HashMap<>();
        private final HashMap<String, String> outgoingArgs = new HashMap<>();
        private final HashMap<Integer, String> responseCodes = new HashMap<>();
        private final ArrayList<Permission> permissions = new ArrayList<>();

        public APIDocs build() {
            if (this.title != null && this.method != null && this.description != null) {
                return new APIDocs(this.title, this.method, this.description, this.incomingArgs, this.outgoingArgs, this.responseCodes, this.permissions);
            } else {
                throw new IllegalArgumentException("Missing parameters!");
            }
        }

        public String title() {
            return this.title;
        }

        public APIDocs.Builder title(String title) {
            this.title = title;
            return this;
        }

        public RequestMethod method() {
            return this.method;
        }

        public APIDocs.Builder method(RequestMethod method) {
            this.method = method;
            return this;
        }

        public String description() {
            return this.description;
        }

        public APIDocs.Builder description(String description) {
            this.description = description;
            return this;
        }

        public APIDocs.Builder addIncomingArgument(String id, String description) {
            this.incomingArgs.put(id, description);
            return this;
        }

        public APIDocs.Builder removeIncomingArgument(String id) {
            this.incomingArgs.remove(id);
            return this;
        }

        public HashMap<String, String> incomingArgs() {
            return this.incomingArgs;
        }

        public APIDocs.Builder addOutgoingArgument(String id, String description) {
            this.outgoingArgs.put(id, description);
            return this;
        }

        public APIDocs.Builder removeOutgoingArgument(String id) {
            this.outgoingArgs.remove(id);
            return this;
        }

        public HashMap<String, String> outgoingArgs() {
            return this.outgoingArgs;
        }

        public APIDocs.Builder addResponseCode(int code, String description) {
            this.responseCodes.put(code, description);
            return this;
        }

        public APIDocs.Builder removeResponseCode(int code) {
            this.responseCodes.remove(code);
            return this;
        }

        public HashMap<Integer, String> responseCodes() {
            return this.responseCodes;
        }

        public APIDocs.Builder add200() {
            this.responseCodes.put(200, "Success");
            return this;
        }

        public APIDocs.Builder add400() {
            this.responseCodes.put(400, "Bad request; parameters missing");
            return this;
        }

        public APIDocs.Builder add401() {
            this.responseCodes.put(401, "Unauthorized; your Authentication header is missing");
            return this;
        }

        public APIDocs.Builder add403() {
            this.responseCodes.put(403, "Forbidden; insufficient permissions to do this action");
            return this;
        }

        public APIDocs.Builder add405() {
            this.responseCodes.put(405, "Method not allowed; you are using the wrong request method");
            return this;
        }

        public APIDocs.Builder add415() {
            this.responseCodes.put(415, "Unsupported Media Type; you are using the wrong content type");
            return this;
        }

        public APIDocs.Builder add429() {
            this.responseCodes.put(429, "Too Many Requests; you've reached the ratelimit for this endpoint");
            return this;
        }

        public APIDocs.Builder add500() {
            this.responseCodes.put(500, "Internal server error");
            return this;
        }

        public APIDocs.Builder addGenericsUnauthenthicated() {
            return this.add200().add400().add500().add405().add429().add415();
        }

        public APIDocs.Builder addGenerics() {
            return this.add200().add400().add500().add405().add429().add415().add401().add403();
        }

        public APIDocs.Builder removeBodyFromGenerics() {
            return this.removeResponseCode(415);
        }

        public APIDocs.Builder addPermission(Permission permission) {
            if (!this.permissions.contains(permission)) {
                this.permissions.add(permission);
            }

            return this;
        }

        public APIDocs.Builder removePermission(Permission permission) {
            this.permissions.remove(permission);
            return this;
        }

        public List<Permission> permissions() {
            return List.copyOf(this.permissions);
        }
    }
}
