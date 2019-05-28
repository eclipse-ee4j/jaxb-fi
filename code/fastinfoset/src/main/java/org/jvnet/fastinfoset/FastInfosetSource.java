/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2019 Oracle and/or its affiliates. All rights reserved.
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

import java.io.InputStream;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.sun.xml.fastinfoset.sax.SAXDocumentParser;

/**
 *  A JAXP Source implementation that supports the parsing fast
 *  infoset document for use by applications that expect a Source.
 *
 *  <P>The derivation of FISource from SAXSource is an implementation
 *  detail.<P>
 *
 *  <P>This implementation is designed for interoperation with JAXP and is not
 *  not designed with performance in mind. It is recommended that for performant
 *  interoperation alternative parser specific solutions be used.<P>
 *
 *  <P>Applications shall obey the following restrictions:
 *   <UL>
 *     <LI>The setXMLReader and setInputSource shall not be called.</LI>
 *     <LI>The XMLReader object obtained by the getXMLReader method shall
 *        be used only for parsing the InputSource object returned by
 *        the getInputSource method.</LI>
 *     <LI>The InputSource object obtained by the getInputSource method shall
 *        be used only for being parsed by the XMLReader object returned by
 *        the getXMLReader method.</LI>
 *   </UL>
 */
public class FastInfosetSource extends SAXSource {

    public FastInfosetSource(InputStream inputStream) {
        super(new InputSource(inputStream));
    }

    public XMLReader getXMLReader() {
        XMLReader reader = super.getXMLReader();
        if (reader == null) {
            reader = new SAXDocumentParser();
            setXMLReader(reader);
        }
        ((SAXDocumentParser) reader).setInputStream(getInputStream());
        return reader;
    }

    public InputStream getInputStream() {
        return getInputSource().getByteStream();
    }

    public void setInputStream(InputStream inputStream) {
        setInputSource(new InputSource(inputStream));
    }
}
