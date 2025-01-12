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
package de.blazemcworld.blazinggames.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.utils.Pair;

public class DataStorage<T, I> {
    public static final String DATA_STORAGE_DIRECTORY = "blazinggames";
    public static final String INDEX_FILE_NAME = "__datastorage_index.json";
    private static final File rootDir = new File(DATA_STORAGE_DIRECTORY);
    static {
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
    }

    /**
     * Creates a new DataStorage for the given class
     * @param <T> The type of data being stored
     * @param <I> The type of the name identifiers
     * @param clazz The class storing the data (use getClass())
     * @param tableId Unique table identifier (null if you don't want to use tables, useful for storing many data types)
     * @param storage Storage provider
     * @param name Naming provider
     * @param compression Compression provider
     * @return DataStorage
     */
    public static <T, I> DataStorage<T, I> forClass(Class<?> clazz, String tableId, StorageProvider<T> storage, NameProvider<I> name, CompressionProvider compression) {
        String dirName = tableId == null ? clazz.getName() : clazz.getName() + "+" + tableId;
        File dir = new File(rootDir, dirName);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return new DataStorage<>(dir, storage, name, compression);
    }

    // /**
    //  * Creates a new DataStorage for the given class, with an indexer
    //  * @param <T> The type of data being stored
    //  * @param <I> The type of the name identifiers
    //  * @param clazz The class storing the data (use getClass())
    //  * @param storage Storage provider
    //  * @param name Naming provider
    //  * @param compression Compression provider
    //  * @param indexer Indexer
    //  * @return DataStorage
    //  */
    // public static <T, I> DataStorage<T, I> forClass(Class<?> clazz, StorageProvider<T> storage, NameProvider<I> name, CompressionProvider compression, Consumer<IndexContext<T, I>> indexer) {
    //     String className = clazz.getName();
    //     File dir = new File(rootDir, className);

    //     if (!dir.exists()) {
    //         dir.mkdirs();
    //     }

    //     return new DataStorage<>(dir, storage, name, compression);
    // }

    public final File dir;
    public final StorageProvider<T> storage;
    public final NameProvider<I> name;
    public final CompressionProvider compression;
    public DataStorage(File dir, StorageProvider<T> storage, NameProvider<I> name, CompressionProvider compression) {
        this.dir = dir;
        this.storage = storage;
        this.name = name;
        this.compression = compression;
        // this.indexer = null;
        // this.index = null;
    }

    // public final Consumer<IndexContext<T, I>> indexer;
    // public final HashMap<String, Object> index;
    // public DataStorage(File dir, StorageProvider<T> storage, NameProvider<I> name, CompressionProvider compression, Consumer<IndexContext<T, I>> indexer) {
    //     this.dir = dir;
    //     this.storage = storage;
    //     this.name = name;
    //     this.compression = compression;
    //     this.indexer = indexer;
    //     this.index = new HashMap<>();
    // }

    /**
     * Utility to read an entire file as a byte[]
     */
    private byte[] readFile(File file) {
        if (!file.exists()) {
            return null;
        }
        
        try (var fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        } catch (IOException e) {
            BlazingGames.get().log(e);
            return null;
        }
    }

