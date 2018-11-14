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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface EncodingAlgorithm {
    
    public Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException;
    
    public Object decodeFromInputStream(InputStream s) throws EncodingAlgorithmException, IOException;
    

    public void encodeToOutputStream(Object data, OutputStream s) throws EncodingAlgorithmException, IOException;
    
    
    public Object convertFromCharacters(char[] ch, int start, int length) throws EncodingAlgorithmException;
    
    public void convertToCharacters(Object data, StringBuffer s) throws EncodingAlgorithmException;
    
}
