/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2004-2005 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.fime.jvnet;

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
