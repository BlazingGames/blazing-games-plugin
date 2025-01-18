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

import com.google.gson.JsonObject;
import de.blazemcworld.blazinggames.BlazingGames;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class EndpointResponse {
    final int status;
    final Map<String, String> headers;
    final byte[] body;
    private static final Configuration config = new Configuration(Configuration.VERSION_2_3_33);

    public EndpointResponse(int status, HashMap<String, String> headers, byte[] body) {
        this.status = status;
        this.headers = Map.copyOf(headers);
        this.body = body == null ? new byte[0] : body;
    }

    public static EndpointResponse.Builder builder(int status) {
        return new EndpointResponse.Builder(status);
    }

    private static EndpointResponse.Builder _genericError(int code, String content) {
        JsonObject object = new JsonObject();
        object.addProperty("code", code);
        object.addProperty("message", content);
        object.addProperty("success", false);
        return new EndpointResponse.Builder(code).header("Content-Type", "application/json").body(BlazingGames.gson.toJson(object));
    }

    public static EndpointResponse.Builder of200(JsonObject json) {
        json.addProperty("success", true);
        return new EndpointResponse.Builder(200).header("Content-Type", "application/json").body(BlazingGames.gson.toJson(json));
    }

    public static EndpointResponse.Builder of204() {
        return new EndpointResponse.Builder(204);
    }

    public static EndpointResponse redirect(String newLocation) {
        return new EndpointResponse.Builder(302).header("Location", newLocation).build();
    }

    public static EndpointResponse of400(String error) {
        return _genericError(400, error).build();
    }

    public static EndpointResponse of401() {
        return _genericError(401, "Unauthorized").build();
    }

    public static EndpointResponse of403() {
        return _genericError(403, "Forbidden").build();
    }

    public static EndpointResponse of404() {
        return _genericError(404, "Not Found").build();
    }

    public static EndpointResponse of405() {
        return _genericError(405, "Method Not Allowed").build();
    }

    public static EndpointResponse of500() {
        return _genericError(500, "Internal Server Error").build();
    }

    public static EndpointResponse authError(String title, String desc) {
        return redirect("/auth/error?error=" + URLEncoder.encode(title, StandardCharsets.UTF_8) + "&desc=" + URLEncoder.encode(desc, StandardCharsets.UTF_8));
    }

    public static EndpointResponse ofHTML(String templatePath, Map<String, Object> map) {
        Map<String, Object> data = map == null ? new HashMap<>() : map;

        try {
            Template template = config.getTemplate(templatePath);
            StringWriter sw = new StringWriter();
            template.process(data, sw);
            sw.flush();
            return new EndpointResponse.Builder(200).header("Content-Type", "text/html").body(sw.toString()).build();
        } catch (TemplateException | IOException e) {
            BlazingGames.get().log(e);
            return of500();
        }
    }

    static {
        config.setDefaultEncoding("UTF-8");
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setLogTemplateExceptions(false);
        config.setWrapUncheckedExceptions(true);
        config.setFallbackOnNullLoopVariable(false);
        config.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
        config.setClassForTemplateLoading(BlazingGames.get().getClass(), "/html/");
    }

    public static class Builder {
        private int status;
        private HashMap<String, String> headers = new HashMap<>();
        private byte[] body = null;

        public Builder(int status) {
            this.status = status;
        }

        public int status() {
            return this.status;
        }

        public EndpointResponse.Builder status(int status) {
            this.status = status;
            return this;
        }

        public HashMap<String, String> headers() {
            return this.headers;
        }

        public EndpointResponse.Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public EndpointResponse.Builder removeHeader(String name) {
            this.headers.remove(name);
            return this;
        }

        public EndpointResponse.Builder headers(HashMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public byte[] body() {
            return this.body;
        }

        public EndpointResponse.Builder body(String body) {
            this.body = body.getBytes();
            return this;
        }

        public EndpointResponse.Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public EndpointResponse build() {
            return new EndpointResponse(this.status, this.headers, this.body);
        }
    }
}
