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

import com.sun.xml.fastinfoset.sax.AttributesHolder;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import com.sun.xml.fastinfoset.tools.VocabularyGenerator;
import com.sun.xml.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jvnet.fastinfoset.ExternalVocabulary;
import org.jvnet.fastinfoset.FastInfosetParser;

public class DecodingTest extends TestCase {

    private byte[][] XML_DECLARATION_VALUES;
    
    public static final String FINF_SPEC_UBL_XML_RESOURCE = "X.finf/UBL-example.xml";
    public static final String FINF_SPEC_UBL_FINF_RESOURCE = "X.finf/UBL-example.finf";
    public static final String FINF_SPEC_UBL_FINF_REFVOCAB_RESOURCE = "X.finf/UBL-example-refvocab.finf";
    
    public static final String EXTERNAL_VOCABULARY_URI_STRING = "urn:oasis:names:tc:ubl:Order:1:0:joinery:example";

    private SAXParserFactory _saxParserFactory;
    private SAXParser _saxParser;
    private URL _xmlDocumentURL;
    private URL _finfDocumentURL;
    private URL _finfRefVocabDocumentURL;
        
    /** Creates a new instance of DecodeTestCase */
    public DecodingTest()  throws Exception {
        _saxParserFactory = SAXParserFactory.newInstance();
        _saxParserFactory.setNamespaceAware(true);
        _saxParser = _saxParserFactory.newSAXParser();
        
        _xmlDocumentURL = this.getClass().getClassLoader().getResource(FINF_SPEC_UBL_XML_RESOURCE);
        _finfDocumentURL = this.getClass().getClassLoader().getResource(FINF_SPEC_UBL_FINF_RESOURCE);
        _finfRefVocabDocumentURL = this.getClass().getClassLoader().getResource(FINF_SPEC_UBL_FINF_REFVOCAB_RESOURCE);

        initiateXMLDeclarationValues();
    }

