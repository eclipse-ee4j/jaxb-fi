/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.fime.util;

import java.util.Vector;

public class SystemUtil {
    public static String getProperty(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
        
    }
    
    public static String getBooleanString(boolean value) {
        if (value) {
            return "true";
        } else {
            return "false";
        }
    }
    
    public static boolean getBooleanValue(String value) {
        if (value.equals("true")) {
            return true;
        } else {
            return false;
        }
    }
    
    public static void fill(byte[] a, int fromIndex, int toIndex, byte val) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                       ") > toIndex(" + toIndex+")");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > a.length) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
        for (int i = fromIndex; i < toIndex; i++) {
            a[i] = val;
        }
    }

    public static boolean isWhitespace(char ch) {
        int c = (int) ch;
        // SPACE_SEPARATOR
        // Note that 202F, 00A0, 2007 are excluded because they are non-break
        // ones.
        if ((c == 0x0020) || (c == 0x1680) || (c == 0x180E)
                || ((c >= 0x2000) && (c <= 0x2006))
                || ((c >= 0x2008) && (c <= 0x200B)) || (c == 0x205F)
                || (c == 0x3000)) {
            return true;
        }
        // LINE_SEPARATOR
        if (c == 0x2028) {
            return true;
        }
        // PARAGRAPH_SEPARATOR
        if (c == 0x2029) {
            return true;
        }
        if (((c >= 0x0009) && (c <= 0x000D))
                || ((c >= 0x001C) && (c <= 0x001F))) {
            return true;
        }
        return false;
    }

    public static String[] split(String target, String pattern) {
        // TODO implement splitting
        int start = 0;
        int index = 0;
        int length = pattern.length();
        Vector splitList = new Vector();
        while ((index = target.indexOf(pattern, start)) >= 0) {
            String split = target.substring(start, index);
            splitList.addElement(split);
            start = index + length;
            // "a b c" by " "
            // index = 1 length =1
            // next start = 2
        }
        String[] splits = new String[splitList.size()];
        for (int i = 0; i < splits.length; i++) {
            splits[i] = (String) splitList.elementAt(i);
        }
        return splits;
    }
}
