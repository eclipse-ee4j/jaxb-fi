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

public class MessageCenter {
    public static String getString(String key, Object[] values) {
        // TODO externalization
        StringBuffer value = new StringBuffer(key);
        value.append("-");
        for (int i = 0; i < values.length; i++) {
            value.append(values[i]);
            value.append(",");
        }
        return value.toString();
    }

    public static String getString(String string) {
        // TODO externalization
        return string;
    }
}
