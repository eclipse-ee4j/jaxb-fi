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

package com.sun.xml.fastinfoset.stax.util;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;


public class StAXParserWrapper implements XMLStreamReader{
    private XMLStreamReader _reader;
    
    /** Creates a new instance of StAXParserWrapper */
    public StAXParserWrapper() {
    }

    public StAXParserWrapper(XMLStreamReader reader) {
        _reader = reader;
    }
    public void setReader(XMLStreamReader reader) {
        _reader = reader;
    }
    public XMLStreamReader getReader() {
        return _reader;
    }

    @Override
    public int next() throws XMLStreamException
    {
        return _reader.next();
    }

    @Override
    public int nextTag() throws XMLStreamException
    {
        return _reader.nextTag();
    }

    @Override
    public String getElementText() throws XMLStreamException
    {
        return _reader.getElementText();
    }

    @Override
    public void require(int type, String namespaceURI, String localName) throws XMLStreamException
    {
        _reader.require(type,namespaceURI,localName);
    }

    @Override
    public boolean hasNext() throws XMLStreamException
    {
        return _reader.hasNext();
    }

    @Override
    public void close() throws XMLStreamException
    {
        _reader.close();
    }

    @Override
    public String getNamespaceURI(String prefix)
    {
        return _reader.getNamespaceURI(prefix);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return _reader.getNamespaceContext();
    }

    @Override
    public boolean isStartElement() {
        return _reader.isStartElement();
    }

    @Override
    public boolean isEndElement() {
        return _reader.isEndElement();
    }

    @Override
    public boolean isCharacters() {
    return _reader.isCharacters();
    }

    @Override
    public boolean isWhiteSpace() {
        return _reader.isWhiteSpace();
    }

    @Override
    public QName getAttributeName(int index) {
        return _reader.getAttributeName(index);
    }

    @Override
    public int getTextCharacters(int sourceStart, char[] target, int targetStart,
                                 int length) throws XMLStreamException
    {
        return _reader.getTextCharacters(sourceStart, target, targetStart, length);
    }

    @Override
    public String getAttributeValue(String namespaceUri,
                                    String localName)
    {
        return _reader.getAttributeValue(namespaceUri,localName);
    }
    @Override
    public int getAttributeCount() {
        return _reader.getAttributeCount();
    }
    @Override
    public String getAttributePrefix(int index) {
        return _reader.getAttributePrefix(index);
    }
    @Override
    public String getAttributeNamespace(int index) {
        return _reader.getAttributeNamespace(index);
    }
    @Override
    public String getAttributeLocalName(int index) {
        return _reader.getAttributeLocalName(index);
    }
    @Override
    public String getAttributeType(int index) {
        return _reader.getAttributeType(index);
    }
    @Override
    public String getAttributeValue(int index) {
        return _reader.getAttributeValue(index);
    }
    @Override
    public boolean isAttributeSpecified(int index) {
        return _reader.isAttributeSpecified(index);
    }

    @Override
    public int getNamespaceCount() {
        return _reader.getNamespaceCount();
    }
    @Override
    public String getNamespacePrefix(int index) {
        return _reader.getNamespacePrefix(index);
    }
    @Override
    public String getNamespaceURI(int index) {
        return _reader.getNamespaceURI(index);
    }

    @Override
    public int getEventType() {
        return _reader.getEventType();
    }

    @Override
    public String getText() {
        return _reader.getText();
    }

    @Override
    public char[] getTextCharacters() {
        return _reader.getTextCharacters();
    }

    @Override
    public int getTextStart() {
        return _reader.getTextStart();
    }

    @Override
    public int getTextLength() {
        return _reader.getTextLength();
    }

    @Override
    public String getEncoding() {
        return _reader.getEncoding();
    }

    @Override
    public boolean hasText() {
        return _reader.hasText();
    }

    @Override
    public Location getLocation() {
        return _reader.getLocation();
    }

    @Override
    public QName getName() {
        return _reader.getName();
    }

    @Override
    public String getLocalName() {
        return _reader.getLocalName();
    }

    @Override
    public boolean hasName() {
        return _reader.hasName();
    }

    @Override
    public String getNamespaceURI() {
        return _reader.getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        return _reader.getPrefix();
    }

    @Override
    public String getVersion() {
        return _reader.getVersion();
    }

    @Override
    public boolean isStandalone() {
        return _reader.isStandalone();
    }

    @Override
    public boolean standaloneSet() {
        return _reader.standaloneSet();
    }

    @Override
    public String getCharacterEncodingScheme() {
        return _reader.getCharacterEncodingScheme();
    }

    @Override
    public String getPITarget() {
        return _reader.getPITarget();
    }

    @Override
    public String getPIData() {
        return _reader.getPIData();
    }

    @Override
    public Object getProperty(String name) {
        return _reader.getProperty(name);
    }    
}
