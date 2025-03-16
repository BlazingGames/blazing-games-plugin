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

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import de.blazemcworld.blazinggames.BlazingGames;
import dev.ivycollective.ivyhttp.http.EarlyResponse;
import dev.ivycollective.ivyhttp.http.EndpointResponse;
import dev.ivycollective.ivyhttp.http.RequestContext;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class APIUtils {
    public static APIUtils of(RequestContext context) {
        return new APIUtils(context);
    }

    public final RequestContext context;
    public APIUtils(RequestContext context) {
        this.context = context;
    }

    public LinkedUser getAuthentication() {
        String authHeader = this.context.getFirstHeader("Authorization");
        if (authHeader == null) {
            return null;
        } else {
            String[] parts = authHeader.split(" ");
            if (parts.length != 2) {
                return null;
            } else if (!"Bearer".equals(parts[0])) {
                return null;
            } else {
                String token = parts[1];
                return LinkedUser.getLinkedUserFromJWT(token);
            }
        }
    }

    public boolean isAuthenticated() {
        return this.getAuthentication() != null;
    }

    public LinkedUser requireAuthentication() throws EarlyResponse {
        LinkedUser linked = this.getAuthentication();
        if (linked == null) {
            throw new EarlyResponse(EndpointResponse.of401());
        } else {
            return linked;
        }
    }

    public void requirePermission(Permission permission) throws EarlyResponse {
        LinkedUser linked = this.requireAuthentication();
        if (!linked.permissions().contains(permission)) {
            throw new EarlyResponse(EndpointResponse.of403());
        }
    }

    public static EndpointResponse authError(String title, String desc) {
        return EndpointResponse.redirect("/auth/error?error=" + URLEncoder.encode(title, StandardCharsets.UTF_8) + "&desc=" + URLEncoder.encode(desc, StandardCharsets.UTF_8));
    }

    private static final Configuration config = new Configuration(Configuration.VERSION_2_3_33);
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
            return EndpointResponse.of500();
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
}