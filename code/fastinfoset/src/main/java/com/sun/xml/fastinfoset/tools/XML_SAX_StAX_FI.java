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

import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XML_SAX_StAX_FI extends TransformInputOutput {
    
    public XML_SAX_StAX_FI() {
    }
    
    public void parse(InputStream xml, OutputStream finf, String workingDirectory) throws Exception {
        StAXDocumentSerializer documentSerializer = new StAXDocumentSerializer();
        documentSerializer.setOutputStream(finf);
        
        SAX2StAXWriter saxTostax = new SAX2StAXWriter(documentSerializer);
        
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        SAXParser saxParser = saxParserFactory.newSAXParser();
        
        XMLReader reader = saxParser.getXMLReader();
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", saxTostax);
        reader.setContentHandler(saxTostax);
        
        if (workingDirectory != null) {
            reader.setEntityResolver(createRelativePathResolver(workingDirectory));
        }
        reader.parse(new InputSource(xml));
        
        xml.close();
        finf.close();
    }
    
    public void parse(InputStream xml, OutputStream finf) throws Exception {
        parse(xml, finf, null);
    }
    
    public static void main(String[] args) throws Exception {
        XML_SAX_StAX_FI s = new XML_SAX_StAX_FI();
        s.parse(args);
    }
    
}
