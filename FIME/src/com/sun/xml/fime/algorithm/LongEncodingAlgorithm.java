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


import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import com.sun.xml.fime.jvnet.EncodingAlgorithmException;
import com.sun.xml.fime.util.MessageCenter;


public class LongEncodingAlgorithm extends IntegerEncodingAlgorithm {

    public int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
        if (octetLength % LONG_SIZE != 0) {
            throw new EncodingAlgorithmException(MessageCenter.
                    getString("message.lengthNotMultipleOfLong", new Object[]{new Integer(LONG_SIZE)}));
        }
        
        return octetLength / LONG_SIZE;
    }

    public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
        return primitiveLength * LONG_SIZE;
    }
    
    public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
        long[] data = new long[getPrimtiveLengthFromOctetLength(length)];
        decodeFromBytesToLongArray(data, 0, b, start, length);
        
        return data;
    }
    
    public final Object decodeFromInputStream(InputStream s) throws IOException {
        return decodeFromInputStreamToIntArray(s);
    }
    
    
    public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
        if (!(data instanceof long[])) {
            throw new IllegalArgumentException(MessageCenter.getString("message.dataNotLongArray"));
        }
        
        final long[] ldata = (long[])data;
        
        encodeToOutputStreamFromLongArray(ldata, s);
    }
    
    
    public Object convertFromCharacters(char[] ch, int start, int length) {
        final String cb = new String(ch, start, length);
        final Vector longList = new Vector();
        
        matchWhiteSpaceDelimnatedWords(cb,
                new WordListener() {
            public void word(int start, int end) {
                String lStringValue = cb.substring(start, end);
                longList.addElement(new Long(Long.parseLong(lStringValue)));
            }
        }
        );
        
        return generateArrayFromList(longList);
    }
    
    public void convertToCharacters(Object data, StringBuffer s) {
        if (!(data instanceof long[])) {
            throw new IllegalArgumentException(MessageCenter.getString("message.dataNotLongArray"));
        }
        
        final long[] ldata = (long[])data;
        
        convertToCharactersFromLongArray(ldata, s);
    }
    
    
    public final void decodeFromBytesToLongArray(long[] ldata, int istart, byte[] b, int start, int length) {
        final int size = length / LONG_SIZE;
        for (int i = 0; i < size; i++) {
            ldata[istart++] = 
                    ((long)(b[start++] & 0xFF) << 56) | 
                    ((long)(b[start++] & 0xFF) << 48) | 
                    ((long)(b[start++] & 0xFF) << 40) | 
                    ((long)(b[start++] & 0xFF) << 32) | 
                    ((long)(b[start++] & 0xFF) << 24) | 
                    ((long)(b[start++] & 0xFF) << 16) | 
                    ((long)(b[start++] & 0xFF) << 8) | 
                    (long)(b[start++] & 0xFF);
        }        
    }
    
    public final long[] decodeFromInputStreamToIntArray(InputStream s) throws IOException {
        final Vector longList = new Vector();
        final byte[] b = new byte[LONG_SIZE];
        
        while (true) {
            int n = s.read(b);
            if (n != LONG_SIZE) {
                if (n == -1) {
                    break;
                }
                
                while(n != LONG_SIZE) {
                    final int m = s.read(b, n, LONG_SIZE - n);
                    if (m == -1) {
                        throw new EOFException();
                    }
                    n += m;
                }
            }
            
            final int l = 
                    ((b[0] & 0xFF) << 56) | 
                    ((b[1] & 0xFF) << 48) | 
                    ((b[2] & 0xFF) << 40) | 
                    ((b[3] & 0xFF) << 32) | 
                    ((b[4] & 0xFF) << 24) | 
                    ((b[5] & 0xFF) << 16) | 
                    ((b[6] & 0xFF) << 8) | 
                    (b[7] & 0xFF);
            longList.addElement(new Long(l));
        }
        
        return generateArrayFromList(longList);
    }
    
    
    public final void encodeToOutputStreamFromLongArray(long[] ldata, OutputStream s) throws IOException {
        for (int i = 0; i < ldata.length; i++) {
            final long bits = ldata[i];
            s.write((int)((bits >>> 56) & 0xFF));
            s.write((int)((bits >>> 48) & 0xFF));
            s.write((int)((bits >>> 40) & 0xFF));
            s.write((int)((bits >>> 32) & 0xFF));
            s.write((int)((bits >>> 24) & 0xFF));
            s.write((int)((bits >>> 16) & 0xFF));
            s.write((int)((bits >>> 8) & 0xFF));
            s.write((int)(bits & 0xFF));
        }
    }
    
    public final void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
        encodeToBytesFromLongArray((long[])array, astart, alength, b, start);
    }
    
    public final void encodeToBytesFromLongArray(long[] ldata, int lstart, int llength, byte[] b, int start) {
        final int lend = lstart + llength;
        for (int i = lstart; i < lend; i++) {
            final long bits = ldata[i];
            b[start++] = (byte)((bits >>> 56) & 0xFF);
            b[start++] = (byte)((bits >>> 48) & 0xFF);
            b[start++] = (byte)((bits >>> 40) & 0xFF);
            b[start++] = (byte)((bits >>> 32) & 0xFF);
            b[start++] = (byte)((bits >>> 24) & 0xFF);
            b[start++] = (byte)((bits >>> 16) & 0xFF);
            b[start++] = (byte)((bits >>>  8) & 0xFF);
            b[start++] = (byte)(bits & 0xFF);
        }
    }
    
    
    public final void convertToCharactersFromLongArray(long[] ldata, StringBuffer s) {
        final int end = ldata.length - 1;        
        for (int i = 0; i <= end; i++) {
            s.append(Long.toString(ldata[i]));
            if (i != end) {
                s.append(' ');
            }
        }
    }
    
    
    public final long[] generateArrayFromList(Vector array) {
        long[] ldata = new long[array.size()];
        for (int i = 0; i < ldata.length; i++) {
            ldata[i] = ((Long)array.elementAt(i)).longValue();
        }
        
        return ldata;
    }
}
