/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2012 Oracle and/or its affiliates. All rights reserved.
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
package com.sun.xml.fastinfoset.streambuffer;

import com.sun.xml.fastinfoset.types.ValueInstance;
import com.sun.xml.stream.buffer.AbstractProcessor;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import org.jvnet.fastinfoset.RestrictedAlphabet;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.XMLConstants;
import org.jvnet.fastinfoset.sax.FastInfosetWriter;
import org.jvnet.fastinfoset.sax.helpers.EncodingAlgorithmAttributesImpl;

/**
 * A processor of a {@link XMLStreamBuffer} that that serializes the XML infoset
 * using a {@link FastInfosetWriter}.
 */
public final class FastInfosetWriterSAXBufferProcessor extends AbstractProcessor {    
    /**
     * Reference to Fast Infoset writer.
     */
    FastInfosetWriter _writer;
    
    /**
     * SAX Namespace attributes features
     */
    boolean _namespacePrefixesFeature = false;
    
    EncodingAlgorithmAttributesImpl _attributes;
    
    String[] _namespacePrefixes = new String[16];
    int _namespacePrefixesIndex;
    
    int[] _namespaceAttributesStack = new int[16];
    int _namespaceAttributesStackIndex;

    public FastInfosetWriterSAXBufferProcessor() {
        _attributes = new EncodingAlgorithmAttributesImpl();
    }

    public FastInfosetWriterSAXBufferProcessor(XMLStreamBuffer buffer) {
        this();
        setXMLStreamBuffer(buffer);
    }
                
    public final void process(XMLStreamBuffer buffer, FastInfosetWriter writer) throws SAXException {
        setXMLStreamBuffer(buffer);
        process(writer);
    }

    public void setXMLStreamBuffer(XMLStreamBuffer buffer) {
        setBuffer(buffer);
    }

    public final void process(FastInfosetWriter writer) throws SAXException {
        _writer = writer;
        
        final int item = readStructure();
        switch(_eiiStateTable[item]) {
            case STATE_DOCUMENT:
                processDocument();
                break;
            case STATE_END:
                // Empty buffer
                return;
            case STATE_ELEMENT_U_LN_QN:
                processElement(readStructureString(), readStructureString(), readStructureString());
                break;
            case STATE_ELEMENT_P_U_LN:
            {
                final String prefix = readStructureString();
                final String uri = readStructureString();
                final String localName = readStructureString();
                processElement(uri, localName, getQName(prefix, localName));
                break;
            }
            case STATE_ELEMENT_U_LN: {
                final String uri = readStructureString();
                final String localName = readStructureString();
                processElement(uri, localName, localName);
                break;
            }
            case STATE_ELEMENT_LN:
            {
                final String localName = readStructureString();
                processElement("", localName, localName);
                break;
            }
            default:
                throw reportFatalError("Illegal state for DIIs: "+item);
        }
    }

    /**
     * Report a fatal error and abort.
     *
     * This is necessary to follow the SAX semantics of error handling.
     */
    private SAXParseException reportFatalError(String msg) throws SAXException {
        SAXParseException spe = new SAXParseException(msg, null);
        return spe;
    }

