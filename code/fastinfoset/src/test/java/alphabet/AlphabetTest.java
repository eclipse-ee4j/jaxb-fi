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
package alphabet;

import com.sun.xml.fastinfoset.sax.AttributesHolder;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import com.sun.xml.fastinfoset.util.CharArrayString;
import com.sun.xml.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;
import org.jvnet.fastinfoset.FastInfosetParser;
import org.jvnet.fastinfoset.sax.helpers.FastInfosetDefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class AlphabetTest extends TestCase {
    protected static final String EXTERNAL_VOCABULARY_URI_STRING = "urn:external-vocabulary";
        
    protected AttributesHolder _attributes = new AttributesHolder();
        
    public AlphabetTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws java.lang.Exception {
    }
    
    protected void tearDown() throws java.lang.Exception {
    }
    
    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(AlphabetTest.class);
        
        return suite;
    }
        
    public void testBuiltInAlphabet1() throws Exception {
        String n = "E";
        String d = "Z";
        _testBuiltInAlphabet(n, d);
    }
    
    public void testBuiltInAlphabet2() throws Exception {
        String n = "E ";
        String d = "Z ";
        _testBuiltInAlphabet(n, d);
    }

    public void testBuiltInAlphabet3() throws Exception {
        String n = "E 9";
        String d = "Z 9";
        _testBuiltInAlphabet(n, d);
    }
    
    public void testBuiltInAlphabet4() throws Exception {
        String n = "E 91";
        String d = "Z 91";
        _testBuiltInAlphabet(n, d);
    }
    
    public void testBuiltInAlphabet5() throws Exception {
        String n = "E 912";
        String d = "Z 912";
        _testBuiltInAlphabet(n, d);
    }
    
    public void testBuiltInAlphabet30() throws Exception {
        String n = "0123456789-+.E 0123456789-+.E ";
        String d = "0123456789-:TZ 0123456789-:TZ ";
        _testBuiltInAlphabet(n, d);
    }
    
    public void _testBuiltInAlphabet(String n, String d) throws Exception {
        byte[] b = createBuiltInFastInfosetDocument(n, d);
        InputStream bais = new ByteArrayInputStream(b);
        
        SAXDocumentParser p = new SAXDocumentParser();
        
        BuiltInTestHandler h = new BuiltInTestHandler(n, d);
        
        p.setContentHandler(h);

        p.parse(bais);
    }
    
    protected byte[] createBuiltInFastInfosetDocument(String numericString, String dateTimeString) throws Exception {
        SAXDocumentSerializer s = new SAXDocumentSerializer();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        s.setOutputStream(baos);
        
        _attributes.clear();
                
        s.startDocument();
        
        s.startElement("", "e", "e", _attributes);

        s.startElement("", "numeric", "numeric", _attributes);
        s.numericCharacters(numericString.toCharArray(), 0, numericString.length());
        s.endElement("", "numeric", "numeric");
                
        s.startElement("", "dateTime", "dateTime", _attributes);
        s.dateTimeCharacters(dateTimeString.toCharArray(), 0, dateTimeString.length());
        s.endElement("", "dateTime", "dateTime");
        
        s.endElement("", "e", "e");
        
        s.endDocument();
        
        return baos.toByteArray();
    }
    
    public class BuiltInTestHandler extends FastInfosetDefaultHandler {
        String _localName;
        
        String _numericString;
        
        String _dateTimeString;
       
        public BuiltInTestHandler(String numericString, String dateTimeString) {
            _numericString = numericString;
            _dateTimeString = dateTimeString;
        }
        
        // ContentHandler
        
        public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            _localName = localName;
        }
        
        public final void characters(char[] ch, int start, int length) throws SAXException {
            if (_localName == "numeric") {
                assertEquals(_numericString.length(), length);
                assertEquals(_numericString, new String(ch, start, length));
            } else if (_localName == "dateTime") {
                assertEquals(_dateTimeString.length(), length);
                assertEquals(_dateTimeString, new String(ch, start, length));
            }
        }        
    }    
    
    public void testApplicationNumericAlphabet() throws Exception {
        _testApplicationAlphabet("0123456789-+.E ", "0123456789-+.E 0123456789-+.E ");
    }
    
    public void testApplicationTwoCharAlphabet() throws Exception {
        _testApplicationAlphabet("01", "1");
        _testApplicationAlphabet("01", "0");
        _testApplicationAlphabet("01", "1010101010101010101");
    }

    public void testApplicationThreeCharAlphabet() throws Exception {
        _testApplicationAlphabet("01X", "1");
        _testApplicationAlphabet("01X", "10");
        _testApplicationAlphabet("01X", "01X01X01X01X01X01X01X01X");
    }
    
    public void testApplicationFourCharAlphabet() throws Exception {
        _testApplicationAlphabet("01XY", "0");
        _testApplicationAlphabet("01XY", "Y0");
        _testApplicationAlphabet("01XY", "01XY01XY01XY01XY01XY01XY01XY");
    }
    
    public void testApplicationSixteenCharAlphabet() throws Exception {
        _testApplicationAlphabet("0123456789ABCDEF", "0");
        _testApplicationAlphabet("0123456789ABCDEF", "F0");
        _testApplicationAlphabet("0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF");
    }
    
    public void _testApplicationAlphabet(String a, String s) throws Exception {
        byte[] b = createApplicationFastInfosetDocument(a, s);
        InputStream bais = new ByteArrayInputStream(b);
                
        SAXDocumentParser p = new SAXDocumentParser();
        
        ParserVocabulary externalVocabulary = new ParserVocabulary();
        externalVocabulary.restrictedAlphabet.add(new CharArrayString(a));
        
        Map externalVocabularies = new HashMap();
        externalVocabularies.put(EXTERNAL_VOCABULARY_URI_STRING, externalVocabulary);
        p.setProperty(FastInfosetParser.EXTERNAL_VOCABULARIES_PROPERTY, externalVocabularies);
        
        ApplicationTestHandler h = new ApplicationTestHandler(a, s);
        
        p.setContentHandler(h);

        p.parse(bais);
    }

    protected byte[] createApplicationFastInfosetDocument(String alphabet, String value) throws Exception {
        SAXDocumentSerializer s = new SAXDocumentSerializer();

        SerializerVocabulary externalVocabulary = new SerializerVocabulary();
        externalVocabulary.restrictedAlphabet.add(alphabet);
        
        SerializerVocabulary initialVocabulary = new SerializerVocabulary();
        initialVocabulary.setExternalVocabulary(
                EXTERNAL_VOCABULARY_URI_STRING,
                externalVocabulary, false);
        
        s.setVocabulary(initialVocabulary);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        s.setOutputStream(baos);
        
        _attributes.clear();
                
        s.startDocument();
        
        s.startElement("", "content", "content", _attributes);
        s.alphabetCharacters(alphabet, value.toCharArray(), 0, value.length());
        s.endElement("", "content", "content");
                        
        s.endDocument();
        
        return baos.toByteArray();
    }

    public class ApplicationTestHandler extends FastInfosetDefaultHandler {
        String _alphabet;
        
        String _string;
       
        public ApplicationTestHandler(String alphabet, String string) {
            _alphabet = alphabet;
            _string = string;
        }
        
        // ContentHandler
                
        public final void characters(char[] ch, int start, int length) throws SAXException {
            assertEquals(_string.length(), length);
            assertEquals(_string, new String(ch, start, length));
        }        
    }    
    
}
