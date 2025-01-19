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
package de.blazemcworld.blazinggames.computing.api.body;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;

public class FileBodyOutput implements BodyOutput {
    public final File file;
    public FileBodyOutput(File file) {
        this.file = file;
    }

    @Override
    public void handle(OutputStream stream) throws IOException {
        IOUtils.copy(Files.newInputStream(file.toPath()), stream);
    }

    @Override
    public long length() {
        try {
            return Files.size(file.toPath());
        } catch (IOException e) {
            return -1;
        }
    }
}