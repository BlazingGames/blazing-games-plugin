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
package de.blazemcworld.blazinggames.utils;

import de.blazemcworld.blazinggames.BlazingGames;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipToolkit {
    public static byte[] compress(String input) {
        return compressBytes(input.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] compressBytes(byte[] input) {
        try {
            byte[] bytes;
            try (
                ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                GZIPOutputStream gzipOut = new GZIPOutputStream(bytesOut);
            ) {
                gzipOut.write(input);
                gzipOut.close();
                bytes = bytesOut.toByteArray();
            }

            return bytes;
        } catch (IOException e) {
            BlazingGames.get().log(e);
            return null;
        }
    }

    public static String decompress(byte[] input) {
        return new String(decompressBytes(input), StandardCharsets.UTF_8);
    }

    public static byte[] decompressBytes(byte[] input) {
        try {
            byte[] data;
            try (
                ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                ByteArrayInputStream bytesIn = new ByteArrayInputStream(input);
                GZIPInputStream gzipIn = new GZIPInputStream(bytesIn);
            ) {
                bytesOut.writeBytes(gzipIn.readAllBytes());
                data = bytesOut.toByteArray();
            }

            return data;
        } catch (IOException e) {
            BlazingGames.get().log(e);
            return null;
        }
    }
}
