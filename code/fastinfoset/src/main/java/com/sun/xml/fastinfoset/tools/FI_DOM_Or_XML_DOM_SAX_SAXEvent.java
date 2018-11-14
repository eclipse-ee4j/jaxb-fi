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
import com.sun.xml.fastinfoset.dom.DOMDocumentParser;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.w3c.dom.Document;

public class FI_DOM_Or_XML_DOM_SAX_SAXEvent extends TransformInputOutput {
    
    public void parse(InputStream document, OutputStream events, String workingDirectory) throws Exception {
        if (!document.markSupported()) {
            document = new BufferedInputStream(document);
        }
        
        document.mark(4);
        boolean isFastInfosetDocument = Decoder.isFastInfosetDocument(document);
        document.reset();
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        
        Document d;
        if (isFastInfosetDocument) {
            d = db.newDocument();
            DOMDocumentParser ddp = new DOMDocumentParser();
            ddp.parse(d, document);
        } else {
            if (workingDirectory != null) {
                db.setEntityResolver(createRelativePathResolver(workingDirectory));
            }
            d = db.parse(document);
        }
        
        SAXEventSerializer ses = new SAXEventSerializer(events);
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.transform(new DOMSource(d), new SAXResult(ses));
    }
    
    public void parse(InputStream document, OutputStream events) throws Exception {
        parse(document, events, null);
    }
    
    public static void main(String[] args) throws Exception {
        FI_DOM_Or_XML_DOM_SAX_SAXEvent p = new FI_DOM_Or_XML_DOM_SAX_SAXEvent();
        p.parse(args);
    }
    
}