    private void processDocument() throws SAXException {
        _writer.startDocument();

        boolean firstElementHasOccured = false;
        int item;
        do {
            item = readStructure();
            switch(_eiiStateTable[item]) {
                case STATE_ELEMENT_U_LN_QN:
                    firstElementHasOccured = true;
                    processElement(readStructureString(), readStructureString(), readStructureString());
                    break;
                case STATE_ELEMENT_P_U_LN:
                {
                    firstElementHasOccured = true;
                    final String prefix = readStructureString();
                    final String uri = readStructureString();
                    final String localName = readStructureString();
                    processElement(uri, localName, getQName(prefix, localName));
                    break;
                }
                case STATE_ELEMENT_U_LN: {
                    firstElementHasOccured = true;
                    final String uri = readStructureString();
                    final String localName = readStructureString();
                    processElement(uri, localName, localName);
                    break;
                }
                case STATE_ELEMENT_LN:
                {
                    firstElementHasOccured = true;
                    final String localName = readStructureString();
                    processElement("", localName, localName);
                    break;
                }
                case STATE_COMMENT_AS_CHAR_ARRAY_SMALL:
                {
                    final int length = readStructure();
                    final int start = readContentCharactersBuffer(length);
                    processComment(_contentCharactersBuffer, start, length);
                    break;
                }
                case STATE_COMMENT_AS_CHAR_ARRAY_MEDIUM:
                {
                    final int length = (readStructure() << 8) | readStructure();
                    final int start = readContentCharactersBuffer(length);
                    processComment(_contentCharactersBuffer, start, length);
                    break;
                }
                case STATE_COMMENT_AS_CHAR_ARRAY_COPY:
                {
                    final char[] ch = readContentCharactersCopy();
                    processComment(ch, 0, ch.length);
                    break;
                }
                case STATE_COMMENT_AS_STRING:
                    processComment(readContentString());
                    break;
                case STATE_PROCESSING_INSTRUCTION:
                    processProcessingInstruction(readStructureString(), readStructureString());
                    break;
                case STATE_END:
                    break;
                default:
                    throw reportFatalError("Illegal state for child of DII: "+item);
            }
        } while(item != T_END || !firstElementHasOccured);

        while(item != T_END) {
            item = readStructure();
            switch(_eiiStateTable[item]) {
                case STATE_COMMENT_AS_CHAR_ARRAY_SMALL:
                {
                    final int length = readStructure();
                    final int start = readContentCharactersBuffer(length);
                    processComment(_contentCharactersBuffer, start, length);
                    break;
                }
                case STATE_COMMENT_AS_CHAR_ARRAY_MEDIUM:
                {
                    final int length = (readStructure() << 8) | readStructure();
                    final int start = readContentCharactersBuffer(length);
                    processComment(_contentCharactersBuffer, start, length);
                    break;
                }
                case STATE_COMMENT_AS_CHAR_ARRAY_COPY:
                {
                    final char[] ch = readContentCharactersCopy();
                    processComment(ch, 0, ch.length);
                    break;
                }
                case STATE_COMMENT_AS_STRING:
                    processComment(readContentString());
                    break;
                case STATE_PROCESSING_INSTRUCTION:
                    processProcessingInstruction(readStructureString(), readStructureString());
                    break;
                case STATE_END:
                    break;
                default:
                    throw reportFatalError("Illegal state for child of DII: "+item);
            }
        }

        _writer.endDocument();
    }

