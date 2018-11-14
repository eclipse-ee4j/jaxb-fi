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

package org.jvnet.fastinfoset;

/** 
 * The indexes of built-in encoding algorithms.
 *
 * <p>The indexes of the built-in encoding algorithms are specified
 * in ITU-T Rec. X.891 | ISO/IEC 24824-1 (Fast Infoset), clause
 * 10. The indexes start from 0 instead of 1 as specified.<p>
 *
 * @see org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler
 * @see org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes
 */
public final class EncodingAlgorithmIndexes {
    public static final int HEXADECIMAL = 0;
    public static final int BASE64      = 1;
    public static final int SHORT       = 2;
    public static final int INT         = 3;
    public static final int LONG        = 4;
    public static final int BOOLEAN     = 5;
    public static final int FLOAT       = 6;
    public static final int DOUBLE      = 7;
    public static final int UUID        = 8;
    public static final int CDATA       = 9;
}
