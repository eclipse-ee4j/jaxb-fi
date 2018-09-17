/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2011 Oracle and/or its affiliates. All rights reserved.
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
package ignore;

import com.sun.xml.fastinfoset.dom.DOMDocumentSerializer;
import com.sun.xml.fastinfoset.org.apache.xerces.util.XMLChar;
import com.sun.xml.fastinfoset.sax.AttributesHolder;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.TestCase;
import org.jvnet.fastinfoset.sax.helpers.FastInfosetDefaultHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class IgnoreTest extends TestCase {

    private static String _notws = "AAAA";
    
    public void testIgnoreSAXSerialization() throws Exception {
        _testIgnoreSAXSerialization(true);
    }
    
    public void testDoNotIgnoreSAXSerialization() throws Exception {
        _testIgnoreSAXSerialization(false);
    }
    
    public void _testIgnoreSAXSerialization(boolean ignore) throws Exception {
        SAXDocumentSerializer ss = new SAXDocumentSerializer();
        AttributesHolder attributes = new AttributesHolder();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ss.setOutputStream(baos);
        
        ss.setIgnoreComments(ignore);
        ss.setIgnoreProcesingInstructions(ignore);        
        ss.setIgnoreWhiteSpaceTextContent(ignore);
        
        ss.startDocument();
        ss.startElement("", "root", "root", attributes);

        ss.startElement("", "e", "e", attributes);
        String comment = "comment";
        ss.comment(comment.toCharArray(), 0, comment.length());
        ss.processingInstruction("target", "data");
        String ws = "    ";
        ss.characters(ws.toCharArray(), 0, ws.length());
        ss.endElement("", "e", "e");

        ss.startElement("", "e", "e", attributes);
        ss.characters(_notws.toCharArray(), 0, _notws.length());
        ss.endElement("", "e", "e");

        ss.endElement("", "root", "root");
        ss.endDocument();
        
        parse(baos, ignore);
    }
    
    public void testIgnoreStAXSerialization() throws Exception {
        _testIgnoreStAXSerialization(true);
    }
    
    public void testDoNotIgnoreStAXSerialization() throws Exception {
        _testIgnoreStAXSerialization(false);
    }
    
    public void _testIgnoreStAXSerialization(boolean ignore) throws Exception {
        StAXDocumentSerializer ss = new StAXDocumentSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ss.setOutputStream(baos);
        
        ss.setIgnoreComments(ignore);
        ss.setIgnoreProcesingInstructions(ignore);        
        ss.setIgnoreWhiteSpaceTextContent(ignore);
        
        ss.writeStartDocument();

        ss.writeStartElement("root");
        
        ss.writeStartElement("e");
        ss.writeComment("comment");
        ss.writeProcessingInstruction("target", "data");
        String ws = "    ";
        ss.writeCharacters(ws);
        ss.writeEndElement();
        
        ss.writeStartElement("e");
        ss.writeCharacters(ws.toCharArray(), 0, ws.length());
        ss.writeEndElement();
        
        ss.writeStartElement("e");
        ss.writeCharacters(_notws);
        ss.writeEndElement();

        ss.writeStartElement("e");
        ss.writeCharacters(_notws.toCharArray(), 0, _notws.length());
        ss.writeEndElement();
        
        ss.writeEndElement();
        ss.writeEndDocument();
        ss.close();
        
        parse(baos, ignore);
    }
    
    public void testIgnoreDOMSerialization() throws Exception {
        _testIgnoreDOMSerialization(true);
    }
    
    public void testDoNotIgnoreDOMSerialization() throws Exception {
        _testIgnoreDOMSerialization(false);
    }
    
    public void _testIgnoreDOMSerialization(boolean ignore) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document d = db.newDocument();
        
        Element root = d.createElement("root");
        d.appendChild(root);
        
        Element e = d.createElement("e");
        root.appendChild(e);
        e.appendChild(d.createComment("comment"));
        e.appendChild(d.createProcessingInstruction("target", "data"));
        e.appendChild(d.createTextNode("    "));
        
        e = d.createElement("e");
        root.appendChild(e);
        e.appendChild(d.createCDATASection("    "));
        
        e = d.createElement("e");
        root.appendChild(e);
        e.appendChild(d.createTextNode(_notws));
        
        DOMDocumentSerializer ds = new DOMDocumentSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ds.setOutputStream(baos);
        
        ds.setIgnoreComments(ignore);
        ds.setIgnoreProcesingInstructions(ignore);        
        ds.setIgnoreWhiteSpaceTextContent(ignore);
        
        ds.serialize(d);
        
        parse(baos, ignore);
    }
    
    public void parse(ByteArrayOutputStream out, boolean ignore) throws Exception {
        SAXDocumentParser sp = new SAXDocumentParser();
        ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());
        
        final boolean assertValue = (ignore) ? false : true;
        
        FastInfosetDefaultHandler h = new FastInfosetDefaultHandler() {
            public void comment(char[] ch, int start, int length) throws SAXException {
                assertTrue(assertValue);
            }

            public void characters (char ch[], int start, int length) throws SAXException {
                if (XMLChar.isSpace(ch[start])) {
                    assertTrue(assertValue);
                } else {
                    String s = new String(ch, start, length);
                    assertEquals(_notws, s);
                }
            }

            public void processingInstruction (String target, String data) throws SAXException {
                assertTrue(assertValue);
            }
        };

        sp.setContentHandler(h);
        sp.setLexicalHandler(h);
        sp.parse(bais);
    }
}
