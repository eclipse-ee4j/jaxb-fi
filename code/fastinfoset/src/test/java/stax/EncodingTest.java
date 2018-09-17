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
package stax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.TestCase;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;

/**
 * @author Alexey Stashok
 */
public class EncodingTest extends TestCase implements
        XMLStreamConstants {
    
    public void testRoundTripInMemory() throws Exception {
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = new StAXDocumentSerializer(out);
        writer.writeStartDocument();
        
        writer.writeStartElement("Envelope");
        writer.writeStartElement("Body");
        
        writer.writeStartElement("findPerson");
        writer.writeStartElement("person");
        
        writer.writeStartElement("name");
        writer.writeStartElement("first");
        writer.writeCharacters("j");
        writer.writeEndElement();
        writer.writeStartElement("last");
        writer.writeCharacters("smith");
        writer.writeEndElement();
        writer.writeEndElement();
        
        writer.writeStartElement("ssn");
        writer.writeCharacters("123");
        writer.writeEndElement();
        
        writer.writeStartElement("requestor");
        writer.writeCharacters("foo");
        writer.writeEndElement();
        
        writer.writeEndElement();
        writer.writeEndElement();
        
        writer.writeEndElement();
        writer.writeEndElement();
        
        writer.writeEndDocument();
        writer.flush();
        out.flush();
        out.close();
        
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        XMLStreamReader reader = new StAXDocumentParser(in);
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("Envelope", reader.getLocalName());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("Body", reader.getLocalName());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("findPerson", reader.getLocalName());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("person", reader.getLocalName());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("name", reader.getLocalName());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("first", reader.getLocalName());
        assertEquals(CHARACTERS, reader.next());
        assertEquals("j", reader.getText());
        assertEquals(END_ELEMENT, reader.next());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("last", reader.getLocalName());
        assertEquals(CHARACTERS, reader.next());
        assertEquals("smith", reader.getText());
        assertEquals(END_ELEMENT, reader.next());
        
        assertEquals(END_ELEMENT, reader.next()); // </name>
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("ssn", reader.getLocalName());
        assertEquals(CHARACTERS, reader.next());
        assertEquals("123", reader.getText());
        assertEquals(END_ELEMENT, reader.next());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("requestor", reader.getLocalName());
        assertEquals(CHARACTERS, reader.next());
        assertEquals("foo", reader.getText());
        assertEquals(END_ELEMENT, reader.next());
        
        assertEquals(END_ELEMENT, reader.next()); // </person>
        assertEquals(END_ELEMENT, reader.next()); // </findPerson>
        assertEquals(END_ELEMENT, reader.next()); // </Body>
        assertEquals(END_ELEMENT, reader.next()); // </Envelope>
        
        reader.close();
        in.close();
    }

    public void testWriteEndElement() throws Exception {
        String envNS = "http://envelope";
        String bodyNS = "http://body";
        String personNS = "http://person";
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = new StAXDocumentSerializer(out);
        writer.writeStartDocument();
        
        writer.setPrefix("ns1", envNS);
        writer.writeStartElement(envNS, "Envelope");
        writer.writeNamespace("ns1", envNS);
        writer.writeNamespace("ns2", bodyNS);
        writer.writeNamespace("ns3", personNS);
        
        writer.writeStartElement(bodyNS, "Body");
        
        writer.writeStartElement(personNS, "person");
        writer.writeEndElement(); // Close just last element

        writer.writeEndDocument();
        
        writer.flush();
        out.flush();
        out.close();
        
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        XMLStreamReader reader = new StAXDocumentParser(in);
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("Envelope", reader.getLocalName());
        assertEquals(envNS, reader.getNamespaceURI());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("Body", reader.getLocalName());
        assertEquals(bodyNS, reader.getNamespaceURI());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("person", reader.getLocalName());
        assertEquals(personNS, reader.getNamespaceURI());
        
        assertEquals(END_ELEMENT, reader.next()); // </person>
        assertEquals(END_ELEMENT, reader.next()); // </Body>
        assertEquals(END_ELEMENT, reader.next()); // </Envelope>
        
        reader.close();
        in.close();
    }

    public void testNamespaceIssue() throws Exception {
        String envNS = "http://envelope";
        String bodyNS = "http://body";
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = new StAXDocumentSerializer(out);
        writer.writeStartDocument();
        
        writer.writeStartElement("ns1", "Envelope", envNS);
        writer.writeNamespace("ns2", envNS);
        writer.writeNamespace("ns1", envNS);
        
        writer.writeStartElement(envNS, "Envelope");
        writer.writeNamespace("ns1", bodyNS);
        
        
        writer.writeEndElement(); // Close Body
        writer.writeEndElement(); // Close Envelope

        writer.writeEndDocument();
        
        writer.flush();
        out.flush();
        out.close();
        
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        XMLStreamReader reader = new StAXDocumentParser(in);
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("Envelope", reader.getLocalName());
        assertEquals(envNS, reader.getNamespaceURI());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("Envelope", reader.getLocalName());
        assertEquals(envNS, reader.getNamespaceURI());
        
        assertEquals(END_ELEMENT, reader.next()); // </Envelope#2>
        assertEquals(END_ELEMENT, reader.next()); // </Envelope#1>
        
        reader.close();
        in.close();
    }
}


