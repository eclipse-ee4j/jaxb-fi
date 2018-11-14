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
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import org.jvnet.fastinfoset.FastInfosetSource;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent extends TransformInputOutput {
    
    public FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent() {
    }
    
    public void parse(InputStream document, OutputStream events, String workingDirectory) throws Exception {
        if (!document.markSupported()) {
            document = new BufferedInputStream(document);
        }
        
        document.mark(4);
        boolean isFastInfosetDocument = Decoder.isFastInfosetDocument(document);
        document.reset();
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMResult dr = new DOMResult();
        
        if (isFastInfosetDocument) {
            t.transform(new FastInfosetSource(document), dr);
        } else if (workingDirectory != null) {
            SAXParser parser = getParser();
            XMLReader reader = parser.getXMLReader();
            reader.setEntityResolver(createRelativePathResolver(workingDirectory));
            SAXSource source = new SAXSource(reader, new InputSource(document));
            
            t.transform(source, dr);
        } else {
            t.transform(new StreamSource(document), dr);
        }
        
        SAXEventSerializer ses = new SAXEventSerializer(events);
        t.transform(new DOMSource(dr.getNode()), new SAXResult(ses));
    }
    
    public void parse(InputStream document, OutputStream events) throws Exception {
        parse(document, events, null);
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
    
    public static void main(String[] args) throws Exception {
        FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent p = new FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent();
        p.parse(args);
    }
    
}
