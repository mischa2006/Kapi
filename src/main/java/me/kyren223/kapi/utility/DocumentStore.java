/*
 * Copyright (c) 2024 Kapi Contributors. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted if the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions, the following disclaimer and the list of contributors.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. The buyer of the "Kapi" API is granted the right to use this software
 *    as a dependency in their own software projects. However, the buyer
 *    may not resell or distribute the "Kapi" API, in whole or in part, to other parties.
 *
 * 4. The buyer may include the "Kapi" API in a "fat jar" along with their own code.
 *    The license for the "fat jar" is at the buyer's discretion and may allow
 *    redistribution of the "fat jar", but the "Kapi" API code inside the "fat jar"
 *    must not be modified.
 *
 * 5. Neither the name "Kapi" nor the names of its contributors may be used to endorse
 *    or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY "Kapi" API, AND ITS CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL "Kapi" API, AND CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Kapi Contributors:
 * - Kyren223
 */

package me.kyren223.kapi.utility;

import me.kyren223.kapi.annotations.Kapi;
import me.kyren223.kapi.core.Kplugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * A utility class that manages configuration files.<br>
 * Allows CRUD operations on documents (.yml configs).<br>
 * Documents are saved and loaded automatically.
 *
 * @deprecated Use {@link Config} instead
 */
@Kapi
@NullMarked
@Deprecated(since = "1.1.0", forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
public class DocumentStore {
    
    private DocumentStore() {
        throw new AssertionError("DocumentStore should not be instantiated");
    }
    
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static HashMap<String,FileConfiguration> a;
    
    private static String getPath(String path) {
        path = path.trim();
        
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be empty");
        }
        
        path = path.replace("\\", "/");
        
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        if (path.endsWith("/")) {
            throw new IllegalArgumentException("Must be a file, not a directory");
        }
        
        if (!path.endsWith(".yml")) {
            path += ".yml";
        }
        
        return path;
    }
    
    /**
     * Creates a new document.<br>
     * If the document already exists, nothing will happen.<br>
     * If the document exists as a file but wasn't loaded yet, it will get loaded.
     *
     * @param path The path to the document
     * @deprecated Use {@link Config} instead, which has no equivalent method
     */
    @Kapi
    @Deprecated(since = "1.1.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    public static void createDocument(String path) {
        path = getPath(path);
        if (documentExists(path)) return;
        loadDocument(path);
    }
    
    /**
     * Checks if a document exists.
     *
     * @param path The path to the document
     * @return True if the document exists, false otherwise
     * @deprecated Use {@link Config#exists(String)} instead
     */
    @Kapi
    @Deprecated(since = "1.1.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    public static boolean documentExists(String path) {
        path = getPath(path);
        return a.containsKey(path);
    }
    
    /**
     * Gets the document from memory.<br>
     * If the document doesn't exist, it'll be loaded with {@link #loadDocument(String)}.<br>
     * Modifications to the document will not be saved to disk,
     * but will be saved to memory, see {@link #saveDocument(String)}
     * or {@link #getAndWriteDocument(String, Consumer)} to save to disk.
     *
     * @param path The path to the document
     * @return The document
     * @deprecated Use {@link Config#get(String)} instead
     */
    @Kapi
    @Deprecated(since = "1.1.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    public static FileConfiguration getDocument(String path) {
        path = getPath(path);
        if (!documentExists(path)) loadDocument(path);
        return a.get(path);
    }
    
    /**
     * Gets the document and passes it to a consumer.<br>
     * Uses {@link #getDocument(String)} internally.
     *
     * @param path     The path to the document
     * @param consumer The consumer to pass the document to
     * @deprecated Use {@link Config#getAndWrite(String, Consumer)} instead
     */
    @Kapi
    @Deprecated(since = "1.1.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    public static void getAndWriteDocument(String path, Consumer<FileConfiguration> consumer) {
        consumer.accept(getDocument(path));
        saveDocument(path);
    }
    
    /**
     * Loads a document and returns it.<br>
     * Reads the document from disk, if it doesn't exist on disk,
     * it will create a new empty document.
     *
     * @param path The path to the document
     * @return The document
     * @deprecated Use {@link Config#get(String)} instead
     */
    @Kapi
    @Deprecated(since = "1.1.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    public static FileConfiguration loadAndGetDocument(String path) {
        loadDocument(path);
        assert documentExists(path);
        return getDocument(path);
    }
    
    /**
     * Gets the document from disk.
     *
     * @param path The path to the document
     * @return The document, or null if it doesn't exist
     * @deprecated Use {@link Config#get(String)} instead
     */
    @Kapi
    @Deprecated(since = "1.1.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    private static @Nullable FileConfiguration getDocumentFromDisk(String path) {
        File file = new File(Kplugin.get().getDataFolder(), path);
        if (!file.exists()) return null;
        return YamlConfiguration.loadConfiguration(file);
    }
    
    /**
     * Loads the document from disk<br>
     * If the document is already loaded, it will overwrite it<br>
     * If the document doesn't exist, it will create a new empty document.
     *
     * @param path Path to the document
     * @deprecated Use {@link Config} instead, which has no equivalent method
     */
    @Kapi
    @Deprecated(since = "1.1.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    public static void loadDocument(String path) {
        path = getPath(path);
        
        FileConfiguration document = getDocumentFromDisk(path);
        if (document == null) {
            document = new YamlConfiguration();
        }
        
        a.put(path, document);
    }
    
    /**
     * Loads all documents from the plugin's data folder.<br>
     * <br>
     * You can specify a `.kapignore` file in the data folder of the plugin.<br>
     * This file contains a list of regex patterns to ignore.<br>
     * Each line in the file is a regex pattern.<br>
     * Start a line with `#` to ignore it.<br>
     * All lines are trimmed before being used, and blank/empty lines are skipped.
     *
     * @deprecated Use {@link Config}, which has no equivalent method
     */
    @Kapi
    @Deprecated(since = "1.1.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    public static void loadDocuments() {
    }
    
    /**
     * Saves the document to disk.
     *
     * @param path The path to the document.
     * @deprecated Use {@link Config#write(String, YamlConfiguration)} instead
     */
    @Kapi
    @Deprecated(since = "1.1.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    public static void saveDocument(String path) {
    }
    
    /**
     * Saves all documents to disk,
     * see {@link #saveDocument(String)} for a single document.
     *
     * @deprecated {@link Config} should be used, which has no equivalent method
     */
    @Kapi
    @Deprecated(since = "1.1.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    public static void saveDocuments() {
    }
}
