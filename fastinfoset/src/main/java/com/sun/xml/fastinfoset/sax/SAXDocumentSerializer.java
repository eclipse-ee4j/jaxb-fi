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

package com.sun.xml.fastinfoset.sax;

import com.sun.xml.fastinfoset.Encoder;
import com.sun.xml.fastinfoset.EncodingConstants;
import com.sun.xml.fastinfoset.QualifiedName;
import org.jvnet.fastinfoset.sax.FastInfosetWriter;
import com.sun.xml.fastinfoset.util.LocalNameQualifiedNamesMap;
import java.io.IOException;
import org.jvnet.fastinfoset.EncodingAlgorithmIndexes;
import org.jvnet.fastinfoset.FastInfosetException;
import org.jvnet.fastinfoset.RestrictedAlphabet;
import org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.sun.xml.fastinfoset.CommonResourceBundle;

/**
 * The Fast Infoset SAX serializer.
 * <p>
 * Instantiate this serializer to serialize a fast infoset document in accordance 
 * with the SAX API.
 * <p>
 * This utilizes the SAX API in a reverse manner to that of parsing. It is the
 * responsibility of the client to call the appropriate event methods on the 
 * SAX handlers, and to ensure that such a sequence of methods calls results 
 * in the production well-formed fast infoset documents. The 
 * SAXDocumentSerializer performs no well-formed checks.
 * 
 * <p>
 * More than one fast infoset document may be encoded to the 
 * {@link java.io.OutputStream}.
 */
public class SAXDocumentSerializer extends Encoder implements FastInfosetWriter {
    protected boolean _elementHasNamespaces = false;

    protected boolean _charactersAsCDATA = false;
    
    protected SAXDocumentSerializer(boolean v) {
        super(v);
    }
    
    public SAXDocumentSerializer() {
    }


    @Override
    public void reset() {
        super.reset();
        
        _elementHasNamespaces = false;
        _charactersAsCDATA = false;
    }
    
    // ContentHandler

    @Override
    public final void startDocument() throws SAXException {
        try {
            reset();
            encodeHeader(false);
            encodeInitialVocabulary();
        } catch (IOException e) {
            throw new SAXException("startDocument", e);
        }
    }

    @Override
    public final void endDocument() throws SAXException {
        try {
            encodeDocumentTermination();
        } catch (IOException e) {
            throw new SAXException("endDocument", e);
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        try {
            if (!_elementHasNamespaces) {
                encodeTermination();

                // Mark the current buffer position to flag attributes if necessary
                mark();
                _elementHasNamespaces = true;

                // Write out Element byte with namespaces
                write(EncodingConstants.ELEMENT | EncodingConstants.ELEMENT_NAMESPACES_FLAG);
            }

            encodeNamespaceAttribute(prefix, uri);
        } catch (IOException e) {
            throw new SAXException("startElement", e);
        }
    }

    @Override
    public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        // TODO consider using buffer for encoding of attributes, then pre-counting is not necessary
        final int attributeCount = (atts != null && atts.getLength() > 0) 
                ? countAttributes(atts) : 0;
        try {
            if (_elementHasNamespaces) {
                _elementHasNamespaces = false;

                if (attributeCount > 0) {
                    // Flag the marked byte with attributes
                    _octetBuffer[_markIndex] |= EncodingConstants.ELEMENT_ATTRIBUTE_FLAG;
                }
                resetMark();

                write(EncodingConstants.TERMINATOR);

                _b = 0;
            } else {
                encodeTermination();

                _b = EncodingConstants.ELEMENT;
                if (attributeCount > 0) {
                    _b |= EncodingConstants.ELEMENT_ATTRIBUTE_FLAG;
                }
            }

            encodeElement(namespaceURI, qName, localName);

            if (attributeCount > 0) {
                encodeAttributes(atts);
            }
        } catch (IOException | FastInfosetException e) {
            throw new SAXException("startElement", e);
        }
    }

