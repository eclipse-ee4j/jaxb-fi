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

import java.util.ArrayList;
import java.util.List;
import java.nio.CharBuffer;
import org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.fastinfoset.CommonResourceBundle;

public class UUIDEncodingAlgorithm extends LongEncodingAlgorithm {
    
    public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
        if (octetLength % (LONG_SIZE * 2) != 0) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().
                    getString("message.lengthNotMultipleOfUUID",new Object[]{Integer.valueOf(LONG_SIZE * 2)}));
        }
        
        return octetLength / LONG_SIZE;
    }
    
    public final Object convertFromCharacters(char[] ch, int start, int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final List longList = new ArrayList();
        
        matchWhiteSpaceDelimnatedWords(cb,
                new WordListener() {
            public void word(int start, int end) {
                String uuidValue = cb.subSequence(start, end).toString();
                fromUUIDString(uuidValue);
                longList.add(Long.valueOf(_msb));
                longList.add(Long.valueOf(_lsb));
            }
        }
        );
        
        return generateArrayFromList(longList);
    }
    
    public final void convertToCharacters(Object data, StringBuffer s) {
        if (!(data instanceof long[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotLongArray"));
        }
        
        final long[] ldata = (long[])data;

        final int end = ldata.length - 2;
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
        String[] components = name.split("-");
        if (components.length != 5)
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().
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
	return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }
    
}
