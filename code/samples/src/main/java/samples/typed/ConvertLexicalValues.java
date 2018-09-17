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

import com.sun.xml.analysis.types.SchemaProcessor;
import com.sun.xml.fastinfoset.types.XSDataType;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.jvnet.fastinfoset.EncodingAlgorithmIndexes;
import org.jvnet.fastinfoset.FastInfosetSource;
import org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import org.jvnet.fastinfoset.sax.helpers.EncodingAlgorithmAttributesImpl;
import org.jvnet.fastinfoset.sax.helpers.FastInfosetDefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Example of converting lexical values to primitive types for encoding
 * in binary form.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class ConvertLexicalValues {
        
    static class LexicalFilter extends XMLFilterImpl {
        private Map<String, Set<XSDataType>> _elements;
        private Map<String, Set<XSDataType>> _attributes;
        private Set<XSDataType> _textContent;
        
        public LexicalFilter(Map<String, Set<XSDataType>> elements, 
                Map<String, Set<XSDataType>> attributes) {
            _elements = elements;
            _attributes = attributes;
        }
        
        /*
         * Replace an the lexical value attribute with primitive data based
         * on the set of XS data type associated with the local name of the
         * attribute.
         */
        private void replaceAttributeValue(Set<XSDataType> attributeTypes, 
                int index, EncodingAlgorithmAttributesImpl atts) {
            if (attributeTypes == null) return;
            
            char[] ch = atts.getValue(index).toCharArray();
            // Iterate through all possible types
            for (XSDataType dt : attributeTypes) {
                try {
                    // Switch on the type
                    switch(dt) {
                        case BASE64BINARY:
                            // Convert the lexical value to byte[]
                            byte[] b = (byte[])BuiltInEncodingAlgorithmFactory.
                                    base64EncodingAlgorithm.
                                    convertFromCharacters(ch, 0, ch.length);
                            // Replace the attribute value
                            atts.replaceWithAttributeAlgorithmData(index, 
                                    null, EncodingAlgorithmIndexes.BASE64, b);
                            return;
                        case FLOAT:
                            // Convert the lexical value to float[]
                            float[] f = (float[])BuiltInEncodingAlgorithmFactory.
                                    floatEncodingAlgorithm.
                                    convertFromCharacters(ch, 0, ch.length);
                            // Replace the attribute value
                            atts.replaceWithAttributeAlgorithmData(index, 
                                    null, EncodingAlgorithmIndexes.FLOAT, f);
                            return;
                        default:
                    }
                } catch (Exception e) {
                }
            }
            
        }
        
        public void startElement(String uri, String localName, String qName, 
                Attributes atts) throws SAXException {
            // Obtain the XS types associated with the local name of this 
            // element
            _textContent = _elements.get(localName);
            
            for (int i = 0; i < atts.getLength(); i++) {
                // Check if one or more attributes has a simple type associated with it
                if (_attributes.containsKey(atts.getLocalName(i))) {
                    // Copy the attributes
                    final EncodingAlgorithmAttributesImpl eatts = new EncodingAlgorithmAttributesImpl(atts);
                    
                    // Replace attribute lexical value with primitive types.
                    for (i = 0; i < atts.getLength(); i++) {
                        replaceAttributeValue(_attributes.get(atts.getLocalName(i)),
                                i, eatts);
                    }
                    
                    // Replace attributes passed to parent
                    atts = eatts;
                    break;
                }
            }

            // Pass event to parent
            super.startElement(uri, localName, qName, atts);
        }
        
        public void characters (char ch[], int start, int length)
            throws SAXException {
            if (_textContent == null) {
                /*
                 * If there are no types associated with element 
                 * use the lexical value and defer to the parent.
                 */
                super.characters(ch, start, length);
                return;
            }

            // Iterate through all possible types
            for (XSDataType dt : _textContent) {
                try {
                    // Switch on the type
                    switch(dt) {
                        case BASE64BINARY:
                            // Convert the lexical value to byte[]
                            byte[] b = (byte[])BuiltInEncodingAlgorithmFactory.
                                    base64EncodingAlgorithm.
                                    convertFromCharacters(ch, start, length);
                            // Directly call the PrimitiveTypeContentHandler
                            ((PrimitiveTypeContentHandler)getContentHandler()).
                                    bytes(b, 0, b.length);
                            _textContent = null;
                            return;
                        case FLOAT:
                            // Convert the lexical value to float[]
                            float[] f = (float[])BuiltInEncodingAlgorithmFactory.
                                    floatEncodingAlgorithm.
                                    convertFromCharacters(ch, start, length);
                            // Directly call the PrimitiveTypeContentHandler
                            ((PrimitiveTypeContentHandler)getContentHandler()).
                                    floats(f, 0, f.length);
                            _textContent = null;
                            return;
                        default:
                    }
                } catch (Exception e) {
                    // If conversion failed try the next type
                }
            }
    
            /*
             * If all conversions failed use the lexical value
             * and defer to the parent.
             */
            super.characters(ch, start, length);
        }
    }
        
    /**
     * Parse an XML document and convert to an FI document while using the schema
     * to convert lexical values that are base64 or floating point values to
     * binary representations.
     * <p>
     * Arg 0 is the path to the schema (XSD or RNG).
     * <p>
     * Arg 1 is the path to the XML document to be converted.
     * <p>
     */
    public static void main(String[] args) throws Exception {
        /**
         * Process the schema to obtain a mapping of attribute/element
         * local name to a set of XS data types.
         */
        SchemaProcessor sp = new SchemaProcessor(new File(args[0]).toURL());
        sp.process();
        
        // Create an instance of the SAX document serializer
        SAXDocumentSerializer s = new SAXDocumentSerializer();
        // Set the location of where the fast infoset document will be written
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        s.setOutputStream(baos);
        
        /*
         * Create a lexical filter whose parent is a JAXP XMLReader.
         * Pass in the element/attribute local name to XS data type
         * maps to the constructor.
         */
        LexicalFilter lf = new LexicalFilter(sp.getElementToXSDataTypeMap(), 
                sp.getAttributeToXSDataTypeMap());
        lf.setContentHandler(s);
        lf.setParent(getXMLReader());
        // Parse the XML document to create a fast infoset document
        lf.parse(new InputSource(new FileInputStream(args[1])));

        /*
         * Transform the fast infoset document to an XML document
         * using a FastInfosetSource and the transform API.
         */
        FastInfosetSource source = new FastInfosetSource(
                new ByteArrayInputStream(baos.toByteArray()));
        StreamResult result = new StreamResult(System.out);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.transform(source, result);
        System.out.println();
        
        /*
         * Parse the fast infoset document using a SAX document
         * parser. Register a PrimitiveContentHandler to access
         * binary data as arrays of primitives.
         */
        SAXDocumentParser p = new SAXDocumentParser();
        FastInfosetDefaultHandler h = new FastInfosetDefaultHandler() {
            public void bytes(byte[] b, int start, int length) 
            throws SAXException {
                System.out.println("Byte: " + b[start]);
            }

            public void floats(float[] f, int start, int length) 
            throws SAXException {
                System.out.println("Float: " + f[start]);
            }
        };
        p.setContentHandler(h);
        p.setPrimitiveTypeContentHandler(h);
        p.parse(new ByteArrayInputStream(baos.toByteArray()));
    }
    
    public static XMLReader getXMLReader() throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        return spf.newSAXParser().getXMLReader();
    }
}
