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

import org.apache.mirae.util.CharacterUtil;

import com.sun.xml.fime.jvnet.EncodingAlgorithm;
import com.sun.xml.fime.jvnet.EncodingAlgorithmException;


public abstract class BuiltInEncodingAlgorithm implements EncodingAlgorithm {
//    protected final static Pattern SPACE_PATTERN = Pattern.compile("\\s");

    public abstract int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException;

    public abstract int getOctetLengthFromPrimitiveLength(int primitiveLength);

    public abstract void encodeToBytes(Object array, int astart, int alength, byte[] b, int start);
        
    public interface WordListener {
        public void word(int start, int end);
    }
    
    public void matchWhiteSpaceDelimnatedWords(String cb, WordListener wl) {
        // TODO implement pattern matching
//        Matcher m = SPACE_PATTERN.matcher(cb);
//        int i = 0;
//        while(m.find()) {
//            int s = m.start();
//            if (s != i) {
//                wl.word(i, s);
//            }
//            i = m.end();
//        }
        int i = 0;
        for (int s = 0; s < cb.length(); s++) {
            if (!CharacterUtil.isWhitespace(cb.charAt(s))) {
                continue;
            }
            if (s != i) {
                wl.word(i, s);
            }
            i = s + 1;
        }    
    }
    
    public StringBuffer removeWhitespace(char[] ch, int start, int length) {
        StringBuffer buf = new StringBuffer();
        int firstNonWS = 0;
        int idx = 0;
        for (; idx < length; ++idx) {
            if (CharacterUtil.isWhitespace(ch[idx])) {
                if (firstNonWS < idx) {
                    buf.append(ch, firstNonWS, idx - firstNonWS);
                }
                firstNonWS = idx + 1;
            }
        }
        if (firstNonWS < idx) {
            buf.append(ch, firstNonWS, idx - firstNonWS);
        }
        return buf;
    }
    
}
