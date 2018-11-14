/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.fastinfoset.algorithm;

import com.sun.xml.fastinfoset.EncodingConstants;
import org.jvnet.fastinfoset.EncodingAlgorithmIndexes;

public final class BuiltInEncodingAlgorithmFactory {

    private final static BuiltInEncodingAlgorithm[] table =
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
    
    public static BuiltInEncodingAlgorithm getAlgorithm(int index) {
        return table[index];
    }
}
