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
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class should be extended by the main class of your plugin.<br>
 * Extend this instead of {@link JavaPlugin}<br>
 */
@Kapi
@NullMarked
public abstract class Kplugin extends JavaPlugin {
    
    private static @Nullable Class<?> clazz;
    private static @Nullable Kplugin instance;
    public static @Nullable String userLicense;
    
    
    @Override
    public void onEnable() {
        clazz = KpluginHelper.tryLoadingKapi(this, clazz);
        if (clazz == null) {
            Bukkit.getLogger().severe(getPluginName() + ": failed to authenticate with Kapi, error enabling!");
            return;
        }
        try {
            Method method = clazz.getDeclaredMethod("linusTorvalds", Object.class);
            method.setAccessible(true);
            method.invoke(null, this);
            method.setAccessible(false);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            Bukkit.getLogger().severe(getPluginName() + " has failed to load Kapi! try restarting the server");
        } catch (InvocationTargetException e) {
            Bukkit.getLogger().severe("onPluginPreload() generated an exception!");
            e.printStackTrace();
        }
    }
    
    @Override
    public void onDisable() {
        if (clazz == null) {
            Bukkit.getLogger().severe(getPluginName() + ": KapiInit not found, error disabling!");
            return;
        }
        try {
            Method method = clazz.getDeclaredMethod("vimMotions");
            method.setAccessible(true);
            method.invoke(null);
            method.setAccessible(false);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            Bukkit.getLogger().severe(getPluginName() + ": Kapi has failed to unload! error saving!");
        } catch (InvocationTargetException e) {
            Bukkit.getLogger().severe("onPluginUnload() generated an exception!");
            e.printStackTrace();
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
     * The name of the plugin.<br>
     * Must adhere to the following regex:
     * <pre><code>[a-zA-Z_][a-zA-Z0-9_]*</code></pre>
     * <ul>
     *     <li>Starts with a letter or underscore</li>
     *     <li>Contains only letters, numbers, underscores</li>
     * </ul>
     *
     * @return The name of the plugin
     */
    @Kapi
    public abstract String getPluginName();
    
    /**
     * Called at the "preload" phase.<br>
     * Used as a replacement to the onEnable method in Bukkit plugins.<br>
     * For initializations, it's recommended to use {@link #onPluginLoad()} instead of this method.<br>
     * <br>
     * The preload phase comes after Kapi has been fully loaded,
     * it's called in the same game tick as the onEnable method.
     */
    @Kapi
    public abstract void onPluginPreload();
    
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
    public static Kplugin get() {
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
        assert instance != null;
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
