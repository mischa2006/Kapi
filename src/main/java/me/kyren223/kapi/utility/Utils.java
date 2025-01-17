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
import me.kyren223.kapi.data.Option;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A utility class that includes a bunch of useful methods.
 */
@Kapi
@NullMarked
public class Utils {
    
    private Utils() {
        throw new AssertionError("Utils should not be instantiated");
    }
    
    /**
     * Formats the string to have colors.<br>
     * Supports the following color codes:
     * <ul>
     *     <li>&amp; - converts to {@link ChatColor#COLOR_CHAR} which is '§' </li>
     *     <li>#[a-fA-F0-9]{6} - converts to hex color codes</li>
     *     <li>&amp;# - Converts to '#', used to escape hex</li>
     * </ul>
     *
     * @param s The string to color
     * @return The colored string
     */
    @Kapi
    @Contract(pure = true)
    public static String col(String s) {
        int skip = 0;
        StringBuilder sb = new StringBuilder();
        outer:
        for (int i = 0; i < s.length(); i++) {
            if (skip > 0) {
                skip--;
                continue;
            }
            
            char c = s.charAt(i);
            Option<Character> nextC = i + 1 < s.length() ? Option.some(s.charAt(i + 1)) : Option.none();
            
            if (nextC.isSomeAnd(next -> next == '#' && c == '&')) {
                // Escape # after & into just #
                sb.append('#');
                skip = 1; // Skip the next character which is the #
                continue;
            }
            
            if (c == '&') {
                sb.append(ChatColor.COLOR_CHAR);
                continue;
            }
            
            if (c == '#') {
                // Try parsing the hex color
                char[] hex = new char[6];
                for (int j = 0; j < 6; j++) {
                    if (i + j + 1 >= s.length()) {
                        sb.append(c);
                        continue outer;
                    }
                    char next = s.charAt(i + j + 1);
                    boolean isDigit = Character.isDigit(next);
                    boolean isHex = 'a' <= next && next <= 'f' || 'A' <= next && next <= 'F';
                    if (!isDigit && !isHex) {
                        sb.append(c);
                        continue outer;
                    }
                    hex[j] = s.charAt(i + j + 1);
                }
                String color = net.md_5.bungee.api.ChatColor.of("#" + new String(hex)) + "";
                sb.append(color);
            }
            
            sb.append(c);
        }
        return sb.toString();
    }
    
    /**
     * Checks if a regex is valid.<br>
     * Note: may impact performance if used frequently on invalid regexes
     * due to using exceptions for validation.
     *
     * @param regex The regex to check
     * @return True if the regex is valid, false otherwise
     */
    @Kapi
    @Contract(pure = true)
    public static boolean isValidRegex(String regex) {
        try {
            Pattern.compile(regex);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }
}
