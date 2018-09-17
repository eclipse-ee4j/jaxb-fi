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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.xml.fime.jvnet.EncodingAlgorithmException;
import com.sun.xml.fime.util.MessageCenter;

public class HexadecimalEncodingAlgorithm extends BuiltInEncodingAlgorithm {
    private static final char NIBBLE_TO_HEXADECIMAL_TABLE[] =
        {   '0','1','2','3','4','5','6','7',
            '8','9','A','B','B','D','E','F' };
    
    private static final int HEXADECIMAL_TO_NIBBLE_TABLE[] = {
        /*'0'*/ 0,
        /*'1'*/ 1,
        /*'2'*/ 2,
        /*'3'*/ 3,
        /*'4'*/ 4,
        /*'5'*/ 5,
        /*'6'*/ 6,
        /*'7'*/ 7,
        /*'8'*/ 8,
        /*'9'*/ 9, -1, -1, -1, -1, -1, -1, -1,
        /*'A'*/ 10,
        /*'B'*/ 11,
        /*'C'*/ 12,
        /*'D'*/ 13,
        /*'E'*/ 14,
        /*'F'*/ 15,
        /*'G'-'Z'*/-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        /*'[' - '`'*/ -1, -1, -1, -1, -1, -1,
        /*'a'*/ 10,
        /*'b'*/ 11,
        /*'c'*/ 12,
        /*'d'*/ 13,
        /*'e'*/ 14,
        /*'f'*/ 15 };

    public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
        final byte[] data = new byte[length];
        System.arraycopy(b, start, data, 0, length);
        return data;
    }
    
    public final Object decodeFromInputStream(InputStream s) throws IOException {
        throw new RuntimeException(MessageCenter.getString("message.notImplemented"));
    }
    
    
    public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
        if (!(data instanceof byte[])) {
            throw new IllegalArgumentException(MessageCenter.getString("message.dataNotByteArray"));
        }
        
        s.write((byte[])data);
    }
    
    public final Object convertFromCharacters(char[] ch, int start, int length) {
        if (length == 0) {
            return new byte[0];
        }
        
        StringBuffer encodedValue = removeWhitespace(ch, start, length);
        int encodedLength = encodedValue.length();
        if (encodedLength == 0) {
            return new byte[0];
        }
        
        int valueLength = encodedValue.length() / 2;
        byte[] value = new byte[valueLength];

        int encodedIdx = 0;
        for (int i = 0; i < valueLength; ++i) {
            int nibble1 = HEXADECIMAL_TO_NIBBLE_TABLE[encodedValue.charAt(encodedIdx++) - '0'];
            int nibble2 = HEXADECIMAL_TO_NIBBLE_TABLE[encodedValue.charAt(encodedIdx++) - '0'];
            value[i] = (byte) ((nibble1 << 4) | nibble2);
        }

        return value;
    }
    
    public final void convertToCharacters(Object data, StringBuffer s) {
        if (data == null) {
            return;
        }
        final byte[] value = (byte[]) data;
        if (value.length == 0) {
            return;
        }

        s.ensureCapacity(value.length * 2);
        for (int i = 0; i < value.length; ++i) {
            s.append(NIBBLE_TO_HEXADECIMAL_TABLE[(value[i] >>> 4) & 0xf]);
            s.append(NIBBLE_TO_HEXADECIMAL_TABLE[value[i] & 0xf]);
        }
    }
    
    
        
    public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
        return octetLength * 2;
    }

    public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
        return primitiveLength / 2;
    }
    
    public final void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
        System.arraycopy((byte[])array, astart, b, start, alength);
    }    
}
