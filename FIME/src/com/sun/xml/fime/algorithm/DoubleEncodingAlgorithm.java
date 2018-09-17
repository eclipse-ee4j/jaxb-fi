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

public class DoubleEncodingAlgorithm extends IEEE754FloatingPointEncodingAlgorithm {

    public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
        if (octetLength % DOUBLE_SIZE != 0) {
            throw new EncodingAlgorithmException(MessageCenter.
                    getString("message.lengthIsNotMultipleOfDouble", new Object[]{new Integer(DOUBLE_SIZE)}));
        }
        
        return octetLength / DOUBLE_SIZE;
    }
    
    public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
        return primitiveLength * DOUBLE_SIZE;
    }
   
    public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
        double[] data = new double[getPrimtiveLengthFromOctetLength(length)];
        decodeFromBytesToDoubleArray(data, 0, b, start, length);
        
        return data;
    }
    
    public final Object decodeFromInputStream(InputStream s) throws IOException {
        return decodeFromInputStreamToDoubleArray(s);
    }
    
    
    public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
        if (!(data instanceof double[])) {
            throw new IllegalArgumentException(MessageCenter.getString("message.dataNotDouble"));
        }
        
        final double[] fdata = (double[])data;
        
        encodeToOutputStreamFromDoubleArray(fdata, s);
    }
    
    public final Object convertFromCharacters(char[] ch, int start, int length) {
        final String cb = new String(ch, start, length);
        final Vector doubleList = new Vector();
        
        matchWhiteSpaceDelimnatedWords(cb,
                new WordListener() {
            public void word(int start, int end) {
                String fStringValue = cb.substring(start, end);
                doubleList.addElement(Float.valueOf(fStringValue));
            }
        }
        );
        
        return generateArrayFromList(doubleList);
    }
    
    public final void convertToCharacters(Object data, StringBuffer s) {
        if (!(data instanceof double[])) {
            throw new IllegalArgumentException(MessageCenter.getString("message.dataNotDouble"));
        }
        
        final double[] fdata = (double[])data;
        
        convertToCharactersFromDoubleArray(fdata, s);
    }
    
    
    public final void decodeFromBytesToDoubleArray(double[] data, int fstart, byte[] b, int start, int length) {
        final int size = length / DOUBLE_SIZE;
        for (int i = 0; i < size; i++) {
            final long bits =
                    ((long)(b[start++] & 0xFF) << 56) | 
                    ((long)(b[start++] & 0xFF) << 48) | 
                    ((long)(b[start++] & 0xFF) << 40) | 
                    ((long)(b[start++] & 0xFF) << 32) | 
                    ((long)(b[start++] & 0xFF) << 24) | 
                    ((long)(b[start++] & 0xFF) << 16) | 
                    ((long)(b[start++] & 0xFF) << 8) | 
                    (long)(b[start++] & 0xFF);
            data[fstart++] = Double.longBitsToDouble(bits);
        }
    }
    
    public final double[] decodeFromInputStreamToDoubleArray(InputStream s) throws IOException {
        final Vector doubleList = new Vector();
        final byte[] b = new byte[DOUBLE_SIZE];
        
        while (true) {
            int n = s.read(b);
            if (n != DOUBLE_SIZE) {
                if (n == -1) {
                    break;
                }
                
                while(n != DOUBLE_SIZE) {
                    final int m = s.read(b, n, DOUBLE_SIZE - n);
                    if (m == -1) {
                        throw new EOFException();
                    }
                    n += m;
                }
            }
            
            final int bits = 
                    ((b[0] & 0xFF) << 56) | 
                    ((b[1] & 0xFF) << 48) | 
                    ((b[2] & 0xFF) << 40) | 
                    ((b[3] & 0xFF) << 32) | 
                    ((b[4] & 0xFF) << 24) | 
                    ((b[5] & 0xFF) << 16) | 
                    ((b[6] & 0xFF) << 8) | 
                    (b[7] & 0xFF);
            
            doubleList.addElement(new Double(Double.longBitsToDouble(bits)));
        }
        
        return generateArrayFromList(doubleList);
    }
    
    
    public final void encodeToOutputStreamFromDoubleArray(double[] fdata, OutputStream s) throws IOException {
        for (int i = 0; i < fdata.length; i++) {
            final long bits = Double.doubleToLongBits(fdata[i]);
            s.write((int)((bits >>> 56) & 0xFF));
            s.write((int)((bits >>> 48) & 0xFF));
            s.write((int)((bits >>> 40) & 0xFF));
            s.write((int)((bits >>> 32) & 0xFF));
            s.write((int)((bits >>> 24) & 0xFF));
            s.write((int)((bits >>> 16) & 0xFF));
            s.write((int)((bits >>>  8) & 0xFF));
            s.write((int)(bits & 0xFF));
        }
    }
    
    public final void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
        encodeToBytesFromDoubleArray((double[])array, astart, alength, b, start);
    }
    
    public final void encodeToBytesFromDoubleArray(double[] fdata, int fstart, int flength, byte[] b, int start) {
        final int fend = fstart + flength;
        for (int i = fstart; i < fend; i++) {
            final long bits = Double.doubleToLongBits(fdata[i]);
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
    
    
    public final void convertToCharactersFromDoubleArray(double[] fdata, StringBuffer s) {
        final int end = fdata.length - 1;
        for (int i = 0; i <= end; i++) {
            s.append(Double.toString(fdata[i]));
            if (i != end) {
                s.append(' ');
            }
        }
    }
    
    
    public final double[] generateArrayFromList(Vector array) {
        double[] fdata = new double[array.size()];
        for (int i = 0; i < fdata.length; i++) {
            fdata[i] = ((Double)array.elementAt(i)).doubleValue();
        }
        
        return fdata;
    }
    
}
