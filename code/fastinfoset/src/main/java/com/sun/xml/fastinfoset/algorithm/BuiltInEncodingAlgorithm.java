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

import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jvnet.fastinfoset.EncodingAlgorithm;
import org.jvnet.fastinfoset.EncodingAlgorithmException;

public abstract class BuiltInEncodingAlgorithm implements EncodingAlgorithm {
    protected final static Pattern SPACE_PATTERN = Pattern.compile("\\s");

    public abstract int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException;

    public abstract int getOctetLengthFromPrimitiveLength(int primitiveLength);

    public abstract void encodeToBytes(Object array, int astart, int alength, byte[] b, int start);
        
    public interface WordListener {
        public void word(int start, int end);
    }
    
    public void matchWhiteSpaceDelimnatedWords(CharBuffer cb, WordListener wl) {
        Matcher m = SPACE_PATTERN.matcher(cb);
        int i = 0;
        int s = 0;
        while(m.find()) {
            s = m.start();
            if (s != i) {
                wl.word(i, s);
            }
            i = m.end();
        }
        if (i != cb.length())
            wl.word(i, cb.length());
    }
    
    public StringBuilder removeWhitespace(char[] ch, int start, int length) {
        StringBuilder buf = new StringBuilder();
        int firstNonWS = 0;
        int idx = 0;
        for (; idx < length; ++idx) {
            if (Character.isWhitespace(ch[idx + start])) {
                if (firstNonWS < idx) {
                    buf.append(ch, firstNonWS + start, idx - firstNonWS);
                }
                firstNonWS = idx + 1;
            }
        }
        if (firstNonWS < idx) {
            buf.append(ch, firstNonWS + start, idx - firstNonWS);
        }
        return buf;
    }
    
}
