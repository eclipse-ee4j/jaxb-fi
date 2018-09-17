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

import com.sun.xml.fastinfoset.types.XSDataType;
import com.sun.xml.fastinfoset.types.LexicalSpaceConvertor;
import com.sun.xml.stream.buffer.AbstractCreator;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.sax.Features;
import com.sun.xml.stream.buffer.sax.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 * A creator of a {@link XMLStreamBuffer} that that produces the XML infoset
 * using a {@link XMLReader} and converts text content and attribute values from
 * the lexical space to value space.
 */
public class TypedSAXBufferCreator extends AbstractCreator 
        implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler, LexicalHandler {
    /**
     * The map of element local names to sets of types.
     */
    protected Map<String, Set<XSDataType>> _elements;
    
    /**
     * The map of attribute local names to sets of types.
     */
    protected Map<String, Set<XSDataType>> _attributes;
    
    protected String[] _namespaceAttributes = new String[16 * 2];
    
    protected int _namespaceAttributesPtr;

    protected AccessibleStringBuilder _textContent = new AccessibleStringBuilder();
    
    protected boolean _storeTextContent;

    protected Set<XSDataType> _textContentTypes;
    
    public static XMLStreamBuffer create(
            Map<String, Set<XSDataType>> elements, 
            Map<String, Set<XSDataType>> attributes, 
            InputStream in) 
            throws SAXException,IOException, ParserConfigurationException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        return createNewBufferFromXMLReader(elements, attributes,
                spf.newSAXParser().getXMLReader(), in);
    }
    
    public static XMLStreamBuffer createNewBufferFromXMLReader(
            Map<String, Set<XSDataType>> elements, 
            Map<String, Set<XSDataType>> attributes, 
            XMLReader reader, 
            InputStream in) 
            throws SAXException,IOException, ParserConfigurationException {
        return createNewBufferFromXMLReader(elements, attributes, reader, in, null);
    }
    
    public static XMLStreamBuffer createNewBufferFromXMLReader(
            Map<String, Set<XSDataType>> elements, 
            Map<String, Set<XSDataType>> attributes, 
            XMLReader reader, 
            InputStream in,
            String systemId) 
            throws SAXException,IOException, ParserConfigurationException {
        TypedSAXBufferCreator t = new TypedSAXBufferCreator(elements, attributes);
        
        return t.create(reader, in, systemId);
    }
    
    public TypedSAXBufferCreator(Map<String, Set<XSDataType>> elements, 
            Map<String, Set<XSDataType>> attributes) {
        _elements = elements;
        _attributes = attributes;
    }
    
    public TypedSAXBufferCreator(Map<String, Set<XSDataType>> elements, 
            Map<String, Set<XSDataType>> attributes, 
            MutableXMLStreamBuffer buffer) {
        this(elements, attributes);
        
        setBuffer(buffer);
    }
    
    public MutableXMLStreamBuffer create(XMLReader reader, InputStream in) throws IOException, SAXException {
        return create(reader, in, null);
    }
    
    public MutableXMLStreamBuffer create(XMLReader reader, InputStream in, 
            String systemId) throws IOException, SAXException {
        if (_buffer == null) {
            createBuffer();
        }
        
        reader.setContentHandler(this);
        reader.setProperty(Properties.LEXICAL_HANDLER_PROPERTY, this);
        
        try {
            setHasInternedStrings(reader.getFeature(Features.STRING_INTERNING_FEATURE));
        } catch (SAXException e) {
        }
        
        if (systemId != null) {
            InputSource s = new InputSource(systemId);
            s.setByteStream(in);
            reader.parse(s);
        } else {
            reader.parse(new InputSource(in));
        }
        
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
        storeTextContent();
        
        cacheNamespaceAttribute(prefix, uri);
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        storeTextContent();

        // Obtain the XS types associated with the local name of this 
        // element
        _textContentTypes = _elements.get(localName);
        
        storeQualifiedName(T_ELEMENT_LN,
                uri, localName, qName);
        
        // Has namespaces attributes
        if (_namespaceAttributesPtr > 0) {
            storeNamespaceAttributes();
        }
        
        // Has attributes
        if (attributes.getLength() > 0) {
            storeAttributes(attributes);
        }
    }
        
    public void endElement(String uri, String localName, String qName) throws SAXException {
        storeTextContent();
        
        storeStructure(T_END);
    }
    
    public void characters(char ch[], int start, int length) throws SAXException {
        _storeTextContent = true;
        _textContent.append(ch, start, length);
    }
    
    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
        characters(ch, start, length);
    }
    
    public void processingInstruction(String target, String data) throws SAXException {
        storeTextContent();
        
        storeStructure(T_PROCESSING_INSTRUCTION);
        storeStructureString(target);
        storeStructureString(data);
    }
            
    public void comment(char[] ch, int start, int length) throws SAXException {
        storeTextContent();
        
        storeContentCharacters(T_COMMENT_AS_CHAR_ARRAY, ch, start, length);
    }
    
    //
    
    private void storeTextContent() {
        if (_storeTextContent) {
            Object o = LexicalSpaceConvertor.convertToValueSpace(_textContentTypes, 
                    _textContent.getValue(), 0, _textContent.length(),
                    LexicalSpaceConvertor.LexicalPreference.charArray);
            
            final boolean isCharArray = (o instanceof char[]);
            
            if (o != null && !isCharArray) {
                storeStructure(T_TEXT_AS_OBJECT);
                storeContentObject(o);
            } else {
                if (isCharArray) {
                    char[] ch = (char[])o;
                    storeContentCharacters(T_TEXT_AS_CHAR_ARRAY, 
                            ch, 0, ch.length);
                } else {
                    storeContentCharacters(T_TEXT_AS_CHAR_ARRAY, 
                            _textContent.getValue(), 0, _textContent.length());
                }
            }
            
            _textContentTypes = null;
            _storeTextContent = false;
            _textContent.setLength(0);
        }
    }
        
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
    
    private void storeAttributes(Attributes attributes) {
        for (int i = 0; i < attributes.getLength(); i++) {
            String value = attributes.getValue(i);
            Object o = LexicalSpaceConvertor.convertToValueSpace(
                    _attributes.get(attributes.getLocalName(i)), value,
                    LexicalSpaceConvertor.LexicalPreference.string);
            boolean storeAsString = o == null || (o instanceof String);

            storeQualifiedName(storeAsString ? T_ATTRIBUTE_LN : T_ATTRIBUTE_LN_OBJECT,
                    attributes.getURI(i),
                    attributes.getLocalName(i),
                    attributes.getQName(i));
            
            storeStructureString(attributes.getType(i));
            
            if (storeAsString) {
                if (o instanceof String) {
                    storeContentString((String)o);                    
                } else {
                    storeContentString(value);
                }
            } else {
                storeContentObject(o);
            }
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
