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

package org.jvnet.fastinfoset.sax.helpers;

import org.jvnet.fastinfoset.sax.*;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Default base class for SAX event handlers of a {@link FastInfosetReader}.
 * <p>
 * This class is available as a convenience for applications: it provides 
 * default implementations for all of the callbacks of the following:
 * <UL>
 *   <LI>{@link DefaultHandler}</LI>
 *   <LI>{@link LexicalHandler}</LI>
 *   <LI>{@link EncodingAlgorithmContentHandler}</LI>
 *   <LI>{@link PrimitiveTypeContentHandler}</LI>
 * </UL>
 * Application writers can extend this class when they need to implement only 
 * part of an interface; parser writers can instantiate this class to provide
 * default handlers when the application has not supplied its own.
 */
public class FastInfosetDefaultHandler extends DefaultHandler implements
        LexicalHandler, EncodingAlgorithmContentHandler, PrimitiveTypeContentHandler {

    // LexicalHandler
    
    public void comment(char[] ch, int start, int length) throws SAXException {
    }
  
    public void startCDATA() throws SAXException {
    }
  
    public void endCDATA() throws SAXException {
    }
    
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }

    public void endDTD() throws SAXException {
    }
    
    public void startEntity(String name) throws SAXException {
    }

    public void endEntity(String name) throws SAXException {
    }

    
    // EncodingAlgorithmContentHandler
    
    public void octets(String URI, int algorithm, byte[] b, int start, int length)  throws SAXException {
    }

    public void object(String URI, int algorithm, Object o)  throws SAXException {
    }
    
    
    // PrimitiveTypeContentHandler
    
    public void booleans(boolean[] b, int start, int length) throws SAXException {
    }

    public void bytes(byte[] b, int start, int length) throws SAXException {
    }
    
    public void shorts(short[] s, int start, int length) throws SAXException {
    }
    
    public void ints(int[] i, int start, int length) throws SAXException {
    }
    
    public void longs(long[] l, int start, int length) throws SAXException {
    }
    
    public void floats(float[] f, int start, int length) throws SAXException {
    }
    
    public void doubles(double[] d, int start, int length) throws SAXException {
    }

    public void uuids(long[] msblsb, int start, int length) throws SAXException {
    }
}