    @Override
    public final void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            encodeElementTermination();
        } catch (IOException e) {
            throw new SAXException("endElement", e);
        }
    }

    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        
        if (getIgnoreWhiteSpaceTextContent() && 
                isWhiteSpace(ch, start, length)) return;

        try {
            encodeTermination();

            if (!_charactersAsCDATA) {
                encodeCharacters(ch, start, length);
            } else {
                encodeCIIBuiltInAlgorithmDataAsCDATA(ch, start, length);
            }
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (getIgnoreWhiteSpaceTextContent()) return;
        
        characters(ch, start, length);
    }

    @Override
    public final void processingInstruction(String target, String data) throws SAXException {
        try {
            if (getIgnoreProcesingInstructions()) return;
            
            if (target.isEmpty()) {
                throw new SAXException(CommonResourceBundle.getInstance().
                        getString("message.processingInstructionTargetIsEmpty"));
            }
            encodeTermination();

            encodeProcessingInstruction(target, data);
        } catch (IOException e) {
            throw new SAXException("processingInstruction", e);
        }
    }

    @Override
    public final void setDocumentLocator(org.xml.sax.Locator locator) {
    }

    @Override
    public final void skippedEntity(String name) throws SAXException {
    }



    // LexicalHandler

    @Override
    public final void comment(char[] ch, int start, int length) throws SAXException {
        try {
            if (getIgnoreComments()) return;
            
            encodeTermination();

            encodeComment(ch, start, length);
        } catch (IOException e) {
            throw new SAXException("startElement", e);
        }
    }

    @Override
    public final void startCDATA() throws SAXException {
        _charactersAsCDATA = true;
    }

    @Override
    public final void endCDATA() throws SAXException {
        _charactersAsCDATA = false;
    }

    @Override
    public final void startDTD(String name, String publicId, String systemId) throws SAXException {
        if (getIgnoreDTD()) return;
        
        try {
            encodeTermination();
            
            encodeDocumentTypeDeclaration(publicId, systemId);
            encodeElementTermination();
        } catch (IOException e) {
            throw new SAXException("startDTD", e);
        }
    }

    @Override
    public final void endDTD() throws SAXException {
    }

    @Override
    public final void startEntity(String name) throws SAXException {
    }

    @Override
    public final void endEntity(String name) throws SAXException {
    }

    
    // EncodingAlgorithmContentHandler
    
    @Override
    public final void octets(String URI, int id, byte[] b, int start, int length)  throws SAXException {
        if (length <= 0) {
            return;
        }

        try {
            encodeTermination();

            encodeNonIdentifyingStringOnThirdBit(URI, id, b, start, length);
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void object(String URI, int id, Object data)  throws SAXException {
        try {
            encodeTermination();

            encodeNonIdentifyingStringOnThirdBit(URI, id, data);
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }


    // PrimitiveTypeContentHandler

    @Override
    public final void bytes(byte[] b, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }

        try {
            encodeTermination();

            encodeCIIOctetAlgorithmData(EncodingAlgorithmIndexes.BASE64, b, start, length);
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void shorts(short[] s, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }

        try {
            encodeTermination();

            encodeCIIBuiltInAlgorithmData(EncodingAlgorithmIndexes.SHORT, s, start, length);
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void ints(int[] i, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }

        try {
            encodeTermination();

            encodeCIIBuiltInAlgorithmData(EncodingAlgorithmIndexes.INT, i, start, length);
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void longs(long[] l, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }

        try {
            encodeTermination();

            encodeCIIBuiltInAlgorithmData(EncodingAlgorithmIndexes.LONG, l, start, length);
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void booleans(boolean[] b, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }

        try {
            encodeTermination();

            encodeCIIBuiltInAlgorithmData(EncodingAlgorithmIndexes.BOOLEAN, b, start, length);
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public final void floats(float[] f, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }

        try {
            encodeTermination();

            encodeCIIBuiltInAlgorithmData(EncodingAlgorithmIndexes.FLOAT, f, start, length);
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void doubles(double[] d, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }

        try {
            encodeTermination();

            encodeCIIBuiltInAlgorithmData(EncodingAlgorithmIndexes.DOUBLE, d, start, length);
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void uuids(long[] msblsb, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }

        try {
            encodeTermination();

            encodeCIIBuiltInAlgorithmData(EncodingAlgorithmIndexes.UUID, msblsb, start, length);
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }


    // RestrictedAlphabetContentHandler
    
    @Override
    public void numericCharacters(char[] ch, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }

        try {
            encodeTermination();

            final boolean addToTable = isCharacterContentChunkLengthMatchesLimit(length);
            encodeNumericFourBitCharacters(ch, start, length, addToTable);
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void dateTimeCharacters(char[] ch, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }

        try {
            encodeTermination();

            final boolean addToTable = isCharacterContentChunkLengthMatchesLimit(length);
            encodeDateTimeFourBitCharacters(ch, start, length, addToTable);
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void alphabetCharacters(String alphabet, char[] ch, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }

        try {
            encodeTermination();

            final boolean addToTable = isCharacterContentChunkLengthMatchesLimit(length);
            encodeAlphabetCharacters(alphabet, ch, start, length, addToTable);
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    // ExtendedContentHandler
    
    @Override
    public void characters(char[] ch, int start, int length, boolean index) throws SAXException {
        if (length <= 0) {
            return;
        }
        
        if (getIgnoreWhiteSpaceTextContent() && 
                isWhiteSpace(ch, start, length)) return;

        try {
            encodeTermination();

            if (!_charactersAsCDATA) {
                encodeNonIdentifyingStringOnThirdBit(ch, start, length, _v.characterContentChunk, index, true);
            } else {
                encodeCIIBuiltInAlgorithmDataAsCDATA(ch, start, length);
            }
        } catch (IOException | FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    

    protected final int countAttributes(Attributes atts) {
        // Count attributes ignoring any in the XMLNS namespace
        // Note, such attributes may be produced when transforming from a DOM node
        int count = 0;
        for (int i = 0; i < atts.getLength(); i++) {
            final String uri = atts.getURI(i);
            if (uri.equals("http://www.w3.org/2000/xmlns/")) {
                continue;
            }
            count++;
        }
        return count;
    }

    protected void encodeAttributes(Attributes atts) throws IOException, FastInfosetException {
        boolean addToTable;
        boolean mustBeAddedToTable;
        String value;
        if (atts instanceof EncodingAlgorithmAttributes) {
            final EncodingAlgorithmAttributes eAtts = (EncodingAlgorithmAttributes)atts;
            Object data;
            String alphabet;
            for (int i = 0; i < eAtts.getLength(); i++) {
                if (encodeAttribute(atts.getURI(i), atts.getQName(i), atts.getLocalName(i))) {
                    data = eAtts.getAlgorithmData(i);
                    // If data is null then there is no algorithm data
                    if (data == null) {
                        value = eAtts.getValue(i);
                        addToTable = isAttributeValueLengthMatchesLimit(value.length());
                        mustBeAddedToTable = eAtts.getToIndex(i);

                        alphabet = eAtts.getAlpababet(i);
                        if (alphabet == null) {
                            encodeNonIdentifyingStringOnFirstBit(value, _v.attributeValue, addToTable, mustBeAddedToTable);
                        } else if (alphabet.equals(RestrictedAlphabet.DATE_TIME_CHARACTERS)) {
                            encodeDateTimeNonIdentifyingStringOnFirstBit(
                                    value, addToTable, mustBeAddedToTable);
                        } else if (alphabet.equals(RestrictedAlphabet.NUMERIC_CHARACTERS)) {
                            encodeNumericNonIdentifyingStringOnFirstBit(
                                    value, addToTable, mustBeAddedToTable);
                        } else {
                            encodeNonIdentifyingStringOnFirstBit(value, _v.attributeValue, addToTable, mustBeAddedToTable);
                        }
                    } else {
                        encodeNonIdentifyingStringOnFirstBit(eAtts.getAlgorithmURI(i),
                                eAtts.getAlgorithmIndex(i), data);
                    }
                }
            }
        } else {
            for (int i = 0; i < atts.getLength(); i++) {
                if (encodeAttribute(atts.getURI(i), atts.getQName(i), atts.getLocalName(i))) {
                    value = atts.getValue(i);
                    addToTable = isAttributeValueLengthMatchesLimit(value.length());
                    encodeNonIdentifyingStringOnFirstBit(value, _v.attributeValue, addToTable, false);
                }
            }
        }
        _b = EncodingConstants.TERMINATOR;
        _terminate = true;
    }
    
    protected void encodeElement(String namespaceURI, String qName, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = _v.elementName.obtainEntry(qName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; i++) {
                final QualifiedName n = names[i];
                if (namespaceURI.equals(n.namespaceName)) {
                    encodeNonZeroIntegerOnThirdBit(names[i].index);
                    return;
                }
            }
        }

        encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, getPrefixFromQualifiedName(qName),
                localName, entry);
    }

    protected boolean encodeAttribute(String namespaceURI, String qName, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = _v.attributeName.obtainEntry(qName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; i++) {
                if (namespaceURI.equals(names[i].namespaceName)) {
                    encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
                    return true;
                }
            }
        }

        return encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, getPrefixFromQualifiedName(qName),
                localName, entry);
    }    
}
