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
package encoding;

import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import com.sun.xml.fastinfoset.tools.VocabularyGenerator;
import com.sun.xml.fastinfoset.util.KeyIntMap;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import junit.framework.*;
import org.jvnet.fastinfoset.ExternalVocabulary;

public class EncodingTest extends TestCase {
    
    public static final String FINF_SPEC_UBL_XML_RESOURCE = "X.finf/UBL-example.xml";
    public static final String FINF_SPEC_UBL_FINF_RESOURCE = "X.finf/UBL-example.finf";
    public static final String FINF_SPEC_UBL_FINF_REFVOCAB_RESOURCE = "X.finf/UBL-example-refvocab.finf";
    
    public static final String EXTERNAL_VOCABULARY_URI_STRING = "urn:oasis:names:tc:ubl:Order:1:0:joinery:example";
    
    private SAXParserFactory _saxParserFactory;
    private SAXParser _saxParser;
    private URL _xmlDocumentURL;
    private URL _finfDocumentURL;
    private URL _finfRefVocabDocumentURL;
    
    private byte[] _finfDocument;
    
    private SAXDocumentSerializer _ds;
    private SerializerVocabulary _initialVocabulary;
    
    public EncodingTest(java.lang.String testName) throws Exception {
        super(testName);
        
        _saxParserFactory = SAXParserFactory.newInstance();
        _saxParserFactory.setNamespaceAware(true);
        _saxParser = _saxParserFactory.newSAXParser();
        
        _xmlDocumentURL = this.getClass().getClassLoader().getResource(FINF_SPEC_UBL_XML_RESOURCE);
        _finfDocumentURL = this.getClass().getClassLoader().getResource(FINF_SPEC_UBL_FINF_RESOURCE);
        _finfRefVocabDocumentURL = this.getClass().getClassLoader().getResource(FINF_SPEC_UBL_FINF_REFVOCAB_RESOURCE);
        
        _ds = new SAXDocumentSerializer();
        _ds.setMaxCharacterContentChunkSize(6);
        _ds.setMaxAttributeValueSize(6);
        _initialVocabulary = new SerializerVocabulary();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(EncodingTest.class);
        return suite;
    }
    
    public void testEncodeWithVocabulary() throws Exception {
        SerializerVocabulary externalVocabulary = new SerializerVocabulary();
        
        VocabularyGenerator vocabularyGenerator = new VocabularyGenerator(externalVocabulary);
        vocabularyGenerator.setCharacterContentChunkSizeLimit(0);
        vocabularyGenerator.setAttributeValueSizeLimit(0);
        _saxParser.parse(_xmlDocumentURL.openStream(), vocabularyGenerator);
        
        _initialVocabulary.setExternalVocabulary(
                EXTERNAL_VOCABULARY_URI_STRING,
                externalVocabulary, false);
        
        _finfDocument = parse();
        FileOutputStream foas = new FileOutputStream("new-UBL-example-refvocab.finf");
        foas.write(_finfDocument);
        
        compare(obtainBytesFromStream(_finfRefVocabDocumentURL.openStream()));
    }
    
    public void testEncodeWithJVNETVocabulary() throws Exception {
        VocabularyGenerator vocabularyGenerator = new VocabularyGenerator();
        vocabularyGenerator.setCharacterContentChunkSizeLimit(0);
        vocabularyGenerator.setAttributeValueSizeLimit(0);
        _saxParser.parse(_xmlDocumentURL.openStream(), vocabularyGenerator);
        
        ExternalVocabulary ev = new ExternalVocabulary(
                EXTERNAL_VOCABULARY_URI_STRING,
                vocabularyGenerator.getVocabulary());
        _ds.setExternalVocabulary(ev);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        _ds.setOutputStream(baos);
        
        _saxParser.parse(_xmlDocumentURL.openStream(), _ds);
        
        _finfDocument = baos.toByteArray();
        
        FileOutputStream foas = new FileOutputStream("new-UBL-example-refvocab.finf");
        foas.write(_finfDocument);
        
        compare(obtainBytesFromStream(_finfRefVocabDocumentURL.openStream()));
    }
    
    public void testEncodeWithoutVocabulary() throws Exception {
        _finfDocument = parse();
        FileOutputStream foas = new FileOutputStream("new-UBL-example.finf");
        foas.write(_finfDocument);
        
        compare(obtainBytesFromStream(_finfDocumentURL.openStream()));
    }
    
    public void testEncodeWithMemoryLimitation() throws Exception {
        int memoryLimitation = 20;
        int charsLimitation = memoryLimitation / 2;
        SerializerVocabulary voc = _initialVocabulary;
        try {
            _ds.setAttributeValueMapMemoryLimit(memoryLimitation);
            _ds.setCharacterContentChunkMapMemoryLimit(memoryLimitation);
            
            voc.clear();
            parse();
            assertTrue(voc.characterContentChunk.getTotalCharacterCount() < charsLimitation);
            assertTrue(voc.otherString.getTotalCharacterCount() < charsLimitation);
            assertTrue(voc.attributeValue.getTotalCharacterCount() < charsLimitation);
            assertTrue(voc.characterContentChunk.obtainIndex("236WV".toCharArray(), 0, 5, false) != KeyIntMap.NOT_PRESENT);
            assertTrue(voc.attributeValue.obtainIndex("unit") != KeyIntMap.NOT_PRESENT);
            assertFalse(voc.characterContentChunk.obtainIndex("wood".toCharArray(), 0, 4, false) != KeyIntMap.NOT_PRESENT);
            
            _ds.setAttributeValueMapMemoryLimit(2);
            voc.clear();
            parse();
            assertFalse(voc.attributeValue.obtainIndex("unit") != KeyIntMap.NOT_PRESENT);
        } finally {
            _ds.setAttributeValueMapMemoryLimit(Integer.MAX_VALUE);
            _ds.setCharacterContentChunkMapMemoryLimit(Integer.MAX_VALUE);
        }
    }
    
    private byte[] parse() throws Exception {
        _ds.setVocabulary(_initialVocabulary);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        _ds.setOutputStream(baos);
        
        _saxParser.parse(_xmlDocumentURL.openStream(), _ds);
        
        return baos.toByteArray();
    }
    
    private void compare(byte[] specFiDocument) throws Exception {
        TestCase.assertTrue("Fast infoset document is not the same length as the X.finf specification",
                _finfDocument.length == specFiDocument.length);
        
        System.out.println(_finfDocument.length);
        System.out.println(specFiDocument.length);
        boolean passed = true;
        for (int i = 0; i < _finfDocument.length; i++) {
            if (_finfDocument[i] != specFiDocument[i]) {
                System.err.println(Integer.toHexString(i) + ": " +
                        Integer.toHexString(_finfDocument[i] & 0xFF) + " " +
                        Integer.toHexString(specFiDocument[i] & 0xFF));
                passed = false;
            }
        }
        
        assertTrue("Fast infoset document does not have the same content as the X.finf specification",
                passed);
        
    }
    
    static byte[] obtainBytesFromStream(InputStream s) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        byte[] buffer = new byte[1024];
        
        int bytesRead = 0;
        while ((bytesRead = s.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }
}
