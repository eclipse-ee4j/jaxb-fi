/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dom;

import com.sun.xml.fastinfoset.dom.DOMDocumentSerializer;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Alexey Stashok
 */
public class NamespaceTest extends TestCase {
    public void testWithoutNamespace() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
        try {
            dbf.setFeature(FEATURE, true);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("ParserConfigurationException was thrown. The feature '"
                + FEATURE + "' is not supported by your XML processor.", e);
        }
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element e = doc.createElement("ABC");
        root.appendChild(e);
        e = doc.createElement("ABC");
        root.appendChild(e);
        
        DOMDocumentSerializer ds = new DOMDocumentSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ds.setOutputStream(baos);
        
        ds.serialize(doc);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        SAXDocumentParser sp = new SAXDocumentParser();
        
        sp.parse(bais);
        
        assertTrue(true);
    }
    
    public void testNamespace() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
        try {
            dbf.setFeature(FEATURE, true);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("ParserConfigurationException was thrown. The feature '"
                + FEATURE + "' is not supported by your XML processor.", e);
        }
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElementNS("http://www.xxx-root.org", "ABC:root");
        doc.appendChild(root);
        Element e = doc.createElementNS("http://www.xxx.org", "ABC:e");
        root.appendChild(e);
        e = doc.createElementNS("http://www.xxx.org", "ABC:e");
        root.appendChild(e);
        
        DOMDocumentSerializer ds = new DOMDocumentSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ds.setOutputStream(baos);
        
        ds.serialize(doc);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        SAXDocumentParser sp = new SAXDocumentParser();
        
        sp.parse(bais);
        
        assertTrue(true);
    }
    
    public void testNestedNamespace() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
        try {
            dbf.setFeature(FEATURE, true);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("ParserConfigurationException was thrown. The feature '"
                + FEATURE + "' is not supported by your XML processor.", e);
        }
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElementNS("http://www.xxx.org", "ABC:root");
        doc.appendChild(root);
        Element e = doc.createElementNS("http://www.xxx.org", "ABC:e");
        root.appendChild(e);
        e = doc.createElementNS("http://www.xxx.org", "ABC:e");
        root.appendChild(e);
        
        DOMDocumentSerializer ds = new DOMDocumentSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ds.setOutputStream(baos);
        
        ds.serialize(doc);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        SAXDocumentParser sp = new SAXDocumentParser();
        
        sp.parse(bais);
        
        assertTrue(true);
    }
}
