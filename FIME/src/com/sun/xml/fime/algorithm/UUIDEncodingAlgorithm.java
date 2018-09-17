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

import java.util.Vector;

import com.sun.xml.fime.jvnet.EncodingAlgorithmException;
import com.sun.xml.fime.util.MessageCenter;
import com.sun.xml.fime.util.SystemUtil;


public class UUIDEncodingAlgorithm extends LongEncodingAlgorithm {
    
    public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
        if (octetLength % (LONG_SIZE * 2) != 0) {
            throw new EncodingAlgorithmException(MessageCenter.
                    getString("message.lengthNotMultipleOfUUID",new Object[]{new Integer(LONG_SIZE * 2)}));
        }
        
        return octetLength / LONG_SIZE;
    }
    
    public final Object convertFromCharacters(char[] ch, int start, int length) {
        final String cb = new String(ch, start, length);
        final Vector longList = new Vector();
        
        matchWhiteSpaceDelimnatedWords(cb,
                new WordListener() {
            public void word(int start, int end) {
                String uuidValue = cb.substring(start, end);
                fromUUIDString(uuidValue);
                longList.addElement(new Long(_msb));
                longList.addElement(new Long(_lsb));
            }
        }
        );
        
        return generateArrayFromList(longList);
    }
    
    public final void convertToCharacters(Object data, StringBuffer s) {
        if (!(data instanceof long[])) {
            throw new IllegalArgumentException(MessageCenter.getString("message.dataNotLongArray"));
        }
        
        final long[] ldata = (long[])data;

        final int end = ldata.length - 1;
        for (int i = 0; i <= end; i += 2) {
            s.append(toUUIDString(ldata[i], ldata[i + 1]));
            if (i != end) {
                s.append(' ');
            }
        }
    }    

    
    private long _msb;
    private long _lsb;
    
    final void fromUUIDString(String name) {
        String[] components = SystemUtil.split(name, "-");
        if (components.length != 5)
            throw new IllegalArgumentException(MessageCenter.
                    getString("message.invalidUUID", new Object[]{name}));
                    
        for (int i=0; i<5; i++)
            components[i] = "0x"+components[i];

        _msb = Long.parseLong(components[0], 16);
        _msb <<= 16;
        _msb |= Long.parseLong(components[1], 16);
        _msb <<= 16;
        _msb |= Long.parseLong(components[2], 16);

        _lsb = Long.parseLong(components[3], 16);
        _lsb <<= 48;
        _lsb |= Long.parseLong(components[4], 16);
    }

    final String toUUIDString(long msb, long lsb) {
	return (digits(msb >> 32, 8) + "-" +
		digits(msb >> 16, 4) + "-" +
		digits(msb, 4) + "-" +
		digits(lsb >> 48, 4) + "-" +
		digits(lsb, 12));
    }
    
    final String digits(long val, int digits) {
	long hi = 1L << (digits * 4);
    long value = hi | (val & (hi - 1));
    //    return Long.toHexString().substring(1);
    // TODO split a long value to two integers.
    int higher = (int) (value >> 32);
    String higherPart;
    if (higher == 0) {
        higherPart = "";
    } else {
        higherPart = Integer.toHexString(higher);
    }
    int lower = (int) (value & ((1L << 32) - 1)); 
    String lowerPart = "0000000" + Integer.toHexString(lower);
    lowerPart = lowerPart.substring(lowerPart.length() - 8);
    String fullDigits = higherPart + lowerPart;
	return fullDigits.substring(1);
    }
    
}

