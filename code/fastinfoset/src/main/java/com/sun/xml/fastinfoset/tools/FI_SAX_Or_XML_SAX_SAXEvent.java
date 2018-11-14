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

import com.sun.xml.fastinfoset.Decoder;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.sax.Properties;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FI_SAX_Or_XML_SAX_SAXEvent extends TransformInputOutput {
    
    public FI_SAX_Or_XML_SAX_SAXEvent() {
    }
    
    public void parse(InputStream document, OutputStream events, String workingDirectory) throws Exception {
        if (!document.markSupported()) {
            document = new BufferedInputStream(document);
        }
        
        document.mark(4);
        boolean isFastInfosetDocument = Decoder.isFastInfosetDocument(document);
        document.reset();
        
        if (isFastInfosetDocument) {
            SAXDocumentParser parser = new SAXDocumentParser();
            SAXEventSerializer ses = new SAXEventSerializer(events);
            parser.setContentHandler(ses);
            parser.setProperty(Properties.LEXICAL_HANDLER_PROPERTY, ses);
            parser.parse(document);
        } else {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            SAXParser parser = parserFactory.newSAXParser();
            SAXEventSerializer ses = new SAXEventSerializer(events);
            
            XMLReader reader = parser.getXMLReader();
            reader.setProperty("http://xml.org/sax/properties/lexical-handler", ses);
            reader.setContentHandler(ses);
            if (workingDirectory != null) {
                reader.setEntityResolver(createRelativePathResolver(workingDirectory));
            }
            reader.parse(new InputSource(document));
        }
    }
    
    public void parse(InputStream document, OutputStream events) throws Exception {
        parse(document, events, null);
    }
    
    public static void main(String[] args) throws Exception {
        FI_SAX_Or_XML_SAX_SAXEvent p = new FI_SAX_Or_XML_SAX_SAXEvent();
        p.parse(args);
    }
}
