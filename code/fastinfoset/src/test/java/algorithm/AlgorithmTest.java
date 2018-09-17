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
package algorithm;

import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.algorithm.BASE64EncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.DoubleEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.FloatEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.IntEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.LongEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.ShortEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.BooleanEncodingAlgorithm;
import com.sun.xml.fastinfoset.dom.DOMDocumentParser;
import com.sun.xml.fastinfoset.sax.AttributesHolder;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamReader;
import junit.framework.*;
import org.jvnet.fastinfoset.EncodingAlgorithmIndexes;
import org.jvnet.fastinfoset.FastInfosetParser;
import org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import org.jvnet.fastinfoset.sax.helpers.FastInfosetDefaultHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class AlgorithmTest extends TestCase {
    protected static final int ARRAY_SIZE = 8;
    protected static final int APPLICATION_DEFINED_ALGORITHM_ID = 32;
    protected static final String APPLICATION_DEFINED_ALGORITHM_URI = "algorithm-32";
    protected static final String EXTERNAL_VOCABULARY_URI_STRING = "urn:external-vocabulary";

    protected String _base64String = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygp" +
            "KissLS4vMDEyMzQ1Njc4OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJT" +
            "VFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6e3x9" +
            "fn+AgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaan" +
            "qKmqq6ytrq+wsbKztLW2t7i5uru8vb6/wMHCw8TFxsfIycrLzM3Oz9DR" +
            "0tPU1dbX2Nna29zd3t/g4eLj5OXm5+jp6uvs7e7v8PHy8/T19vf4+fr7" +
            "/P3+/w==";

    protected AttributesHolder _attributes = new AttributesHolder();

    protected byte[] _byteArray;
    protected short[] _shortArray;
    protected int[] _intArray;
    protected long[] _longArray;
    protected float[] _floatArray;
    protected double[] _doubleArray;
    protected long[] _uuidArray;
	protected boolean[] _booleanArray;

    protected String _cdata = new String("CDATA characters");
    protected String _characters = new String("characters");

    public AlgorithmTest(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(AlgorithmTest.class);

        return suite;
    }

    public void testBase64AlgorithmNoZerosNoWhiteSpace() throws Exception {
        byte[] data = new byte[9];
        _testBase64Algorithm("AAAAAAAAAAAA", "AAAAAAAAAAAA", data);
    }

    public void testBase64AlgorithmNoZerosWhiteSpace() throws Exception {
        byte[] data = new byte[9];
        _testBase64Algorithm("   AAA AA AAA  AAAA    ", "AAAAAAAAAAAA", data);
    }

    public void testBase64Algorithm() throws Exception {
        byte[] data = new byte[256];
        for (int i = 0; i < 256; i++) {
            data[i] = (byte)i;
        }

        _testBase64Algorithm(_base64String, _base64String, data);
    }

    public void _testBase64Algorithm(String base64Characters, String base64CharactersNoWS, byte[] b) throws Exception {
        BASE64EncodingAlgorithm b64ea = new BASE64EncodingAlgorithm();

        byte[] data = (byte[])b64ea.convertFromCharacters(
                base64Characters.toCharArray(), 0, base64Characters.length());

        assertEquals(b.length, data.length);
        for (int i = 0; i < data.length; i++) {
            assertEquals(b[i], data[i]);
        }

        StringBuffer s = new StringBuffer();
        b64ea.convertToCharacters(data, s);
        assertEquals(base64CharactersNoWS.length(), s.length());
        assertEquals(base64CharactersNoWS, s.toString());
    }

    public void testShortAlgorithm() throws Exception {
        createArrayValues(ARRAY_SIZE);

        byte[] b = new byte[ARRAY_SIZE * 2];

        ShortEncodingAlgorithm sea = new ShortEncodingAlgorithm();
        sea.encodeToBytesFromShortArray(_shortArray, 0, ARRAY_SIZE, b, 0);

        short[] i = new short[ARRAY_SIZE];
        sea.decodeFromBytesToShortArray(i, 0, b, 0, b.length);

        for (int is = 0; is < ARRAY_SIZE; is++) {
            assertEquals(_shortArray[is], i[is]);
        }
    }

    public void testBooleanAlgorithm1() throws Exception {
        _testBooleanAlgorithm(1);
    }
    
    public void testBooleanAlgorithm2() throws Exception {
        _testBooleanAlgorithm(2);
    }
    
    public void testBooleanAlgorithm3() throws Exception {
        _testBooleanAlgorithm(3);
    }
    
    public void testBooleanAlgorithm4() throws Exception {
        _testBooleanAlgorithm(4);
    }
    
    public void testBooleanAlgorithm5() throws Exception {
        _testBooleanAlgorithm(5);
    }
    
    public void testBooleanAlgorithm6() throws Exception {
        _testBooleanAlgorithm(6);
    }
    
    public void testBooleanAlgorithm7() throws Exception {
        _testBooleanAlgorithm(7);
    }
    
    public void testBooleanAlgorithm8() throws Exception {
        _testBooleanAlgorithm(8);
    }

    public void testBooleanAlgorithm9() throws Exception {
        _testBooleanAlgorithm(9);
    }
    
    public void testBooleanAlgorithm10() throws Exception {
        _testBooleanAlgorithm(10);
    }
    
    public void testBooleanAlgorithm11() throws Exception {
        _testBooleanAlgorithm(11);
    }
    
    public void testBooleanAlgorithm12() throws Exception {
        _testBooleanAlgorithm(12);
    }
    
    public void testBooleanAlgorithm32() throws Exception {
        _testBooleanAlgorithm(32);
    }
    
    public void _testBooleanAlgorithm(int size) throws Exception {
        createArrayValues(size);

        BooleanEncodingAlgorithm bea = new BooleanEncodingAlgorithm();
        byte[] b = new byte[bea.getOctetLengthFromPrimitiveLength(size)];
        bea.encodeToBytesFromBooleanArray(_booleanArray, 0, size, b, 0);

        int bsize = bea.getPrimtiveLengthFromOctetLength(b.length, b[0]);
        assertEquals(size, bsize);
        boolean[] i = new boolean[bsize];
        bea.decodeFromBytesToBooleanArray(i, 0, bsize, b, 0, b.length);

        for (int is = 0; is < size; is++) {
            assertEquals(_booleanArray[is], i[is]);
        }
    }
    
    public void testIntAlgorithm() throws Exception {
        createArrayValues(ARRAY_SIZE);

        byte[] b = new byte[ARRAY_SIZE * 4];

        IntEncodingAlgorithm iea = new IntEncodingAlgorithm();
        iea.encodeToBytesFromIntArray(_intArray, 0, ARRAY_SIZE, b, 0);

        int[] i = new int[ARRAY_SIZE];
        iea.decodeFromBytesToIntArray(i, 0, b, 0, b.length);

        for (int is = 0; is < ARRAY_SIZE; is++) {
            assertEquals(_intArray[is], i[is]);
        }
    }

    public void testLongAlgorithm() throws Exception {
        createArrayValues(ARRAY_SIZE);

        byte[] b = new byte[ARRAY_SIZE * 8];

        LongEncodingAlgorithm iea = new LongEncodingAlgorithm();
        iea.encodeToBytesFromLongArray(_longArray, 0, ARRAY_SIZE, b, 0);

        long[] i = new long[ARRAY_SIZE];
        iea.decodeFromBytesToLongArray(i, 0, b, 0, b.length);

        for (int is = 0; is < ARRAY_SIZE; is++) {
            assertEquals(_longArray[is], i[is]);
        }
    }

    public void testFloatAlgorithm() throws Exception {
        createArrayValues(ARRAY_SIZE);

        byte[] b = new byte[ARRAY_SIZE * 4];

        FloatEncodingAlgorithm fea = new FloatEncodingAlgorithm();
        fea.encodeToBytesFromFloatArray(_floatArray, 0, ARRAY_SIZE, b, 0);

        float[] f = new float[ARRAY_SIZE];
        fea.decodeFromBytesToFloatArray(f, 0, b, 0, b.length);

        for (int is = 0; is < ARRAY_SIZE; is++) {
            assertEquals(_floatArray[is], f[is], 0.0);
        }
    }

    public void testDoubleAlgorithm() throws Exception {
        createArrayValues(ARRAY_SIZE);

        byte[] b = new byte[ARRAY_SIZE * 8];

        DoubleEncodingAlgorithm fea = new DoubleEncodingAlgorithm();
        fea.encodeToBytesFromDoubleArray(_doubleArray, 0, ARRAY_SIZE, b, 0);

        double[] f = new double[ARRAY_SIZE];
        fea.decodeFromBytesToDoubleArray(f, 0, b, 0, b.length);

        for (int is = 0; is < ARRAY_SIZE; is++) {
            assertEquals(_doubleArray[is], f[is], 0.0);
        }
    }

    public void testUUIDAlgorithm() throws Exception {
        createArrayValues(ARRAY_SIZE);

        byte[] b = new byte[ARRAY_SIZE * 16];

        LongEncodingAlgorithm iea = new LongEncodingAlgorithm();
        iea.encodeToBytesFromLongArray(_uuidArray, 0, ARRAY_SIZE * 2, b, 0);

        long[] i = new long[ARRAY_SIZE * 2];
        iea.decodeFromBytesToLongArray(i, 0, b, 0, b.length);

        for (int is = 0; is < ARRAY_SIZE * 2; is++) {
            assertEquals(_uuidArray[is], i[is]);
        }
    }

    public void testBuiltInAlgorithmsSize1() throws Exception {
        _testBuiltInAlgorithms(1);
    }

    public void testBuiltInAlgorithmsSize2() throws Exception {
        _testBuiltInAlgorithms(1);
    }

    public void testBuiltInAlgorithmsSize8() throws Exception {
        _testBuiltInAlgorithms(8);
    }

    public void testBuiltInAlgorithmsSize32() throws Exception {
        _testBuiltInAlgorithms(32);
    }

    public void testBuiltInAlgorithmsSiz64() throws Exception {
        _testBuiltInAlgorithms(64);
    }

    public void testBuiltInAlgorithmsSiz256() throws Exception {
        _testBuiltInAlgorithms(256);
    }

    public void testBuiltInAlgorithmsSiz512() throws Exception {
        _testBuiltInAlgorithms(512);
    }

    public void _testBuiltInAlgorithms(int arraySize) throws Exception {
        createArrayValues(arraySize);

        byte[] b = createBuiltInTestFastInfosetDocument();
        InputStream bais = new ByteArrayInputStream(b);

        SAXDocumentParser p = new SAXDocumentParser();

        BuiltInTestHandler h = new BuiltInTestHandler(arraySize);

        p.setContentHandler(h);
        p.setLexicalHandler(h);
        p.setPrimitiveTypeContentHandler(h);

        p.parse(bais);
    }

    protected byte[] createBuiltInTestFastInfosetDocument() throws Exception {
        SAXDocumentSerializer s = new SAXDocumentSerializer();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        s.setOutputStream(baos);

        _attributes.clear();


        s.startDocument();

        s.startElement("", "e", "e", _attributes);

        // Booleans
        _attributes.addAttributeWithAlgorithmData(new QualifiedName("", "", "boolean", "boolean"),
                null, EncodingAlgorithmIndexes.BOOLEAN, _booleanArray);
        s.startElement("", "boolean", "boolean", _attributes);
        _attributes.clear();
        s.booleans(_booleanArray, 0, _booleanArray.length);
        s.endElement("", "boolean", "boolean");
        
        // Bytes
        _attributes.addAttributeWithAlgorithmData(new QualifiedName("", "", "byte", "byte"),
                null, EncodingAlgorithmIndexes.BASE64, _byteArray);
        s.startElement("", "byte", "byte", _attributes);
        _attributes.clear();
        s.bytes(_byteArray, 0, _byteArray.length);
        s.endElement("", "byte", "byte");

        // Shorts
        _attributes.addAttributeWithAlgorithmData(new QualifiedName("", "", "short", "short"),
                null, EncodingAlgorithmIndexes.SHORT, _shortArray);
        s.startElement("", "short", "short", _attributes);
        _attributes.clear();
        s.shorts(_shortArray, 0, _shortArray.length);
        s.endElement("", "short", "short");

        // Ints
        _attributes.addAttributeWithAlgorithmData(new QualifiedName("", "", "int", "int"),
                null, EncodingAlgorithmIndexes.INT, _intArray);
        s.startElement("", "int", "int", _attributes);
        _attributes.clear();
        s.ints(_intArray, 0, _intArray.length);
        s.endElement("", "int", "int");

        // Longs
        _attributes.addAttributeWithAlgorithmData(new QualifiedName("", "", "long", "long"),
                null, EncodingAlgorithmIndexes.LONG, _longArray);
        s.startElement("", "long", "long", _attributes);
        _attributes.clear();
        s.longs(_longArray, 0, _longArray.length);
        s.endElement("", "long", "long");

        // Floats
        _attributes.addAttributeWithAlgorithmData(new QualifiedName("", "", "float", "float"),
                null, EncodingAlgorithmIndexes.FLOAT, _floatArray);
        s.startElement("", "float", "float", _attributes);
        _attributes.clear();
        s.floats(_floatArray, 0, _floatArray.length);
        s.endElement("", "float", "float");

        // Doubles
        _attributes.addAttributeWithAlgorithmData(new QualifiedName("", "", "double", "double"),
                null, EncodingAlgorithmIndexes.DOUBLE, _doubleArray);
        s.startElement("", "double", "double", _attributes);
        _attributes.clear();
        s.doubles(_doubleArray, 0, _doubleArray.length);
        s.endElement("", "double", "double");

        // UUIDs
        _attributes.addAttributeWithAlgorithmData(new QualifiedName("", "", "uuid", "uuid"),
                null, EncodingAlgorithmIndexes.UUID, _uuidArray);
        s.startElement("", "uuid", "uuid", _attributes);
        _attributes.clear();
        s.uuids(_uuidArray, 0, _uuidArray.length);
        s.endElement("", "uuid", "uuid");

        // CDATA
        s.startElement("", "cdata", "cdata", _attributes);
            s.startCDATA();
            s.characters(_cdata.toCharArray(), 0, _cdata.length());
            s.endCDATA();
        s.endElement("", "cdata", "cdata");

        // Characters
        s.startElement("", "characters", "characters", _attributes);
            s.characters(_characters.toCharArray(), 0, _characters.length());
        s.endElement("", "characters", "characters");

        s.endElement("", "e", "e");

        s.endDocument();

        return baos.toByteArray();
    }

    public class BuiltInTestHandler extends FastInfosetDefaultHandler {

        protected int _arraySize;

        protected boolean _charactersAsCDATA;

        protected boolean _charactersShouldBeAsCDATA;

        public BuiltInTestHandler(int arraySize) {
            _arraySize = arraySize;
        }

        // ContentHandler

        public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            if (atts.getLength() > 0) {
                EncodingAlgorithmAttributes eas = (EncodingAlgorithmAttributes)atts;

                assertEquals(1, atts.getLength());

                if (localName.equals("boolean")) {
                    assertEquals("boolean", eas.getLocalName(0));
                    assertEquals(EncodingAlgorithmIndexes.BOOLEAN, eas.getAlgorithmIndex(0));
                    assertEquals(null, eas.getAlgorithmURI(0));
                    assertEquals(true, eas.getAlgorithmData(0) instanceof boolean[]);
                    boolean[] b = (boolean[])eas.getAlgorithmData(0);
                    for (int is = 0; is < _booleanArray.length; is++) {
                        assertEquals(_booleanArray[is], b[is]);
                    }
                } else if (localName.equals("byte")) {
                    assertEquals("byte", eas.getLocalName(0));
                    assertEquals(EncodingAlgorithmIndexes.BASE64, eas.getAlgorithmIndex(0));
                    assertEquals(null, eas.getAlgorithmURI(0));
                    assertEquals(true, eas.getAlgorithmData(0) instanceof byte[]);
                    byte[] b = (byte[])eas.getAlgorithmData(0);
                    for (int is = 0; is < _byteArray.length; is++) {
                        assertEquals(_byteArray[is], b[is]);
                    }
                } else if (localName.equals("short")) {
                    assertEquals("short", eas.getLocalName(0));
                    assertEquals(EncodingAlgorithmIndexes.SHORT, eas.getAlgorithmIndex(0));
                    assertEquals(null, eas.getAlgorithmURI(0));
                    assertEquals(true, eas.getAlgorithmData(0) instanceof short[]);
                    short[] i = (short[])eas.getAlgorithmData(0);
                    for (int is = 0; is < _shortArray.length; is++) {
                        assertEquals(_shortArray[is], i[is]);
                    }
                } else if (localName.equals("int")) {
                    assertEquals("int", eas.getLocalName(0));
                    assertEquals(EncodingAlgorithmIndexes.INT, eas.getAlgorithmIndex(0));
                    assertEquals(null, eas.getAlgorithmURI(0));
                    assertEquals(true, eas.getAlgorithmData(0) instanceof int[]);
                    int[] i = (int[])eas.getAlgorithmData(0);
                    for (int is = 0; is < _intArray.length; is++) {
                        assertEquals(_intArray[is], i[is]);
                    }
                } else if (localName.equals("long")) {
                    assertEquals("long", eas.getLocalName(0));
                    assertEquals(EncodingAlgorithmIndexes.LONG, eas.getAlgorithmIndex(0));
                    assertEquals(null, eas.getAlgorithmURI(0));
                    assertEquals(true, eas.getAlgorithmData(0) instanceof long[]);
                    long[] i = (long[])eas.getAlgorithmData(0);
                    for (int is = 0; is < _longArray.length; is++) {
                        assertEquals(_longArray[is], i[is]);
                    }
                } else if (localName.equals("float")) {
                    assertEquals("float", eas.getLocalName(0));
                    assertEquals(EncodingAlgorithmIndexes.FLOAT, eas.getAlgorithmIndex(0));
                    assertEquals(null, eas.getAlgorithmURI(0));
                    assertEquals(true, eas.getAlgorithmData(0) instanceof float[]);
                    float[] f = (float[])eas.getAlgorithmData(0);
                    for (int is = 0; is < _floatArray.length; is++) {
                        assertEquals(_floatArray[is], f[is], 0.0);
                    }
                } else if (localName.equals("double")) {
                    assertEquals("double", eas.getLocalName(0));
                    assertEquals(EncodingAlgorithmIndexes.DOUBLE, eas.getAlgorithmIndex(0));
                    assertEquals(null, eas.getAlgorithmURI(0));
                    assertEquals(true, eas.getAlgorithmData(0) instanceof double[]);
                    double[] f = (double[])eas.getAlgorithmData(0);
                    for (int is = 0; is < _doubleArray.length; is++) {
                        assertEquals(_doubleArray[is], f[is], 0.0);
                    }
                } else if (localName.equals("uuid")) {
                    assertEquals("uuid", eas.getLocalName(0));
                    assertEquals(EncodingAlgorithmIndexes.UUID, eas.getAlgorithmIndex(0));
                    assertEquals(null, eas.getAlgorithmURI(0));
                    assertEquals(true, eas.getAlgorithmData(0) instanceof long[]);
                    long[] i = (long[])eas.getAlgorithmData(0);
                    for (int is = 0; is < _uuidArray.length; is++) {
                        assertEquals(_uuidArray[is], i[is]);
                    }
                }
            } else {
                if (localName.equals("cdata")) {
                    _charactersShouldBeAsCDATA = true;
                } else if (localName.equals("characters")) {
                    _charactersShouldBeAsCDATA = false;
                } else {
                    assertEquals("e", localName);
                }
            }
        }

        public final void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        }

        public final void characters(char[] c, int start, int length) throws SAXException {
            String s = new String(c, start, length);
            if (_charactersShouldBeAsCDATA) {
                assertEquals(true, _charactersAsCDATA);
                assertEquals(_cdata, s);
            } else {
                assertEquals(false, _charactersAsCDATA);
                assertEquals(_characters, s);
            }
        }

        public final void startCDATA() throws SAXException {
            _charactersAsCDATA = true;
        }

        public final void endCDATA() throws SAXException {
            _charactersAsCDATA = false;
        }

        // PrimitiveTypeContentHandler

        public final void booleans(boolean[] b, int start, int length) throws SAXException {
            assertEquals(_arraySize, length);
            for (int i = 0; i < _booleanArray.length; i++) {
                assertEquals(_booleanArray[i], b[i + start]);
            }
        }
        
        public final void bytes(byte[] b, int start, int length) throws SAXException {
            assertEquals(_arraySize, length);
            for (int i = 0; i < _byteArray.length; i++) {
                assertEquals(_byteArray[i], b[i + start]);
            }
        }

        public final void shorts(short[] i, int start, int length) throws SAXException {
            assertEquals(_arraySize, length);

            for (int is = 0; is < _shortArray.length; is++) {
                assertEquals(_shortArray[is], i[is + start]);
            }
        }

        public final void ints(int[] i, int start, int length) throws SAXException {
            assertEquals(_arraySize, length);

            for (int is = 0; is < _intArray.length; is++) {
                assertEquals(_intArray[is], i[is + start]);
            }
        }

        public final void longs(long[] i, int start, int length) throws SAXException {
            assertEquals(_arraySize, length);

            for (int is = 0; is < _longArray.length; is++) {
                assertEquals(_longArray[is], i[is + start]);
            }
        }

        public final void floats(float[] f, int start, int length) throws SAXException {
            assertEquals(_arraySize, length);

            for (int i = 0; i < _floatArray.length; i++) {
                assertEquals(_floatArray[i], f[i + start], 0.0);
            }
        }

        public final void doubles(double[] f, int start, int length) throws SAXException {
            assertEquals(_arraySize, length);

            for (int i = 0; i < _doubleArray.length; i++) {
                assertEquals(_doubleArray[i], f[i + start], 0.0);
            }
        }
    }


    public void testGenericAlgorithms() throws Exception {
        createArrayValues(ARRAY_SIZE);

        byte[] b = createGenericTestFastInfosetDocument();
        InputStream bais = new ByteArrayInputStream(b);

        SAXDocumentParser p = new SAXDocumentParser();

        ParserVocabulary externalVocabulary = new ParserVocabulary();
        externalVocabulary.encodingAlgorithm.add(APPLICATION_DEFINED_ALGORITHM_URI);

        Map externalVocabularies = new HashMap();
        externalVocabularies.put(EXTERNAL_VOCABULARY_URI_STRING, externalVocabulary);
        p.setProperty(FastInfosetParser.EXTERNAL_VOCABULARIES_PROPERTY, externalVocabularies);

        GenericTestHandler h = new GenericTestHandler();

        p.setContentHandler(h);
        p.setLexicalHandler(h);
        p.setEncodingAlgorithmContentHandler(h);

        p.parse(bais);
    }

    protected byte[] createGenericTestFastInfosetDocument() throws Exception {
        SAXDocumentSerializer s = new SAXDocumentSerializer();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        s.setOutputStream(baos);

        SerializerVocabulary externalVocabulary = new SerializerVocabulary();
        externalVocabulary.encodingAlgorithm.add(APPLICATION_DEFINED_ALGORITHM_URI);

        SerializerVocabulary initialVocabulary = new SerializerVocabulary();
        initialVocabulary.setExternalVocabulary(
                EXTERNAL_VOCABULARY_URI_STRING,
                externalVocabulary, false);

        s.setVocabulary(initialVocabulary);

        _attributes.clear();


        s.startDocument();

        s.startElement("", "e", "e", _attributes);

        // Application-defined algorithm 31
        _attributes.addAttributeWithAlgorithmData(new QualifiedName("", "", "algorithm", "algorithm"),
                APPLICATION_DEFINED_ALGORITHM_URI, APPLICATION_DEFINED_ALGORITHM_ID, _byteArray);
        s.startElement("", "algorithm", "algorithm", _attributes);
        _attributes.clear();
        s.octets(APPLICATION_DEFINED_ALGORITHM_URI, APPLICATION_DEFINED_ALGORITHM_ID, _byteArray, 0, _byteArray.length);
        s.endElement("", "algorithm", "algorithm");


        s.endElement("", "e", "e");

        s.endDocument();

        return baos.toByteArray();
    }

    public class GenericTestHandler extends FastInfosetDefaultHandler {

        // ContentHandler

        public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            if (atts.getLength() > 0) {
                EncodingAlgorithmAttributes eas = (EncodingAlgorithmAttributes)atts;

                assertEquals(1, atts.getLength());

                if (localName.equals("algorithm")) {
                    assertEquals("algorithm", eas.getLocalName(0));
                    assertEquals(APPLICATION_DEFINED_ALGORITHM_ID, eas.getAlgorithmIndex(0));
                    assertEquals(APPLICATION_DEFINED_ALGORITHM_URI, eas.getAlgorithmURI(0));
                    assertEquals(true, eas.getAlgorithmData(0) instanceof byte[]);
                    byte[] b = (byte[])eas.getAlgorithmData(0);
                    for (int is = 0; is < _byteArray.length; is++) {
                        assertEquals(_byteArray[is], b[is]);
                    }
                }
            } else {
                assertEquals("e", localName);
            }
        }

        public final void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        }

        // EncodingAlgorithmContentHandler

        public final void object(String URI, int algorithm, Object data)  throws SAXException {
            assertTrue(true);
        }

        public final void octets(String URI, int algorithm, byte[] b, int start, int length)  throws SAXException {
            assertEquals(APPLICATION_DEFINED_ALGORITHM_ID, algorithm);
            assertEquals(APPLICATION_DEFINED_ALGORITHM_URI, URI);
            assertEquals(ARRAY_SIZE, length);

            for (int i = 0; i < ARRAY_SIZE; i++) {
                assertEquals(_byteArray[i], b[i + start]);
            }
        }
    }

    public void testRegisteredAlgorithms() throws Exception {
        createArrayValues(ARRAY_SIZE);

        byte[] b = createRegisteredTestFastInfosetDocument();
        InputStream bais = new ByteArrayInputStream(b);

        SAXDocumentParser p = new SAXDocumentParser();

        ParserVocabulary externalVocabulary = new ParserVocabulary();
        externalVocabulary.encodingAlgorithm.add(APPLICATION_DEFINED_ALGORITHM_URI);

        Map externalVocabularies = new HashMap();
        externalVocabularies.put(EXTERNAL_VOCABULARY_URI_STRING, externalVocabulary);
        p.setProperty(FastInfosetParser.EXTERNAL_VOCABULARIES_PROPERTY, externalVocabularies);

        Map algorithms = new HashMap();
        algorithms.put(APPLICATION_DEFINED_ALGORITHM_URI, new FloatEncodingAlgorithm());
        p.setRegisteredEncodingAlgorithms(algorithms);

        RegisteredTestHandler h = new RegisteredTestHandler();

        p.setContentHandler(h);
        p.setLexicalHandler(h);
        p.setEncodingAlgorithmContentHandler(h);

        p.parse(bais);
    }

    protected byte[] createRegisteredTestFastInfosetDocument() throws Exception {
        SAXDocumentSerializer s = new SAXDocumentSerializer();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        s.setOutputStream(baos);

        SerializerVocabulary externalVocabulary = new SerializerVocabulary();
        externalVocabulary.encodingAlgorithm.add(APPLICATION_DEFINED_ALGORITHM_URI);

        SerializerVocabulary initialVocabulary = new SerializerVocabulary();
        initialVocabulary.setExternalVocabulary(
                EXTERNAL_VOCABULARY_URI_STRING,
                externalVocabulary, false);

        s.setVocabulary(initialVocabulary);

        Map algorithms = new HashMap();
        algorithms.put(APPLICATION_DEFINED_ALGORITHM_URI, new FloatEncodingAlgorithm());
        s.setRegisteredEncodingAlgorithms(algorithms);

        _attributes.clear();


        s.startDocument();

        s.startElement("", "e", "e", _attributes);

        // Application-defined algorithm 31
        _attributes.addAttributeWithAlgorithmData(new QualifiedName("", "", "algorithm", "algorithm"),
                APPLICATION_DEFINED_ALGORITHM_URI, APPLICATION_DEFINED_ALGORITHM_ID, _floatArray);
        s.startElement("", "algorithm", "algorithm", _attributes);
        _attributes.clear();
        s.object(APPLICATION_DEFINED_ALGORITHM_URI, APPLICATION_DEFINED_ALGORITHM_ID, _floatArray);
        s.endElement("", "algorithm", "algorithm");


        s.endElement("", "e", "e");

        s.endDocument();

        return baos.toByteArray();
    }

    public class RegisteredTestHandler extends FastInfosetDefaultHandler {

        // ContentHandler

        public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            if (atts.getLength() > 0) {
                EncodingAlgorithmAttributes eas = (EncodingAlgorithmAttributes)atts;

                assertEquals(1, atts.getLength());

                if (localName.equals("algorithm")) {
                    assertEquals("algorithm", eas.getLocalName(0));
                    assertEquals(APPLICATION_DEFINED_ALGORITHM_ID, eas.getAlgorithmIndex(0));
                    assertEquals(APPLICATION_DEFINED_ALGORITHM_URI, eas.getAlgorithmURI(0));
                    assertEquals(true, eas.getAlgorithmData(0) instanceof float[]);
                    float[] b = (float[])eas.getAlgorithmData(0);
                    for (int is = 0; is < ARRAY_SIZE; is++) {
                        assertEquals(_floatArray[is], b[is], 0.0);
                    }
                }
            } else {
                assertEquals("e", localName);
            }
        }

        public final void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        }

        // EncodingAlgorithmContentHandler

        public final void object(String URI, int algorithm, Object data)  throws SAXException {
            assertEquals(APPLICATION_DEFINED_ALGORITHM_ID, algorithm);
            assertEquals(APPLICATION_DEFINED_ALGORITHM_URI, URI);
            assertEquals(true, data instanceof float[]);
            float[] b = (float[])data;
            for (int is = 0; is < _floatArray.length; is++) {
                assertEquals(_floatArray[is], b[is], 0.0);
            }
        }

        public final void octets(String URI, int algorithm, byte[] b, int start, int length)  throws SAXException {
            assertTrue(true);
        }
    }

    protected void createArrayValues(int arraySize) {
        _byteArray = new byte[arraySize];
        _shortArray = new short[arraySize];
        _intArray = new int[arraySize];
        _longArray = new long[arraySize];
        _floatArray = new float[arraySize];
        _doubleArray = new double[arraySize];
        _uuidArray = new long[arraySize * 2];
		_booleanArray = new boolean[arraySize];

        for (int i = 0; i < arraySize; i++) {
            final int j = i + 1;

            _byteArray[i] = (byte)j;
            _shortArray[i] = (short)(j * Short.MAX_VALUE / arraySize);
            _intArray[i] = j * (Integer.MAX_VALUE / arraySize);
            _longArray[i] = j * (Long.MAX_VALUE / arraySize);
            _floatArray[i] = (float)(j * Math.E);
            _doubleArray[i] = j * Math.E;
            _uuidArray[i] = j * (Long.MAX_VALUE / arraySize);
            _uuidArray[i * 2] = j * (Long.MAX_VALUE / arraySize);
            if (j % 2 == 0)
               _booleanArray[i] = true;
        }
    }

    public void testStAXBase64EncodingAlgorithm() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StAXDocumentSerializer ds = new StAXDocumentSerializer(baos);

        byte[] data = new byte[256];
        for (int i = 0; i < 256; i++) {
            data[i] = (byte)i;
        }

        ds.writeStartDocument();
            ds.writeStartElement("element");
                ds.writeOctets(data, 0, data.length);
            ds.writeEndElement();
        ds.writeEndDocument();
        ds.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        StAXDocumentParser dp = new StAXDocumentParser(bais);
        while(dp.hasNext()) {
            int e = dp.next();
            if (e == XMLStreamReader.CHARACTERS) {
                byte[] b = dp.getTextAlgorithmBytes();
                assertEquals(EncodingAlgorithmIndexes.BASE64, dp.getTextAlgorithmIndex());
                assertTrue(b != null);
                assertEquals(data.length, dp.getTextAlgorithmLength());
                for (int i = 0; i < data.length; i++) {
                    assertEquals(data[i], b[dp.getTextAlgorithmStart() + i]);
                }

                b = dp.getTextAlgorithmBytesClone();
                assertTrue(b != null);
                assertEquals(data.length, b.length);
                for (int i = 0; i < data.length; i++) {
                    assertEquals(data[i], b[i]);
                }

                String c = dp.getText();
                assertEquals(_base64String.length(), c.length());
                assertEquals(_base64String, c);
            }
        }
    }

    public void testDOMBase64EncodingAlgorithm() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StAXDocumentSerializer ds = new StAXDocumentSerializer(baos);

        byte[] data = new byte[256];
        for (int i = 0; i < 256; i++) {
            data[i] = (byte)i;
        }

        ds.writeStartDocument();
            ds.writeStartElement("element");
                ds.writeOctets(data, 0, data.length);
            ds.writeEndElement();
        ds.writeEndDocument();
        ds.close();


        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DOMDocumentParser dp = new DOMDocumentParser();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document d = db.newDocument();
        dp.parse(d, bais);

        Text t = (Text)d.getFirstChild().getFirstChild();
        String c = t.getNodeValue();
        assertEquals(_base64String.length(), c.length());
        assertEquals(_base64String, c);
    }
}
