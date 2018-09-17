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
package samples.typed;

import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.jvnet.fastinfoset.EncodingAlgorithmIndexes;
import org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import org.jvnet.fastinfoset.sax.helpers.FastInfosetDefaultHandler;
import org.jvnet.fastinfoset.sax.FastInfosetReader;
import org.jvnet.fastinfoset.sax.FastInfosetWriter;
import org.jvnet.fastinfoset.sax.helpers.EncodingAlgorithmAttributesImpl;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Examples of encoding/decoding primitive types as content of an element
 * when using the Fast Infoset SAX serializers and parsers.
 *
 * <p>
 * The {@link org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler} is used 
 * to encode and decode primitive types.
 * @author Paul.Sandoz@Sun.Com
 */
public class PrimitiveTypesWithElementContentSample {

    /**
     * Create an FI document with binary encoded content.
     */
    byte[] createFIDocument() throws SAXException {
        // Instantiate a new FastInfosetWriter
        FastInfosetWriter fiw = new SAXDocumentSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Set the OutputStream to write the document to
        fiw.setOutputStream(baos);
        
        // Create an attributes holder that can hold algorithm data
        EncodingAlgorithmAttributesImpl atts = new EncodingAlgorithmAttributesImpl();
        // Serialize the infoset
        fiw.startDocument();
            fiw.startElement("", "root", "root", null);
                byte[] bytes = {1, 2, 3, 4};
                atts.clear();
                // Add an attirbute value that holds byte[]
                atts.addAttributeWithBuiltInAlgorithmData("", "value", "value", 
                        EncodingAlgorithmIndexes.BASE64, bytes);
                fiw.startElement("", "bytes", "bytes", atts);
                    // Write an array of bytes using the PrimitiveTypeContentHandler
                    fiw.bytes(bytes, 0, bytes.length);
                fiw.endElement("", "bytes", "bytes");
                
                int[] ints = {1, 2, 3, 4};
                atts.clear();
                // Add an attirbute value that holds int[]
                atts.addAttributeWithBuiltInAlgorithmData("", "value", "value", 
                        EncodingAlgorithmIndexes.INT, ints);
                fiw.startElement("", "integers", "integers", atts);
                    // Write an array of integers using the PrimitiveTypeContentHandler
                    fiw.ints(ints, 0, ints.length);
                fiw.endElement("", "integers", "integers");
                
                
                float[] floats = {1.0f, 2.0f, 3.0f, 4.0f};
                atts.clear();
                // Add an attirbute value that holds float[]
                atts.addAttributeWithBuiltInAlgorithmData("", "value", "value", 
                        EncodingAlgorithmIndexes.FLOAT, floats);
                fiw.startElement("", "floats", "floats", atts);
                    // Write an array of floats using the PrimitiveTypeContentHandler
                    fiw.floats(floats, 0, floats.length);
                fiw.endElement("", "floats", "floats");
            fiw.endElement("", "root", "root");
        fiw.endDocument();
        
        return baos.toByteArray();
    }

