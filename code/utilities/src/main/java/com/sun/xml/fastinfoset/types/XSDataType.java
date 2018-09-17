/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2012 Oracle and/or its affiliates. All rights reserved.
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

import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import java.util.Locale;
import org.jvnet.fastinfoset.EncodingAlgorithmIndexes;
import org.jvnet.fastinfoset.RestrictedAlphabet;

/**
 * Enumeration of XS data types.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public enum XSDataType {
    _UNSPECIFIED_,
    
    ANYTYPE,
    ANYSIMPLETYPE,
    DURATION(RestrictedAlphabet.DATE_TIME_CHARACTERS),
    DATETIME(RestrictedAlphabet.DATE_TIME_CHARACTERS),
    TIME(RestrictedAlphabet.DATE_TIME_CHARACTERS),
    DATE(RestrictedAlphabet.DATE_TIME_CHARACTERS),
    GYEARMONTH(RestrictedAlphabet.DATE_TIME_CHARACTERS),
    GYEAR(RestrictedAlphabet.DATE_TIME_CHARACTERS),
    GMONTHDAY(RestrictedAlphabet.DATE_TIME_CHARACTERS),
    GDAY(RestrictedAlphabet.DATE_TIME_CHARACTERS),
    GMONTH(RestrictedAlphabet.DATE_TIME_CHARACTERS),
    STRING,
    NORMALIZEDSTRING,
    TOKEN,
    LANGUAGE,
    NAME,
    NCNAME,
    ID,
    IDREF,
    IDREFS,
    ENTITY,
    ENTITIES,
    NMTOKEN,
    NMTOKENS,
    BOOLEAN(EncodingAlgorithmIndexes.BOOLEAN, BuiltInEncodingAlgorithmFactory.booleanEncodingAlgorithm),
    BASE64BINARY(EncodingAlgorithmIndexes.BASE64, BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm),
    HEXBINARY(EncodingAlgorithmIndexes.HEXADECIMAL, BuiltInEncodingAlgorithmFactory.hexadecimalEncodingAlgorithm) ,
    FLOAT(EncodingAlgorithmIndexes.FLOAT, BuiltInEncodingAlgorithmFactory.floatEncodingAlgorithm),
    DECIMAL(RestrictedAlphabet.NUMERIC_CHARACTERS),
    INTEGER(RestrictedAlphabet.NUMERIC_CHARACTERS),
    NONPOSITIVEINTEGER(RestrictedAlphabet.NUMERIC_CHARACTERS),
    NEGATIVEINTEGER(RestrictedAlphabet.NUMERIC_CHARACTERS),
    LONG(EncodingAlgorithmIndexes.LONG, BuiltInEncodingAlgorithmFactory.longEncodingAlgorithm),
    INT(EncodingAlgorithmIndexes.INT, BuiltInEncodingAlgorithmFactory.intEncodingAlgorithm),
    SHORT(EncodingAlgorithmIndexes.SHORT, BuiltInEncodingAlgorithmFactory.shortEncodingAlgorithm),
    BYTE,
    NONNEGATIVEINTEGER(RestrictedAlphabet.NUMERIC_CHARACTERS),
    UNSIGNEDLONG(EncodingAlgorithmIndexes.LONG, BuiltInEncodingAlgorithmFactory.longEncodingAlgorithm),
    UNSIGNEDINT(EncodingAlgorithmIndexes.INT, BuiltInEncodingAlgorithmFactory.intEncodingAlgorithm),
    UNSIGNEDSHORT(EncodingAlgorithmIndexes.SHORT, BuiltInEncodingAlgorithmFactory.shortEncodingAlgorithm),
    UNSIGNEDBYTE,
    POSITIVEINTEGER(RestrictedAlphabet.NUMERIC_CHARACTERS),
    DOUBLE(EncodingAlgorithmIndexes.DOUBLE, BuiltInEncodingAlgorithmFactory.doubleEncodingAlgorithm),
    ANYURI,
    QNAME,
    NOTATION;

    public final int encodingAlgorithmId;
    public final transient BuiltInEncodingAlgorithm encodingAlgorithm;
    
    public final String alphabet;
    
    XSDataType() {
        encodingAlgorithmId = -1;
        encodingAlgorithm = null;
        alphabet = null;
    }
    
    XSDataType(int id, BuiltInEncodingAlgorithm algorithm) {
        encodingAlgorithmId = id;
        encodingAlgorithm = algorithm;
        alphabet = null;
    }
    
    XSDataType(String s) {
        encodingAlgorithmId = -1;
        encodingAlgorithm = null;
        alphabet = s;
    }
    
    public static XSDataType create(String s) {
        try {
            return valueOf(s.toUpperCase(Locale.US));
        } catch (IllegalArgumentException e) {
            return _UNSPECIFIED_;
        }
    }
}
