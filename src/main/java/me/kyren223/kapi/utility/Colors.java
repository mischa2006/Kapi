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
import org.bukkit.Color;
import org.jspecify.annotations.NullMarked;

/**
 * A utility class that includes a bunch of predefined colors.<br>
 * For the built-in bukkit colors, see {@link org.bukkit.Color}.<br>
 */
@Kapi
@NullMarked
public class Colors {
    
    private Colors() {
        // Prevent instantiation
    }
    
    private static Color bukkit(java.awt.Color color) {
        return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }
    
    @Kapi
    public static final Color DARK_GREEN = bukkit(new java.awt.Color(16, 107, 16));
    @Kapi
    public static final Color DARK_RED = bukkit(new java.awt.Color(120, 1, 1));
    @Kapi
    public static final Color DARK_BLUE = bukkit(new java.awt.Color(1, 1, 120));
    @Kapi
    public static final Color DARK_PURPLE = bukkit(new java.awt.Color(91, 1, 91));
    @Kapi
    public static final Color DARK_AQUA = bukkit(new java.awt.Color(1, 120, 120));
    @Kapi
    public static final Color KYRENS_COLOR = bukkit(new java.awt.Color(0, 241, 255));
    
}