    /**
     * Parse an FI document using the 
     * {@link org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler}.
     */
    void parseFIDocumentUsingPTC(InputStream in) throws Exception {
        FastInfosetReader r = new SAXDocumentParser();

        // Create a default FI handler that will receive events for primitive types
        FastInfosetDefaultHandler h = new FastInfosetDefaultHandler() {
            
            public void startElement (String uri, String localName, String qName, 
                    Attributes attributes) throws SAXException {
                // Check if the attributes implements the EncodingAlgorithmAttributes interface
                if (attributes instanceof EncodingAlgorithmAttributes) {
                    EncodingAlgorithmAttributes a = (EncodingAlgorithmAttributes)attributes;
                    
                    // Iterate through the attributes
                    for (int index = 0; index < a.getLength(); index++) {
                        // Get the algorithm data
                        Object data = a.getAlgorithmData(index);
                        // If there is binary data then the returned valus will not be null
                        if (data != null) {
                            // Switch on the algorithm used
                            switch (a.getAlgorithmIndex(index)) {
                                case EncodingAlgorithmIndexes.BASE64:
                                    System.out.print("Attribute value as bytes: ");
                                    byte[] bs = (byte[])data;
                                    for (int i = 0; i < bs.length; i++) {
                                        if (i > 0) System.out.print(" ");
                                        System.out.print(bs[i]);
                                    }
                                    System.out.println();
                                    break;
                                case EncodingAlgorithmIndexes.INT:
                                    System.out.print("Attribute value as ints: ");
                                    int[] is = (int[])data;
                                    for (int i = 0; i < is.length; i++) {
                                        if (i > 0) System.out.print(" ");
                                        System.out.print(is[i]);
                                    }
                                    System.out.println();
                                    break;
                                case EncodingAlgorithmIndexes.FLOAT:
                                    System.out.print("Attribute value as floats: ");
                                    float[] fs = (float[])data;
                                    for (int i = 0; i < fs.length; i++) {
                                        if (i > 0) System.out.print(" ");
                                        System.out.print(fs[i]);
                                    }
                                    System.out.println();
                                    break;
                                default:
                            }
                        }
                    }
                }
            }
    
            // Recieve events for an array of byte[]
            public void bytes(byte[] bs, int start, int length) throws SAXException {
                System.out.print("element content as bytes: ");
                for (int i = 0; i < length; i++) {
                    if (i > 0) System.out.print(" ");
                    System.out.print(bs[start + i]);
                }
                System.out.println();
            }

            // Recieve events for an array of int[]
            public void ints(int[] is, int start, int length) throws SAXException {
                System.out.print("element content as ints: ");
                for (int i = 0; i < length; i++) {
                    if (i > 0) System.out.print(" ");
                    System.out.print(is[start + i]);
                }
                System.out.println();
            }

            // Recieve events for an array of float[]
            public void floats(float[] fs, int start, int length) throws SAXException {
                System.out.print("element content as floats: ");
                for (int i = 0; i < length; i++) {
                    if (i > 0) System.out.print(" ");
                    System.out.print(fs[start + i]);
                }
                System.out.println();
            }            
        };
        
        // Set the handlers
        r.setContentHandler(h);
        r.setPrimitiveTypeContentHandler(h);
        
        // Parse the document
        r.parse(in);
    }
    
    /**
     * Parse an FI document using the the {@link org.xml.sax.ContentHandler}
     * <p>
     * Since the {@link org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler}
     * is not used all algorithm data encoded in binary form will be converted
     * to lexical form.
     * <p>
     * In this example the bytes have to be base64 encoded to convery to lexical
     * form.
     */
    void parseFIDocument(InputStream in) throws Exception {
        FastInfosetReader r = new SAXDocumentParser();

        // Create a handler that will receive standard SAX events
        ContentHandler h = new DefaultHandler() {
            public void startElement (String uri, String localName, String qName, 
                    Attributes attributes) throws SAXException {
                for (int index = 0; index < attributes.getLength(); index++) {
                    System.out.println("attribute value as text: " + attributes.getValue(index));
                }
            }
            
            public void characters (char ch[], int start, int length) throws SAXException {
                String s = new String(ch, start, length);
                System.out.println("element content as text: " + s);
            }
        };
        
        r.setContentHandler(h);
        
        r.parse(in);
    }
    
    public static void main(String[] args) throws Exception {
        PrimitiveTypesWithElementContentSample pt = new PrimitiveTypesWithElementContentSample();
        
        // Create an FI document with binary encoded content.
        byte[] b = pt.createFIDocument();
        
        // Parse the document using a PrimitiveTypeContentHandler
        pt.parseFIDocumentUsingPTC(new ByteArrayInputStream(b));
        
        // Parse the document using a ContentHandler
        pt.parseFIDocument(new ByteArrayInputStream(b));
    }
}