    /**
     * Utility to write a byte[] to a file
     */
    private void writeFile(File file, byte[] data) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            BlazingGames.get().log(e);
            return;
        }

        try (var fos = new FileOutputStream(file)) {
            fos.write(data);
        } catch (IOException e) {
            BlazingGames.get().log(e);
        }
    }

    /**
     * Synchronized file usage
     */
    private void useFile(I identifier, Consumer<File> callback) {
        useFile(identifier, file -> {
            callback.accept(file);
            return null;
        });
    }

    /**
     * Synchronized file usage (with return value)
     */
    private synchronized <V> V useFile(I identifier, Function<File, V> callback) {
        StringBuilder out = new StringBuilder(name.fromValue(identifier));
        
        if (storage.fileExtension() != null) {
            out.append(".").append(storage.fileExtension());
        }

        if (compression.fileExtension() != null) {
            out.append(".").append(compression.fileExtension());
        }
        
        File file = new File(dir, out.toString());
        return callback.apply(file);
    }

    private String stripFileExtension(String filename) {
        int toRemove = 0;

        if (compression.fileExtension() != null) {
            toRemove += 1;
        }

        if (storage.fileExtension() != null) {
            toRemove += 1;
        }

        List<String> parts = new ArrayList<>(List.of(filename.split("\\.")));

        // remove the last toRemove parts
        while (toRemove > 0) {
            parts.remove(parts.size() - 1);
            toRemove--;
        }

        return String.join(".", parts);
    }

    /**
     * Gets the data currently stored for the given identifier
     * @param identifier The identifier
     * @return Data, or null if it can't be found
     */
    public T getData(I identifier) {
        return getData(identifier, null);
    }

    /**
     * Gets the data currently stored for this identifier
     * @param identifier The identifier
     * @param defaultValue Default value to return if the data can't be found
     * @return Data, or defaultValue if it can't be found
     */
    public T getData(I identifier, T defaultValue) {
        return useFile(identifier, file -> {
            if (!file.exists()) return null;
            byte[] contents = readFile(file);
            if (contents == null) return null;
            byte[] decompressed = compression.decompress(contents);
            return storage.read(decompressed);
        });
    }

    /**
     * Construct a new instance of a class with the given identifier, and store it
     * @param applyId Function to create a new instance of T with the I specified
     * @return The identifier used and data constructed
     */
    public Pair<T, I> storeNext(Function<I, T> constructor) {
        I id = name.next();
        if (id == null) {
            throw new UnsupportedOperationException("Your name provider doesn't support next() values");
        }
        T data = constructor.apply(id);
        storeData(id, data);
        return new Pair<>(data, id);
    }

    /**
     * Stores data and returns an identifier for it
     * @param data Data to store
     * @return New identifier to find the same data
     */
    public I storeNext(T data) {
        I id = name.next();
        if (id == null) {
            throw new UnsupportedOperationException("Your name provider doesn't support next() values");
        }
        storeData(id, data);
        return id;
    }

    /**
     * Stores data with an identifier
     * @param identifier The identifier
     * @param data The data to store
     */
    public void storeData(I identifier, T data) {
        useFile(identifier, file -> {
            byte[] bytes = storage.write(data);
            byte[] compressed = compression.compress(bytes);
            writeFile(file, compressed);
        });
    }

    /**
     * Deletes stored data
     * @param identifier The identifier of the data to delete
     */
    public void deleteData(I identifier) {
        useFile(identifier, file -> {
            if (file.exists()) {
                file.delete();
            }
        });
    }

    // /**
    //  * Get data from the index
    //  * @param key Index key
    //  * @return Data, if it exists
    //  */
    // @SuppressWarnings("unchecked")
    // public <V> V queryIndex(String key, Class<V> clazz) {
    //     if (index == null) return null;
    //     if (!index.containsKey(key)) return null;
    //     if (clazz.isAssignableFrom(index.get(key).getClass())) return (V) index.get(key);
    //     return null;
    // }

    /**
     * Get all identifiers of data where the predicate matches
     * @param predicate Predicate to test with
     * @return List of matching data identifiers
     */
    public List<I> query(Predicate<T> predicate) {
        try (Stream<Path> paths = Files.walk(dir.toPath())) {
            return paths.filter(Files::isRegularFile).map(path -> name.fromString(stripFileExtension(path.toFile().getName()))).filter(i -> {
                T data = getData(i, null);
                if (data == null) return false;
                return predicate.test(data);
            }).collect(Collectors.toList());
        } catch (IOException e) {
            BlazingGames.get().log(e);
            return List.of();
        }
    }

    /**
     * Get data where the predicate matches
     * @param predicate Predicate to test with
     * @return List of matching data
     */
    public List<T> queryForData(Predicate<T> predicate) {
        return query(predicate).stream().map(this::getData).collect(Collectors.toList());
    }

    /**
     * Get all identifiers for data matching a certain condition
     * @param predicate Predicate to test with
     * @return List of matching data identifiers
     */
    public List<I> queryIdentifiers(Predicate<I> predicate) {
        try (Stream<Path> paths = Files.walk(dir.toPath())) {
            return paths.filter(Files::isRegularFile).map(path -> name.fromString(stripFileExtension(path.toFile().getName())))
                .filter(predicate::test).collect(Collectors.toList());
        } catch (IOException e) {
            BlazingGames.get().log(e);
            return List.of();
        }
    }

    /**
     * Get all data where the identifiers match a certain condition
     * @param predicate Predicate to test with
     * @return List of matching data
     */
    public List<T> queryIdentifiersForData(Predicate<I> predicate) {
        return queryIdentifiers(predicate).stream().map(this::getData).collect(Collectors.toList());
    }

    /**
     * Checks if data exists. Works even if the data stored is null
     * @param identifier The identifier for the data
     * @return If the stored data's file actually exists, without reading it
     */
    public boolean hasData(I identifier) {
        return useFile(identifier, File::exists);
    }
}
