/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
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

    public final static BuiltInEncodingAlgorithm[] table =
            new BuiltInEncodingAlgorithm[EncodingConstants.ENCODING_ALGORITHM_BUILTIN_END + 1];

    public final static HexadecimalEncodingAlgorithm hexadecimalEncodingAlgorithm = new HexadecimalEncodingAlgorithm();
    
    public final static BASE64EncodingAlgorithm base64EncodingAlgorithm = new BASE64EncodingAlgorithm();

    public final static BooleanEncodingAlgorithm booleanEncodingAlgorithm = new BooleanEncodingAlgorithm();
    
    public final static ShortEncodingAlgorithm shortEncodingAlgorithm = new ShortEncodingAlgorithm();

    public final static IntEncodingAlgorithm intEncodingAlgorithm = new IntEncodingAlgorithm();

    public final static LongEncodingAlgorithm longEncodingAlgorithm = new LongEncodingAlgorithm();
    
    public final static FloatEncodingAlgorithm floatEncodingAlgorithm = new FloatEncodingAlgorithm();

    public final static DoubleEncodingAlgorithm doubleEncodingAlgorithm = new DoubleEncodingAlgorithm();
    
    public final static UUIDEncodingAlgorithm uuidEncodingAlgorithm = new UUIDEncodingAlgorithm();
    
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
