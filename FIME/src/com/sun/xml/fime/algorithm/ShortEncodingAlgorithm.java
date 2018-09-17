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

/**
 * An encoder for handling Short values.  Suppports the builtin SHORT encoder.
 *
 * @author Alan Hudson
 * @version
 */
public class ShortEncodingAlgorithm extends IntegerEncodingAlgorithm {

    public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
        if (octetLength % SHORT_SIZE != 0) {
            throw new EncodingAlgorithmException(MessageCenter.
                    getString("message.lengthNotMultipleOfShort", new Object[]{new Integer(SHORT_SIZE)}));
        }

        return octetLength / SHORT_SIZE;
    }

    public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
        return primitiveLength * SHORT_SIZE;
    }

    public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
        short[] data = new short[getPrimtiveLengthFromOctetLength(length)];
        decodeFromBytesToShortArray(data, 0, b, start, length);

        return data;
    }

    public final Object decodeFromInputStream(InputStream s) throws IOException {
        return decodeFromInputStreamToShortArray(s);
    }


    public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
        if (!(data instanceof short[])) {
            throw new IllegalArgumentException(MessageCenter.getString("message.dataNotShortArray"));
        }

        final short[] idata = (short[])data;

        encodeToOutputStreamFromShortArray(idata, s);
    }


    public final Object convertFromCharacters(char[] ch, int start, int length) {
        final String cb = new String(ch, start, length);
        final Vector shortList = new Vector();

        matchWhiteSpaceDelimnatedWords(cb,
                new WordListener() {
            public void word(int start, int end) {
                String iStringValue = cb.substring(start, end);
                shortList.addElement(new Short(Short.parseShort(iStringValue)));
            }
        }
        );

        return generateArrayFromList(shortList);
    }

    public final void convertToCharacters(Object data, StringBuffer s) {
        if (!(data instanceof short[])) {
            throw new IllegalArgumentException(MessageCenter.getString("message.dataNotShortArray"));
        }

        final short[] idata = (short[])data;

        convertToCharactersFromShortArray(idata, s);
    }


    public final void decodeFromBytesToShortArray(short[] sdata, int istart, byte[] b, int start, int length) {
        final int size = length / SHORT_SIZE;
        for (int i = 0; i < size; i++) {
            sdata[istart++] = (short) (((b[start++] & 0xFF) << 8) |
                    (b[start++] & 0xFF));
        }
    }

    public final short[] decodeFromInputStreamToShortArray(InputStream s) throws IOException {
        final Vector shortList = new Vector();
        final byte[] b = new byte[SHORT_SIZE];

        while (true) {
            int n = s.read(b);
            if (n != 2) {
                if (n == -1) {
                    break;
                }

                while(n != 2) {
                    final int m = s.read(b, n, SHORT_SIZE - n);
                    if (m == -1) {
                        throw new EOFException();
                    }
                    n += m;
                }
            }

            final int i = ((b[0] & 0xFF) << 8) |
                    (b[1] & 0xFF);
            shortList.addElement(new Short((short)i));
        }

        return generateArrayFromList(shortList);
    }


    public final void encodeToOutputStreamFromShortArray(short[] idata, OutputStream s) throws IOException {
        for (int i = 0; i < idata.length; i++) {
            final int bits = idata[i];
            s.write((bits >>> 8) & 0xFF);
            s.write(bits & 0xFF);
        }
    }

    public final void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
        encodeToBytesFromShortArray((short[])array, astart, alength, b, start);
    }

    public final void encodeToBytesFromShortArray(short[] sdata, int istart, int ilength, byte[] b, int start) {
        final int iend = istart + ilength;
        for (int i = istart; i < iend; i++) {
            final short bits = sdata[i];
            b[start++] = (byte)((bits >>> 8) & 0xFF);
            b[start++] = (byte)(bits & 0xFF);
        }
    }


    public final void convertToCharactersFromShortArray(short[] sdata, StringBuffer s) {
        final int end = sdata.length - 1;
        for (int i = 0; i <= end; i++) {
            s.append(Integer.toString(sdata[i]));
            if (i != end) {
                s.append(' ');
            }
        }
    }


    public final short[] generateArrayFromList(Vector array) {
        short[] sdata = new short[array.size()];
        for (int i = 0; i < sdata.length; i++) {
            sdata[i] = ((Short)array.elementAt(i)).shortValue();
        }

        return sdata;
    }
}
