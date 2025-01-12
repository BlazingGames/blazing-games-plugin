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

public class EarlyResponse extends Throwable {
    private final EndpointResponse response;

    public EarlyResponse(EndpointResponse response) {
        this.response = response;
    }

    public static EarlyResponse of(EndpointResponse e) {
        return new EarlyResponse(e);
    }

    public EndpointResponse getResponse() {
        return this.response;
    }
}