    private void initiateXMLDeclarationValues() {
        XML_DECLARATION_VALUES = new byte[9][];
        
        try {
            XML_DECLARATION_VALUES[0] = "<?xml encoding='finf'?>".getBytes("UTF-8");
            XML_DECLARATION_VALUES[1] = "<?xml version='1.0' encoding='finf'?>".getBytes("UTF-8");
            XML_DECLARATION_VALUES[2] = "<?xml version='1.1' encoding='finf'?>".getBytes("UTF-8");
            XML_DECLARATION_VALUES[3] = "<?xml encoding='finf' standalone='no'?>".getBytes("UTF-8");
            XML_DECLARATION_VALUES[4] = "<?xml encoding='finf' standalone='yes'?>".getBytes("UTF-8");
            XML_DECLARATION_VALUES[5] = "<?xml version='1.0' encoding='finf' standalone='no'?>".getBytes("UTF-8");
            XML_DECLARATION_VALUES[6] = "<?xml version='1.1' encoding='finf' standalone='no'?>".getBytes("UTF-8");
            XML_DECLARATION_VALUES[7] = "<?xml version='1.0' encoding='finf' standalone='yes'?>".getBytes("UTF-8");
            XML_DECLARATION_VALUES[8] = "<?xml version='1.1' encoding='finf' standalone='yes'?>".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(DecodingTest.class);
        return suite;
    }

    public void testDecodeWithVocabulary() throws Exception {
        SerializerVocabulary serializerExternalVocabulary = new SerializerVocabulary();
        ParserVocabulary parserExternalVocabulary = new ParserVocabulary();
        
        VocabularyGenerator vocabularyGenerator = new VocabularyGenerator(serializerExternalVocabulary, parserExternalVocabulary);
        vocabularyGenerator.setCharacterContentChunkSizeLimit(0);
        vocabularyGenerator.setAttributeValueSizeLimit(0);
        _saxParser.parse(_xmlDocumentURL.openStream(), vocabularyGenerator);

        SerializerVocabulary initialVocabulary = new SerializerVocabulary();
        initialVocabulary.setExternalVocabulary(
                EXTERNAL_VOCABULARY_URI_STRING,
                serializerExternalVocabulary, false);

        Map externalVocabularies = new HashMap();
        externalVocabularies.put(EXTERNAL_VOCABULARY_URI_STRING, parserExternalVocabulary);
        
        
        SAXDocumentSerializer documentSerializer = new SAXDocumentSerializer();
        documentSerializer.setMaxCharacterContentChunkSize(6);
        documentSerializer.setMaxAttributeValueSize(6);
        documentSerializer.setVocabulary(initialVocabulary);        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        documentSerializer.setOutputStream(baos);

        SAXDocumentParser documentParser = new SAXDocumentParser();
        documentParser.setProperty(FastInfosetParser.EXTERNAL_VOCABULARIES_PROPERTY, externalVocabularies);
        documentParser.setContentHandler(documentSerializer);
        documentParser.parse(_finfRefVocabDocumentURL.openStream());        

        
        byte[] finfDocument = baos.toByteArray();
        compare(finfDocument, obtainBytesFromStream(_finfRefVocabDocumentURL.openStream()));
    }

    public void testDecodeWithJVNETVocabulary() throws Exception {
        VocabularyGenerator vocabularyGenerator = new VocabularyGenerator();
        vocabularyGenerator.setCharacterContentChunkSizeLimit(0);
        vocabularyGenerator.setAttributeValueSizeLimit(0);
        _saxParser.parse(_xmlDocumentURL.openStream(), vocabularyGenerator);

        ExternalVocabulary ev = new ExternalVocabulary(
                EXTERNAL_VOCABULARY_URI_STRING,
                vocabularyGenerator.getVocabulary());
        
        Map externalVocabularies = new HashMap();
        externalVocabularies.put(ev.URI, ev);
                
        SAXDocumentSerializer documentSerializer = new SAXDocumentSerializer();
        documentSerializer.setMaxCharacterContentChunkSize(6);
        documentSerializer.setMaxAttributeValueSize(6);
        documentSerializer.setExternalVocabulary(ev);        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        documentSerializer.setOutputStream(baos);

        SAXDocumentParser documentParser = new SAXDocumentParser();
        documentParser.setProperty(FastInfosetParser.EXTERNAL_VOCABULARIES_PROPERTY, externalVocabularies);
        documentParser.setContentHandler(documentSerializer);
        documentParser.parse(_finfRefVocabDocumentURL.openStream());        

        
        byte[] finfDocument = baos.toByteArray();
        compare(finfDocument, obtainBytesFromStream(_finfRefVocabDocumentURL.openStream()));
    }
    
    public void testDecodeWithoutVocabulary() throws Exception {
        SerializerVocabulary initialVocabulary = new SerializerVocabulary();

        SAXDocumentSerializer documentSerializer = new SAXDocumentSerializer();
        documentSerializer.setMaxCharacterContentChunkSize(6);
        documentSerializer.setMaxAttributeValueSize(6);
        documentSerializer.setVocabulary(initialVocabulary);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        documentSerializer.setOutputStream(baos);

        SAXDocumentParser documentParser = new SAXDocumentParser();
        documentParser.setContentHandler(documentSerializer);
        documentParser.parse(_finfDocumentURL.openStream());

        
        byte[] finfDocument = baos.toByteArray();
        compare(finfDocument, obtainBytesFromStream(_finfDocumentURL.openStream()));
    }
 
        
    private void compare(byte[] fiDocument, byte[] specFiDocument) throws Exception {
        TestCase.assertTrue("Fast infoset document is not the same length as the X.finf specification", 
            fiDocument.length == specFiDocument.length);
        
        boolean passed = true;
        for (int i = 0; i < fiDocument.length; i++) {
            if (fiDocument[i] != specFiDocument[i]) {
                System.err.println(Integer.toHexString(i) + ": " + 
                        Integer.toHexString(fiDocument[i] & 0xFF) + " " + 
                        Integer.toHexString(specFiDocument[i] & 0xFF));
                passed = false;
            }
        }
        
        assertTrue("Fast infoset document does not have the same content as the X.finf specification",
            passed);

    }

    public void testDecodeWithXMLDeclaration() throws Exception {
        for (int i = 0; i < XML_DECLARATION_VALUES.length; i++) {
            _testDecodeWithXMLDeclaration(XML_DECLARATION_VALUES[i]);
        }
    }
    
    public void _testDecodeWithXMLDeclaration(byte[] header) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(header);
        
        SAXDocumentSerializer sds = new SAXDocumentSerializer();
        sds.setOutputStream(baos);
        
        sds.startDocument();
            sds.startElement("", "element", "element", new AttributesHolder());
                sds.characters(new String("foo").toCharArray(), 0, 3);
            sds.endElement("", "element", "element");
        sds.endDocument();
        
        SAXDocumentParser sdp = new SAXDocumentParser();
        sdp.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
        sdp.parse();
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