    private void processElement(String uri, String localName, String qName) throws SAXException {
        boolean hasAttributes = false;
        boolean hasNamespaceAttributes = false;
        int item = peekStructure();
        if ((item & TYPE_MASK) == T_NAMESPACE_ATTRIBUTE) {
            hasNamespaceAttributes = true;
            item = processNamespaceAttributes(item);
        }        
        if ((item & TYPE_MASK) == T_ATTRIBUTE) {
            hasAttributes = true;
            processAttributes(item);
        }

        _writer.startElement(uri, localName, qName, _attributes);

        if (hasAttributes) {
            _attributes.clear();
        }

        do {
            item = _eiiStateTable[readStructure()];
            switch(item) {
                case STATE_ELEMENT_U_LN_QN:
                    processElement(readStructureString(), readStructureString(), readStructureString());
                    break;
                case STATE_ELEMENT_P_U_LN:
                {
                    final String p = readStructureString();
                    final String u = readStructureString();
                    final String ln = readStructureString();
                    processElement(u, ln, getQName(p, ln));
                    break;
                }
                case STATE_ELEMENT_U_LN: {
                    final String u = readStructureString();
                    final String ln = readStructureString();
                    processElement(u, ln, ln);
                    break;
                }
                case STATE_ELEMENT_LN: {
                    final String ln = readStructureString();
                    processElement("", ln, ln);
                    break;
                }
                case STATE_TEXT_AS_CHAR_ARRAY_SMALL:
                {
                    final int length = readStructure();
                    int start = readContentCharactersBuffer(length);
                    _writer.characters(_contentCharactersBuffer, start, length);
                    break;
                }
                case STATE_TEXT_AS_CHAR_ARRAY_MEDIUM:
                {
                    final int length = (readStructure() << 8) | readStructure();
                    int start = readContentCharactersBuffer(length);
                    _writer.characters(_contentCharactersBuffer, start, length);
                    break;
                }
                case STATE_TEXT_AS_CHAR_ARRAY_COPY:
                {
                    final char[] ch = readContentCharactersCopy();

                    _writer.characters(ch, 0, ch.length);
                    break;
                }
                case STATE_TEXT_AS_STRING:
                {
                    final String s = readContentString();
                    _writer.characters(s.toCharArray(), 0, s.length());
                    break;
                }
                case STATE_TEXT_AS_OBJECT:
                {
                    ValueInstance v = (ValueInstance)readContentObject();
                    if (v.type == ValueInstance.Type.encodingAlgorithm) {
                        _writer.object(null, v.id, v.instance);
                    } else if (v.type == ValueInstance.Type.alphabet) {
                         char ch[] = (char[]) v.instance;
                         if (v.alphabet == RestrictedAlphabet.DATE_TIME_CHARACTERS) {
                            _writer.dateTimeCharacters(ch, 0, ch.length);
                        } else if (v.alphabet == RestrictedAlphabet.NUMERIC_CHARACTERS) {
                            _writer.numericCharacters(ch, 0, ch.length);
                        }
                    } else {
                        char ch[] = (char[]) v.instance;
                        _writer.characters(ch, 0, ch.length, true);
                    }
                    break;
                }
                case STATE_COMMENT_AS_CHAR_ARRAY_SMALL:
                {
                    final int length = readStructure();
                    final int start = readContentCharactersBuffer(length);
                    processComment(_contentCharactersBuffer, start, length);
                    break;
                }
                case STATE_COMMENT_AS_CHAR_ARRAY_MEDIUM:
                {
                    final int length = (readStructure() << 8) | readStructure();
                    final int start = readContentCharactersBuffer(length);
                    processComment(_contentCharactersBuffer, start, length);
                    break;
                }
                case STATE_COMMENT_AS_CHAR_ARRAY_COPY:
                {
                    final char[] ch = readContentCharactersCopy();
                    processComment(ch, 0, ch.length);
                    break;
                }
                case T_COMMENT_AS_STRING:
                    processComment(readContentString());
                    break;
                case STATE_PROCESSING_INSTRUCTION:
                    processProcessingInstruction(readStructureString(), readStructureString());
                    break;
                case STATE_END:
                    break;
                default:
                    throw reportFatalError("Illegal state for child of EII: "+item);
            }
        } while(item != STATE_END);
        
        _writer.endElement(uri, localName, qName);

        if (hasNamespaceAttributes) {
            processEndPrefixMapping();
        }
    }
    
    private void processEndPrefixMapping() throws SAXException {
        final int end = _namespaceAttributesStack[--_namespaceAttributesStackIndex];
        final int start = (_namespaceAttributesStackIndex > 0) ? _namespaceAttributesStack[_namespaceAttributesStackIndex] : 0;
        
        for (int i = end - 1; i >= start; i--) {
            _writer.endPrefixMapping(_namespacePrefixes[i]);
        }
        _namespacePrefixesIndex = start;
    }
    
    private int processNamespaceAttributes(int item) throws SAXException {
        do {
            switch(_niiStateTable[item]) {
                case STATE_NAMESPACE_ATTRIBUTE:
                    // Undeclaration of default namespace
                    processNamespaceAttribute("", "");
                    break;
                case STATE_NAMESPACE_ATTRIBUTE_P:
                    // Undeclaration of namespace
                    processNamespaceAttribute(readStructureString(), "");
                    break;
                case STATE_NAMESPACE_ATTRIBUTE_P_U:
                    // Declaration with prefix
                    processNamespaceAttribute(readStructureString(), readStructureString());
                    break;
                case STATE_NAMESPACE_ATTRIBUTE_U:
                    // Default declaration
                    processNamespaceAttribute("", readStructureString());
                    break;
                default:
                    throw reportFatalError("Illegal state: "+item);
            }
            readStructure();
            
            item = peekStructure();
        } while((item & TYPE_MASK) == T_NAMESPACE_ATTRIBUTE);
        
        
        cacheNamespacePrefixIndex();
        
        return item;
    }
    
