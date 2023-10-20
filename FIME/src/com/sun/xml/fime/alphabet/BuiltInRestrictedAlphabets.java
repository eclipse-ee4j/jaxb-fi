/*
 * Copyright (c) 2005, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.fime.alphabet;

import com.sun.xml.fime.EncodingConstants;
import com.sun.xml.fime.jvnet.RestrictedAlphabet;


public final class BuiltInRestrictedAlphabets {
    public static final char[][] table =
            new char[EncodingConstants.RESTRICTED_ALPHABET_BUILTIN_END + 1][];
    
    static {
        table[RestrictedAlphabet.NUMERIC_CHARACTERS_INDEX] = RestrictedAlphabet.NUMERIC_CHARACTERS.toCharArray();
        table[RestrictedAlphabet.DATE_TIME_CHARACTERS_INDEX] = RestrictedAlphabet.DATE_TIME_CHARACTERS.toCharArray();
    }
}
