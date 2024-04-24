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

package me.kyren223.kapi.core;

import me.kyren223.kapi.annotations.Kapi;
import me.kyren223.kapi.math.CryptoUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.UUID;

/**
 * This class should be extended by the main class of your plugin.<br>
 * Extend this instead of {@link JavaPlugin}<br>
 */
@Kapi
public abstract class Kplugin extends JavaPlugin {
    
    private static Class<?> clazz;
    private static Kplugin instance;
    public static String userLicense;
    
    private void tryLoadingKapi() {
        if (clazz != null) return;
        try {
            clazz = Class.forName("me.kyren223.kapi.core.KapiInit");
        } catch (ClassNotFoundException e) {
            clazz = null;
            askServerForClass();
            if (clazz == null) {
                System.out.println("KapiInit not found!");
            }
        }
        System.out.println("KapiInit has been loaded!");
    }
    
    private YamlConfiguration getKapiConfig() {
        File dataFolder = getDataFolder();
        //noinspection ResultOfMethodCallIgnored
        dataFolder.mkdirs();
        File configFile = new File(dataFolder, "kapi.yml");
        try {
            boolean created = configFile.createNewFile();
            if (created) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                config.set("license", "PUT YOUR LICENSE KEY HERE");
                config.set("server", "78.46.110.72");
                config.set("port", 50008);
                config.set("publicKey", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq3PnBCFuL0aoIikYcChuixkUg0vQTHX6Un5/kCg/UzIlTX7f2PHEf0WFj/OhSldOr93abIOMM8SFF/0q45MmltrdNe0QjW+vhF5cEKTWjSuWGAGWRO25r0il2dmfdYPmGbbp+gilkk6hpdnSDrRAsx/Rm/mAlnhjjf+WmSC6K1bVgJZSfO4tmZVq6IX6MpcddnD9KEQoBXsxywKeXU+rlXbQ1k2HgNS7giMirR/3TdJVUbGqxM5abmVSt5ecv62tmtkrcECTukFe922mh9SE0CGvCjLAwyXKYOm2uUbjKxcycyLDZMlDgNyWtsvo0lnkkDt4zIgsVBFXShR5i2ZxAwIDAQAB");
                config.save(configFile);
            }
        } catch (IOException e) {
            System.out.println("Failed to create Kapi configuration file due to IOException");
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }
    
    private void askServerForClass() {
        YamlConfiguration config = getKapiConfig();
        String license = config.getString("license");
        String server = config.getString("server");
        String serverPublicKey = config.getString("publicKey");
        int port = config.getInt("port");
        
        if (license == null || license.isEmpty() || license.equals("PUT YOUR LICENSE KEY HERE")) {
            System.out.println("Kapi license not found! Please set your license key in the kapi.yml file.");
            return;
        }
        userLicense = license;
        
        if (server == null || server.isEmpty()) {
            System.out.println("Kapi server not found! Please set the server IP in the kapi.yml file.");
            return;
        }
        
        try (Socket socket = new Socket(server, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            KeyPair keyPair = CryptoUtils.generateRsaKeyPair().unwrap();
            PrivateKey privateKey = keyPair.getPrivate();
            String publicKey = CryptoUtils.publicKeyToString(keyPair.getPublic()).unwrap();
            String salt = UUID.randomUUID().toString();
            String hashedPublicKey = CryptoUtils.hashString(publicKey + salt);
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            String mac = Base64.getEncoder().encodeToString(ni.getHardwareAddress());
            String payload = salt + ":" + hashedPublicKey + ":" + getKapiDeveloperLicense() + ":" + license + ":"+ mac;
            String encryptedPayload = CryptoUtils.encryptRsa(payload,
                    CryptoUtils.stringToPublicKey(serverPublicKey).unwrap()).unwrap();
            String message = publicKey + ":" + encryptedPayload;
            out.println(message);
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            String[] split = response.toString().split(":");
            String encryptedSecret = split[0];
            String encryptedResponse = split[1];
            String secret = CryptoUtils.decryptRsa(encryptedSecret, privateKey).unwrap();
            split = secret.split(":");
            String ivString = split[0];
            String aesKeyString = split[1];
            IvParameterSpec iv = CryptoUtils.stringToIv(ivString).unwrap();
            SecretKey aesKey = CryptoUtils.stringToAesKey(aesKeyString).unwrapOrThrow();
            String responsePayload = CryptoUtils.decryptAes(encryptedResponse, aesKey, iv).unwrap();
            split = responsePayload.split(":");
            String verifiedResponse = split[0];
            String signedHashedResponse = split[1];
            String hashedResponse = CryptoUtils.verifyRsa(signedHashedResponse,
                    CryptoUtils.stringToPublicKey(serverPublicKey).unwrap()).unwrap();
            if (!CryptoUtils.hashString(verifiedResponse + salt).equals(hashedResponse)) {
                return;
            }
            if (!verifiedResponse.toLowerCase().startsWith("bytecode")) {
                return;
            }
            String base64 = verifiedResponse.substring("bytecode".length());
            byte[] classBytes = Base64.getDecoder().decode(base64);
            ByteClassLoader loader = new ByteClassLoader();
            clazz = loader.defineClass("me.kyren223.kapi.core.KapiInit", classBytes);
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        } catch (Throwable ignored) {
            System.err.println("Error Loading Kapi");
        }
    }
    
    private static class ByteClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
    
    @Override
    public void onEnable() {
        tryLoadingKapi();
        if (clazz == null) {
            System.out.println("Kapi failed to load, error enabling!");
            return;
        }
        try {
            Method method = clazz.getDeclaredMethod("linusTorvalds", Object.class);
            method.setAccessible(true);
            method.invoke(null, this);
            method.setAccessible(false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("KAPI has failed to load! restart the server to retry...");
        }
    }
    
    @Override
    public void onDisable() {
        if (clazz == null) {
            System.out.println("KapiInit not found, error disabling!");
            return;
        }
        try {
            Method method = clazz.getDeclaredMethod("vimMotions");
            method.setAccessible(true);
            method.invoke(null);
            method.setAccessible(false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("Kapi has failed to unload!");
        }
    }
    
    /**
     * The developer's License Key for Kapi.<br>
     * This is the key you received when you purchased Kapi.<br>
     * If this is set to null, the plugin will not be loaded correctly.
     */
    @Kapi
    public abstract String getKapiDeveloperLicense();
    
    /**
     * The name of the plugin, supports String color codes.<br>
     * This is used as a Display Name for the plugin.<br>
     * Should be overridden by any plugin that extends Kplugin.
     *
     * @return The name of the plugin
     */
    @Kapi
    public abstract String getPluginName();
    
    /**
     * Called at the "preload" phase.<br>
     * Used as a replacement to the onEnable method in Bukkit plugins.<br>
     * Recommended to use {@link #onPluginLoad()} instead of this method.<br>
     * <br>
     * The preload phase comes after Kapi has been fully loaded,
     * it's called in the same game tick as the onEnable method.
     */
    @Kapi
    public void onPluginPreload() {
        // Do nothing
    }
    
    /**
     * Called at the "load" phase.<br>
     * This method should be used for initialization of the plugin.<br>
     * {@link #onPluginPreload()} can be used only if absolutely needed, otherwise use this method.<br>
     * <br>
     * The load phase comes after the onEnable method has finished.<br>
     * It'll be called 1 tick after the onEnable method (by using a 1-tick delayed Bukkit Task).<br>
     * This means Kapi has been fully loaded and the {@link #onPluginPreload()} method has been called.
     */
    @Kapi
    public abstract void onPluginLoad();
    
    /**
     * Called at the "unload" phase.<br>
     * Used as a replacement to the onDisable method in Bukkit plugins.<br>
     * <br>
     * The unload phase comes before Kapi unloads itself, so you can still use Kapi methods here.<br>
     * It's called as the first thing in the onDisable method.<br>
     * Immediately after it, Kapi unloads and then the onDisable method finishes.
     */
    @Kapi
    public abstract void onPluginUnload();
    
    /**
     * Gets the instance of the plugin.
     *
     * @return The instance of the plugin
     * @throws IllegalStateException If the plugin has not been enabled yet
     */
    @Kapi
    public static @NotNull Kplugin get() {
        if (instance == null) {
            if (clazz == null) {
                throw new IllegalStateException("KapiInit has not been loaded!");
            }
            
            try {
                Field instanceField = clazz.getDeclaredField("nms");
                instanceField.setAccessible(true);
                instance = (Kplugin) instanceField.get(null);
                instanceField.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                throw new IllegalStateException("Kapi has not been loaded yet!");
            }
        }
        return instance;
    }
    
    /**
     * Enables debug mode for the plugin.<br>
     * See {@link #isDebug()} to check if debug mode is enabled.
     */
    @Kapi
    public void enableDebug() {
        IllegalStateException e = new IllegalStateException("Error enabling debug mode!");
        if (clazz == null) {
            throw e;
        }
        
        try {
            Field debugField = clazz.getField("cpp");
            debugField.setAccessible(true);
            debugField.set(null, true);
            debugField.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            throw e;
        }
    }
    
    /**
     * Disables debug mode for the plugin.<br>
     * See {@link #enableDebug()} to enable debug mode.<br>
     * Note: this method uses reflection, so be aware of the performance overhead.
     *
     * @return true if debug mode was enabled, false otherwise
     */
    @Kapi
    public boolean isDebug() {
        IllegalStateException e = new IllegalStateException("Error checking debug mode!");
        if (clazz == null) {
            throw e;
        }
        
        try {
            Field debugField = clazz.getField("cpp");
            debugField.setAccessible(true);
            boolean debug = (boolean) debugField.get(null);
            debugField.setAccessible(false);
            return debug;
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            throw e;
        }
    }
}