    private void processAttributes(int item) throws SAXException {
        do {
            switch(_aiiStateTable[item]) {
                case STATE_ATTRIBUTE_U_LN_QN:
                    _attributes.addAttribute(readStructureString(), readStructureString(), readStructureString(), readStructureString(), readContentString());
                    break;
                case STATE_ATTRIBUTE_P_U_LN:
                {
                    final String p = readStructureString();
                    final String u = readStructureString();
                    final String ln = readStructureString();
                    _attributes.addAttribute(u, ln, getQName(p, ln), readStructureString(), readContentString());
                    break;
                }
                case STATE_ATTRIBUTE_U_LN: {
                    final String u = readStructureString();
                    final String ln = readStructureString();
                    _attributes.addAttribute(u, ln, ln, readStructureString(), readContentString()); 
                    break;
                }
                case STATE_ATTRIBUTE_LN: {
                    final String ln = readStructureString();
                    _attributes.addAttribute("", ln, ln, readStructureString(), readContentString()); 
                    break;
                }
                case STATE_ATTRIBUTE_U_LN_QN_OBJECT:
                    processAttributeValue(readStructureString(), readStructureString(), readStructureString(), readStructureString());
                    break;
                case STATE_ATTRIBUTE_P_U_LN_OBJECT:
                {
                    final String p = readStructureString();
                    final String u = readStructureString();
                    final String ln = readStructureString();
                    processAttributeValue(u, ln, getQName(p, ln), readStructureString());
                    break;
                }
                case STATE_ATTRIBUTE_U_LN_OBJECT: {
                    final String u = readStructureString();
                    final String ln = readStructureString();
                    processAttributeValue(u, ln, ln, readStructureString());
                    break;
                }
                case STATE_ATTRIBUTE_LN_OBJECT: {
                    final String ln = readStructureString();
                    processAttributeValue("", ln, ln, readStructureString());
                    break;
                }
                default:
                    throw reportFatalError("Illegal state: "+item);
            }
            readStructure();
            
            item = peekStructure();
        } while((item & TYPE_MASK) == T_ATTRIBUTE);
    }

    private void processAttributeValue(String u, String ln, String qn, String t) throws SAXException {
        ValueInstance v = (ValueInstance)readContentObject();
        if (v.type == ValueInstance.Type.encodingAlgorithm) {
            _attributes.addAttributeWithAlgorithmData(u, ln, qn, null, v.id, v.instance);
        } else if (v.type == ValueInstance.Type.alphabet) {
            _attributes.addAttribute(u, ln, qn, t, (String)v.instance, false, v.alphabet);
        } else {
            _attributes.addAttribute(u, ln, qn, t, (String)v.instance, true, null);
        }
    }
    
    private void processNamespaceAttribute(String prefix, String uri) throws SAXException {
        _writer.startPrefixMapping(prefix, uri);

        if (_namespacePrefixesFeature) {
            // Add the namespace delcaration as an attribute
            if (prefix != "") {
                _attributes.addAttribute(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix,
                        getQName(XMLConstants.XMLNS_ATTRIBUTE, prefix),
                        "CDATA", uri);
            } else {
                _attributes.addAttribute(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE, 
                        XMLConstants.XMLNS_ATTRIBUTE,
                        "CDATA", uri);
            }
        }
        
        cacheNamespacePrefix(prefix);
    }
    
    private void cacheNamespacePrefix(String prefix) {
        if (_namespacePrefixesIndex == _namespacePrefixes.length) {
            final String[] namespaceAttributes = new String[_namespacePrefixesIndex * 3 / 2 + 1];
            System.arraycopy(_namespacePrefixes, 0, namespaceAttributes, 0, _namespacePrefixesIndex);
            _namespacePrefixes = namespaceAttributes;
        }
        
        _namespacePrefixes[_namespacePrefixesIndex++] = prefix;
    }
    
    private void cacheNamespacePrefixIndex() {
        if (_namespaceAttributesStackIndex == _namespaceAttributesStack.length) {
            final int[] namespaceAttributesStack = new int[_namespaceAttributesStackIndex * 3 /2 + 1];
            System.arraycopy(_namespaceAttributesStack, 0, namespaceAttributesStack, 0, _namespaceAttributesStackIndex);
            _namespaceAttributesStack = namespaceAttributesStack;
        }

        _namespaceAttributesStack[_namespaceAttributesStackIndex++] = _namespacePrefixesIndex;
    }
    
    private void processComment(String s)  throws SAXException {
        processComment(s.toCharArray(), 0, s.length());
    }
    
    private void processComment(char[] ch, int start, int length) throws SAXException {
        _writer.comment(ch, start, length);
    }

    private void processProcessingInstruction(String target, String data) throws SAXException {
        _writer.processingInstruction(target, data);
    }
}
