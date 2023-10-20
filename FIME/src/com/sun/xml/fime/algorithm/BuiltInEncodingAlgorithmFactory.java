/*
 * Copyright (c) 2005, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.fime.algorithm;

import com.sun.xml.fime.EncodingConstants;
import com.sun.xml.fime.jvnet.EncodingAlgorithmIndexes;


public final class BuiltInEncodingAlgorithmFactory {

    public static final BuiltInEncodingAlgorithm[] table =
            new BuiltInEncodingAlgorithm[EncodingConstants.ENCODING_ALGORITHM_BUILTIN_END + 1];

    public static final HexadecimalEncodingAlgorithm hexadecimalEncodingAlgorithm = new HexadecimalEncodingAlgorithm();
    
    public static final BASE64EncodingAlgorithm base64EncodingAlgorithm = new BASE64EncodingAlgorithm();

    public static final BooleanEncodingAlgorithm booleanEncodingAlgorithm = new BooleanEncodingAlgorithm();
    
    public static final ShortEncodingAlgorithm shortEncodingAlgorithm = new ShortEncodingAlgorithm();

    public static final IntEncodingAlgorithm intEncodingAlgorithm = new IntEncodingAlgorithm();

    public static final LongEncodingAlgorithm longEncodingAlgorithm = new LongEncodingAlgorithm();
    
    public static final FloatEncodingAlgorithm floatEncodingAlgorithm = new FloatEncodingAlgorithm();

    public static final DoubleEncodingAlgorithm doubleEncodingAlgorithm = new DoubleEncodingAlgorithm();
    
    public static final UUIDEncodingAlgorithm uuidEncodingAlgorithm = new UUIDEncodingAlgorithm();
    
    static {
        table[EncodingAlgorithmIndexes.HEXADECIMAL] = hexadecimalEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.BASE64] = base64EncodingAlgorithm;
        table[EncodingAlgorithmIndexes.SHORT] = shortEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.INT] = intEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.LONG] = longEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.BOOLEAN] = booleanEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.FLOAT] = floatEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.DOUBLE] = doubleEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.UUID] = uuidEncodingAlgorithm;
    }
}
