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

import com.sun.xml.fastinfoset.algorithm.BASE64EncodingAlgorithm;
import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import junit.framework.TestCase;

/**
 * @author Alexey Stashok
 */
public class OctetEncodingTest extends TestCase implements
        XMLStreamConstants {
    
    public void testSmallChunkedOctets() throws Exception {
        String envNS = "http://envelope";
        String[] texts = new String[] {"sdf===ertS", "S", "I", "N", "G", "L", "E", "", "DDFFEdsfsdf23432423"};
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StAXDocumentSerializer serializer = new StAXDocumentSerializer(out);
        XMLStreamWriter writer = serializer;
        writer.writeStartDocument();
        
        writer.setPrefix("ns1", envNS);
        writer.writeStartElement(envNS, "Envelope");
        writer.writeNamespace("ns1", envNS);
        StringBuffer patternBuffer = new StringBuffer();
        for(String s : texts) {
            byte[] buf = s.getBytes();
            serializer.writeOctets(buf, 0, buf.length);
            patternBuffer.append(s);
        }
        writer.writeEndElement(); // Close just last element
        
        writer.writeEndDocument();
        
        writer.flush();
        out.flush();
        out.close();
        
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        XMLStreamReader reader = new StAXDocumentParser(in);
        
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("Envelope", reader.getLocalName());
        
        BASE64EncodingAlgorithm base64Decoder = new BASE64EncodingAlgorithm();
        StringBuffer bufToTest = new StringBuffer();
        while(reader.next() == CHARACTERS) {
            char[] charBuf = reader.getText().toCharArray();
            String decodedStr = new String((byte[]) base64Decoder.convertFromCharacters(charBuf, 0, charBuf.length));
            bufToTest.append(decodedStr);
        }
        assertEquals(patternBuffer.toString(), bufToTest.toString());
        
        assertEquals(END_ELEMENT, reader.getEventType()); // </Envelope>
        
        reader.close();
        in.close();
    }
    
    public void testBigChunkedOctets() throws Exception {
        String envNS = "http://envelope";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StAXDocumentSerializer serializer = new StAXDocumentSerializer(out);
        XMLStreamWriter writer = serializer;
        writer.writeStartDocument();
        
        writer.setPrefix("ns1", envNS);
        writer.writeStartElement(envNS, "Envelope");
        writer.writeNamespace("ns1", envNS);
        StringBuffer patternBuffer = new StringBuffer();
        for(int i = 0; i < 5; i++) {
            String s = createRandomString(2000 + i * 100);
            byte[] buf = s.getBytes();
            serializer.writeOctets(buf, 0, buf.length);
            patternBuffer.append(s);
        }
        
        writer.writeEndElement(); // Close just last element
        
        writer.writeEndDocument();
        
        writer.flush();
        out.flush();
        out.close();
        
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        XMLStreamReader reader = new StAXDocumentParser(in);
        
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("Envelope", reader.getLocalName());
        
        BASE64EncodingAlgorithm base64Decoder = new BASE64EncodingAlgorithm();
        StringBuffer bufToTest = new StringBuffer();
        while(reader.next() == CHARACTERS) {
            char[] charBuf = reader.getText().toCharArray();
            String decodedStr = new String((byte[]) base64Decoder.convertFromCharacters(charBuf, 0, charBuf.length));
            bufToTest.append(decodedStr);
        }
        assertEquals(patternBuffer.toString(), bufToTest.toString());
        
        assertEquals(END_ELEMENT, reader.getEventType()); // </Envelope>
        
        reader.close();
        in.close();
    }
    
    private String createRandomString(int length) {
        StringBuffer sb = new StringBuffer(length);
        Random random = new Random();
        for(int i=0; i<length; i++) {
            sb.append((char) (random.nextInt(20) + 'a'));
        }
        
        return sb.toString();
    }
}
