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
import com.sun.xml.stream.buffer.AbstractCreator;
import org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.sax.Features;
import java.io.IOException;
import java.io.InputStream;
import org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import org.jvnet.fastinfoset.sax.FastInfosetReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;

/**
 * A creator of a {@link XMLStreamBuffer} that that produces the XML infoset
 * using a {@link FastInfosetReader}.
 */
public final class FastInfosetReaderSAXBufferCreator extends AbstractCreator 
        implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler, LexicalHandler,
        EncodingAlgorithmContentHandler {
    
    String[] _namespaceAttributes = new String[16 * 2];
    
    int _namespaceAttributesPtr;
    
    public FastInfosetReaderSAXBufferCreator() {
    }
    
    public FastInfosetReaderSAXBufferCreator(MutableXMLStreamBuffer buffer) {
        setBuffer(buffer);
    }
    
    public MutableXMLStreamBuffer create(FastInfosetReader reader, InputStream in) throws IOException, SAXException {
        if (_buffer == null) {
            createBuffer();
        }
        
        reader.setContentHandler(this);
        reader.setLexicalHandler(this);
        reader.setEncodingAlgorithmContentHandler(this);
        
        try {
            setHasInternedStrings(reader.getFeature(Features.STRING_INTERNING_FEATURE));
        } catch (SAXException e) {
        }
        
        reader.parse(new InputSource(in));
        
        return getXMLStreamBuffer();
    }
    
    public void reset() {
        _buffer = null;
        _namespaceAttributesPtr = 0;
    }
    
    
    public void startDocument() throws SAXException {
        storeStructure(T_DOCUMENT);
    }
    
    public void endDocument() throws SAXException {
        storeStructure(T_END);
    }
        
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        cacheNamespaceAttribute(prefix, uri);
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        storeQualifiedName(T_ELEMENT_LN,
                uri, localName, qName);
        
        // Has namespaces attributes
        if (_namespaceAttributesPtr > 0) {
            storeNamespaceAttributes();
        }
        
        // Has attributes
        if (attributes.getLength() > 0) {
            storeAttributes((EncodingAlgorithmAttributes)attributes);
        }
    }
        
    public void endElement(String uri, String localName, String qName) throws SAXException {
        storeStructure(T_END);
    }
    
    public void characters(char ch[], int start, int length) throws SAXException {
        storeContentCharacters(T_TEXT_AS_CHAR_ARRAY, ch, 0, length);                
    }
    
    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
        characters(ch, start, length);
    }
    
    public void processingInstruction(String target, String data) throws SAXException {
        storeStructure(T_PROCESSING_INSTRUCTION);
        storeStructureString(target);
        storeStructureString(data);
    }
            
    public void comment(char[] ch, int start, int length) throws SAXException {
        storeContentCharacters(T_COMMENT_AS_CHAR_ARRAY, ch, start, length);
    }

    //
    
    public void octets(String URI, int algorithm, byte[] b, int start, int length) throws SAXException {
    }

    public void object(String URI, int algorithm, Object o) throws SAXException {
        storeStructure(T_TEXT_AS_OBJECT);
        storeContentObject(algorithm, o);
    }

    //
            
    private void cacheNamespaceAttribute(String prefix, String uri) {
        _namespaceAttributes[_namespaceAttributesPtr++] = prefix;
        _namespaceAttributes[_namespaceAttributesPtr++] = uri;
        
        if (_namespaceAttributesPtr == _namespaceAttributes.length) {
            final String[] namespaceAttributes = new String[_namespaceAttributesPtr * 3 / 2 + 1];
            System.arraycopy(_namespaceAttributes, 0, namespaceAttributes, 0, _namespaceAttributesPtr);
            _namespaceAttributes = namespaceAttributes;
        }
    }

    private void storeNamespaceAttributes() {
        for (int i = 0; i < _namespaceAttributesPtr; i += 2) {
            int item = T_NAMESPACE_ATTRIBUTE;
            if (_namespaceAttributes[i].length() > 0) {
                item |= FLAG_PREFIX;
                storeStructureString(_namespaceAttributes[i]);
            }
            if (_namespaceAttributes[i + 1].length() > 0) {
                item |= FLAG_URI;
                storeStructureString(_namespaceAttributes[i + 1]);
            }
            storeStructure(item);
        }
        _namespaceAttributesPtr = 0;
    }
    
    private void storeAttributes(EncodingAlgorithmAttributes attributes) {
        for (int i = 0; i < attributes.getLength(); i++) {
            
            Object o = attributes.getAlgorithmData(i);
            storeQualifiedName((o == null) ? T_ATTRIBUTE_LN : T_ATTRIBUTE_LN_OBJECT,
                    attributes.getURI(i),
                    attributes.getLocalName(i),
                    attributes.getQName(i));
            
            storeStructureString(attributes.getType(i));
            
            if (o == null) {
                storeContentString(attributes.getValue(i));
            } else {
                storeContentObject(attributes.getAlgorithmIndex(i), o);
            }
        }
    }
    
    private void storeContentObject(int algorithm, Object o) {
        Object previousValue = peekAtContentObject();
        if (previousValue != null && previousValue instanceof ValueInstance) {
            ((ValueInstance)previousValue).set(algorithm, o);
            storeContentObject(previousValue);
        } else {
            storeContentObject(new ValueInstance(algorithm, o));        
        }
    }
    
    private void storeQualifiedName(int item, String uri, String localName, String qName) {
        if (uri.length() > 0) {
            item |= FLAG_URI;
            storeStructureString(uri);
        }

        storeStructureString(localName);

        if (qName.indexOf(':') >= 0) {
            item |= FLAG_QUALIFIED_NAME;
            storeStructureString(qName);
        }

        storeStructure(item);
    }    
    
    
    // Empty methods for SAX handlers
    
    // Entity resolver handler
    
    public InputSource resolveEntity (String publicId, String systemId)
	throws IOException, SAXException {
	return null;
    }
        
    // DTD handler
    
    public void notationDecl (String name, String publicId, String systemId)
	throws SAXException { }
    
    public void unparsedEntityDecl (String name, String publicId,
				    String systemId, String notationName)
	throws SAXException { }
        
    // Content handler
    
    public void setDocumentLocator (Locator locator) { }
        
    public void endPrefixMapping (String prefix) throws SAXException { }
    
    public void skippedEntity (String name) throws SAXException { }

    // Lexical handler 
    
    public void startDTD(String name, String publicId, String systemId) throws SAXException { }
    
    public void endDTD() throws SAXException { }
    
    public void startEntity(String name) throws SAXException { }
    
    public void endEntity(String name) throws SAXException { }
    
    public void startCDATA() throws SAXException { }
    
    public void endCDATA() throws SAXException { }
    
    // Error handler
    
    public void warning(SAXParseException e) throws SAXException { }
    
    public void error(SAXParseException e) throws SAXException { }
    
    public void fatalError(SAXParseException e) throws SAXException {
	throw e;
    }    

}
