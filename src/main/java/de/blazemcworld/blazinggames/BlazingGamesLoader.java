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
package de.blazemcworld.blazinggames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;

public class BlazingGamesLoader implements PluginLoader {
    public static final String mavenCentral = "https://repo1.maven.org/maven2/";

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        Logger logger = LoggerFactory.getLogger(getClass());
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        logger.info("Adding maven central ({})", mavenCentral);
        resolver.addRepository(new RemoteRepository.Builder("maven-central", "default", mavenCentral).build());

        String[] repos = readFile("repositories");
        for (String repo : repos) {
            String[] parts = repo.split(";");
            if (parts.length != 2) {
                throw new RuntimeException("Invalid repository format: " + repo);
            }
            logger.info("Adding repository {} ({})", parts[0], parts[1]);
            resolver.addRepository(new RemoteRepository.Builder(parts[0], "default", parts[1]).build());
        }

        String[] deps = readFile("dependencies");
        for (String dep : deps) {
            logger.info("Adding dependency {}", dep);
            resolver.addDependency(new Dependency(new DefaultArtifact(dep), null));
        }

        logger.info("Finished adding dependencies");
        classpathBuilder.addLibrary(resolver);
    }

    public String[] readFile(String name) {
        try (
            InputStream stream = BlazingGames.class.getClassLoader().getResourceAsStream(name + ".txt");
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(reader);
        ) {
            return bufferedReader.lines().toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }
}