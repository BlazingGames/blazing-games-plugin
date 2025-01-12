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

import java.util.List;

public enum RequestMethod {
    GET("GET", RequestMethod.Flag.USE_QUERY_PARAMETERS),
    HEAD("GET", RequestMethod.Flag.USE_QUERY_PARAMETERS, RequestMethod.Flag.RESPOND_EMPTY_BODY),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE", RequestMethod.Flag.USE_QUERY_PARAMETERS),
    PATCH("PATCH"),
    OPTIONS("OPTIONS"),
    NULL("NULL");

    public final String methodCall;
    public final List<RequestMethod.Flag> flags;

    private RequestMethod(String methodCall, RequestMethod.Flag... flags) {
        this.methodCall = methodCall;
        this.flags = List.of(flags);
    }

    public static enum Flag {
        USE_QUERY_PARAMETERS,
        RESPOND_EMPTY_BODY;
    }
}
