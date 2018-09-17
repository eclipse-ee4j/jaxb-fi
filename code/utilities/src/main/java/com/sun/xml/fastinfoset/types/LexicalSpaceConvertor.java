/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sun.xml.fastinfoset.types;

import java.util.Set;

/**
 * Convertor from lexical space to value space.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class LexicalSpaceConvertor {
    
    public static enum LexicalPreference {
        string, charArray
    };
    
    public static Object convertToValueSpace(Set<XSDataType> types, String s,
            LexicalPreference preference) {
        return convertToValueSpace(types, s.toCharArray(), 0, s.length(), preference);
    }
    
    public static Object convertToValueSpace(Set<XSDataType> types, char[] ch, int start, int length,
            LexicalPreference preference) {
        if (types == null) return null;
        
        // Iterate through all possible types
        for (XSDataType dt : types) {
            try {
                if (dt.encodingAlgorithm != null) {
                    Object value = dt.encodingAlgorithm.
                            convertFromCharacters(ch, start, length);

                    /*
                     * Check if only one boolean value, convert to "1" or "0"
                     * and return lexical representation.
                     */
                    if (dt == XSDataType.BOOLEAN) {
                        boolean[] b = (boolean[])value;
                        if (b.length == 1) {
                            String s = new String(ch, start, length).trim();
                            if (s.equalsIgnoreCase("true")) {
                                return (preference == LexicalPreference.string) ?
                                    "1" : "1".toCharArray();
                            } else if (s.equalsIgnoreCase("false")) {
                                return (preference == LexicalPreference.string) ?
                                    "0" : "0".toCharArray();
                            }
                            return null;
                        }
                    }
                    return new ValueInstance(dt.encodingAlgorithmId, value);
                } else if (dt.alphabet != null) {
                    ch = validateCharactersWithAlphabet(dt.alphabet, ch, start, length);
                    // Do not use alohabet if lexical representation contains few characters
                    if (ch != null && ch.length > 2)
                        return (preference == LexicalPreference.string) ?
                            new ValueInstance(dt.alphabet, new String(ch)) :
                            new ValueInstance(dt.alphabet, ch);
                    else
                        return (preference == LexicalPreference.string) ?
                            new String(ch) : ch;
                } else {
                    if (dt == XSDataType.ANYURI) {
                        return (preference == LexicalPreference.string) ?
                            new ValueInstance(new String(ch, start, length)) :
                            new ValueInstance(new String(ch, start, length).toCharArray());
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
                // If conversion failed try the next type
            }
        }

        // Could not convert from lexical space to value space
        return null;
    }
    
    private static char[] validateCharactersWithAlphabet(String alphabet, char[] ch, int start, int length) {
        ch = new String(ch, start, length).trim().toCharArray();
        for (char c : ch)
            if (alphabet.indexOf(c) == -1)
                return null;

        return ch;
    }   
}