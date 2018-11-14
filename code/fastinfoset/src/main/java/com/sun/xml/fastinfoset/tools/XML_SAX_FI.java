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

package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.Reader;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XML_SAX_FI extends TransformInputOutput {
    
    public XML_SAX_FI() {
    }
    
    public void parse(InputStream xml, OutputStream finf, String workingDirectory) throws Exception {
        SAXParser saxParser = getParser();
        SAXDocumentSerializer documentSerializer = getSerializer(finf);
        
        XMLReader reader = saxParser.getXMLReader();
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", documentSerializer);
        reader.setContentHandler(documentSerializer);
        
        if (workingDirectory != null) {
            reader.setEntityResolver(createRelativePathResolver(workingDirectory));
        }
        reader.parse(new InputSource(xml));
    }
    
    public void parse(InputStream xml, OutputStream finf) throws Exception {
        parse(xml, finf, null);
    }
    
    public void convert(Reader reader, OutputStream finf) throws Exception {
        InputSource is = new InputSource(reader);
        
        SAXParser saxParser = getParser();
        SAXDocumentSerializer documentSerializer = getSerializer(finf);
        
        saxParser.setProperty("http://xml.org/sax/properties/lexical-handler", documentSerializer);
        saxParser.parse(is, documentSerializer);
    }
    
    private SAXParser getParser() {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        try {
            return saxParserFactory.newSAXParser();
        } catch (Exception e) {
            return null;
        }
    }
    
    private SAXDocumentSerializer getSerializer(OutputStream finf) {
        SAXDocumentSerializer documentSerializer = new SAXDocumentSerializer();
        documentSerializer.setOutputStream(finf);
        return documentSerializer;
    }
    
    public static void main(String[] args) throws Exception {
        XML_SAX_FI s = new XML_SAX_FI();
        s.parse(args);
    }
}
