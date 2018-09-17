/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.fime;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import com.sun.xml.fime.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.fime.jvnet.EncodingAlgorithm;
import com.sun.xml.fime.jvnet.EncodingAlgorithmException;
import com.sun.xml.fime.jvnet.EncodingAlgorithmIndexes;
import com.sun.xml.fime.jvnet.FastInfosetException;
import com.sun.xml.fime.jvnet.FastInfosetSerializer;
import com.sun.xml.fime.jvnet.ReferencedVocabulary;
import com.sun.xml.fime.jvnet.Vocabulary;
import com.sun.xml.fime.util.CharArrayIntMap;
import com.sun.xml.fime.util.KeyIntMap;
import com.sun.xml.fime.util.LocalNameQualifiedNamesMap;
import com.sun.xml.fime.util.MessageCenter;
import com.sun.xml.fime.util.StringIntMap;
import com.sun.xml.fime.util.SystemUtil;
import com.sun.xml.fime.vocab.SerializerVocabulary;

public abstract class Encoder implements FastInfosetSerializer {
    
    // Character encoding scheme system property
    public static final String CHARACTER_ENCODING_SCHEME_SYSTEM_PROPERTY =
        "com.sun.xml.fastinfoset.serializer.character-encoding-scheme";

    protected static String _characterEncodingSchemeSystemDefault = UTF_8;
        
    static {
        String p = SystemUtil.getProperty(CHARACTER_ENCODING_SCHEME_SYSTEM_PROPERTY,
            _characterEncodingSchemeSystemDefault);
        if (p.equals(UTF_16BE)) {
            _characterEncodingSchemeSystemDefault = UTF_16BE;
        }
    }

    protected boolean _encodingStringsAsUtf8 = true;
    
    protected int _nonIdentifyingStringOnThirdBitCES;

    protected int _nonIdentifyingStringOnFirstBitCES;
    
    protected Hashtable _registeredEncodingAlgorithms = new Hashtable();

    protected SerializerVocabulary _v;

    protected boolean _vIsInternal;

    protected boolean _terminate = false;

    protected int _b;

    protected OutputStream _s;

    protected char[] _charBuffer = new char[512];

    protected byte[] _octetBuffer = new byte[1024];
    
    protected int _octetBufferIndex;
   
    protected int _markIndex = -1;

    
    // FastInfosetSerializer

    public Encoder() {
        setCharacterEncodingScheme(_characterEncodingSchemeSystemDefault);
    }
    
    public void reset() {
        _terminate = false;
    }
    
    public void setCharacterEncodingScheme(String characterEncodingScheme) {
        if (characterEncodingScheme.equals(UTF_16BE)) { 
            _encodingStringsAsUtf8 = false;
            _nonIdentifyingStringOnThirdBitCES = EncodingConstants.CHARACTER_CHUNK | EncodingConstants.CHARACTER_CHUNK_UTF_16_FLAG;
            _nonIdentifyingStringOnFirstBitCES = EncodingConstants.NISTRING_UTF_16_FLAG;
        } else {
            _encodingStringsAsUtf8 = true;
            _nonIdentifyingStringOnThirdBitCES = EncodingConstants.CHARACTER_CHUNK;
            _nonIdentifyingStringOnFirstBitCES = 0;
        }
    }
        
    public String getCharacterEncodingScheme() {
        return (_encodingStringsAsUtf8) ? UTF_8 : UTF_16BE;
    }
    
    public void setRegisteredEncodingAlgorithms(Hashtable algorithms) {
        _registeredEncodingAlgorithms = algorithms;
        if (_registeredEncodingAlgorithms == null) {
            _registeredEncodingAlgorithms = new Hashtable();
        }
    }

    public Hashtable getRegisteredEncodingAlgorithms() {
        return _registeredEncodingAlgorithms;
    }

    public void setExternalVocabulary(ReferencedVocabulary referencedVocabulary) {
        throw new RuntimeException();
    }

    public void setIntitialVocabulary(Vocabulary initialVocabulary) {
        throw new RuntimeException();
    }

    public void setDynamicVocabulary(Vocabulary dynamicVocabulary) {
        throw new RuntimeException();
    }

    public Vocabulary getDynamicVocabulary() {
        throw new RuntimeException();
    }

    public Vocabulary getFinalVocabulary() {
        throw new RuntimeException();
    }

    
    
    public void setOutputStream(OutputStream s) {
        _octetBufferIndex = 0;
        _markIndex = -1;
        _s = s;
    }

    public void setVocabulary(SerializerVocabulary vocabulary) {
        _v = vocabulary;
        _vIsInternal = false;
    }

    protected final void encodeHeader(boolean encodeXmlDecl) throws IOException {
        if (encodeXmlDecl) {
            _s.write(EncodingConstants.XML_DECLARATION_VALUES[0]);
        }
        _s.write(EncodingConstants.BINARY_HEADER);
    }

    protected final void encodeInitialVocabulary() throws IOException {
        if (_v == null) {
            _v = new SerializerVocabulary();
            _vIsInternal = true;
        } else if (_vIsInternal) {
            _v.clear();
        }

        if (_v.hasInitialVocabulary()) {
            _b = EncodingConstants.DOCUMENT_INITIAL_VOCABULARY_FLAG;
            write(_b);

            SerializerVocabulary initialVocabulary = _v.getReadOnlyVocabulary();

            // TODO check for contents of vocabulary to assign bits
            if (initialVocabulary.hasExternalVocabulary()) {
                _b = EncodingConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG;
                write(_b);
                write(0);
            }

            if (initialVocabulary.hasExternalVocabulary()) {
                encodeNonEmptyOctetStringOnSecondBit(_v.getExternalVocabularyURI().toString());
            }

            // TODO check for contents of vocabulary to encode values
        } else if (_v.hasExternalVocabulary()) {
            _b = EncodingConstants.DOCUMENT_INITIAL_VOCABULARY_FLAG;
            write(_b);

            _b = EncodingConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG;
            write(_b);
            write(0);

            encodeNonEmptyOctetStringOnSecondBit(_v.getExternalVocabularyURI().toString());
        } else {
            write(0);
        }
    }

    protected final void encodeDocumentTermination() throws IOException {
        encodeElementTermination();
        encodeTermination();
        _flush();
        _s.flush();
    }

    protected final void encodeElementTermination() throws IOException {
        _terminate = true;
        switch (_b) {
            case EncodingConstants.TERMINATOR:
                _b = EncodingConstants.DOUBLE_TERMINATOR;
                break;
            case EncodingConstants.DOUBLE_TERMINATOR:
                write(EncodingConstants.DOUBLE_TERMINATOR);
            default:
                _b = EncodingConstants.TERMINATOR;
        }
    }

    protected final void encodeTermination() throws IOException {
        if (_terminate) {
            write(_b);
            _terminate = false;
        }
    }

    protected final void encodeNamespaceAttribute(String prefix, String uri) throws IOException {
        _b = EncodingConstants.NAMESPACE_ATTRIBUTE;
        if (prefix != "") {
            _b |= EncodingConstants.NAMESPACE_ATTRIBUTE_PREFIX_FLAG;
        }
        if (uri != "") {
            _b |= EncodingConstants.NAMESPACE_ATTRIBUTE_NAME_FLAG;
        }

        // NOTE a prefix with out a namespace name is an undeclaration
        // of the namespace bound to the prefix
        // TODO needs to investigate how the startPrefixMapping works in
        // relation to undeclaration

        write(_b);

        if (prefix != "") {
            encodeIdentifyingNonEmptyStringOnFirstBit(prefix, _v.prefix);
        }
        if (uri != "") {
            encodeIdentifyingNonEmptyStringOnFirstBit(uri, _v.namespaceName);
        }
    }

    protected final void encodeCharacters(char[] ch, int start, int length) throws IOException {
        final boolean addToTable = (length < _v.characterContentChunkSizeContraint) ? true : false;
        encodeNonIdentifyingStringOnThirdBit(ch, start, length, _v.characterContentChunk, addToTable, true);
    }

    protected final void encodeCharactersNoClone(char[] ch, int start, int length) throws IOException {
        final boolean addToTable = (length < _v.characterContentChunkSizeContraint) ? true : false;
        encodeNonIdentifyingStringOnThirdBit(ch, start, length, _v.characterContentChunk, addToTable, false);
    }
    
    protected final void encodeFourBitCharacters(int id, int[] table, char[] ch, int start, int length) throws FastInfosetException, IOException {
        // This procedure assumes that id <= 64
        _b = (length < _v.characterContentChunkSizeContraint) ?
            EncodingConstants.CHARACTER_CHUNK | EncodingConstants.CHARACTER_CHUNK_RESTRICTED_ALPHABET_FLAG | EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG :
            EncodingConstants.CHARACTER_CHUNK | EncodingConstants.CHARACTER_CHUNK_RESTRICTED_ALPHABET_FLAG;
        write (_b);

        // Encode bottom 6 bits of enoding algorithm id
        _b = id << 2;

        encodeNonEmptyFourBitCharacterStringOnSeventhBit(table, ch, start, length);        
    }
    
    protected final void encodeAlphabetCharacters(String alphabet, char[] ch, int start, int length) throws FastInfosetException, IOException {
        int id = _v.restrictedAlphabet.get(alphabet);
        if (id == KeyIntMap.NOT_PRESENT) {
            throw new FastInfosetException(MessageCenter.getString("message.restrictedAlphabetNotPresent"));
        }
        id += EncodingConstants.RESTRICTED_ALPHABET_APPLICATION_START;
        
        _b = (length < _v.characterContentChunkSizeContraint) ?
            EncodingConstants.CHARACTER_CHUNK | EncodingConstants.CHARACTER_CHUNK_RESTRICTED_ALPHABET_FLAG | EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG :
            EncodingConstants.CHARACTER_CHUNK | EncodingConstants.CHARACTER_CHUNK_RESTRICTED_ALPHABET_FLAG;        
        _b |= (id & 0xC0) >> 6;
        write (_b);

        // Encode bottom 6 bits of enoding algorithm id
        _b = (id & 0x3F) << 2;

        encodeNonEmptyNBitCharacterStringOnSeventhBit(alphabet, ch, start, length);        
    }
            
    protected final void encodeProcessingInstruction(String target, String data) throws IOException {
        write(EncodingConstants.PROCESSING_INSTRUCTION);

        // Target
        encodeIdentifyingNonEmptyStringOnFirstBit(target, _v.otherNCName);

        // Data
        boolean addToTable = (data.length() < _v.characterContentChunkSizeContraint) ? true : false;
        encodeNonIdentifyingStringOnFirstBit(data, _v.otherString, addToTable);
    }

    protected final void encodeComment(char[] ch, int start, int length) throws IOException {
        write(EncodingConstants.COMMENT);

        boolean addToTable = (length < _v.characterContentChunkSizeContraint) ? true : false;
        encodeNonIdentifyingStringOnFirstBit(ch, start, length, _v.otherString, addToTable, true);
    }

    protected final void encodeCommentNoClone(char[] ch, int start, int length) throws IOException {
        write(EncodingConstants.COMMENT);

        boolean addToTable = (length < _v.characterContentChunkSizeContraint) ? true : false;
        encodeNonIdentifyingStringOnFirstBit(ch, start, length, _v.otherString, addToTable, false);
    }

    /*
     * C.18
     */
    protected final void encodeElementQualifiedNameOnThirdBit(String namespaceURI, String prefix, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = _v.elementName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; i++) {
                if ((prefix == names[i].prefix || prefix.equals(names[i].prefix))
                        && (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName))) {
                    encodeNonZeroIntegerOnThirdBit(names[i].index);
                    return;
                }
            }
        }

        encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, prefix,
                localName, entry);
    }

    /*
     * C.18
     */
    protected final void encodeLiteralElementQualifiedNameOnThirdBit(String namespaceURI, String prefix, String localName,
            LocalNameQualifiedNamesMap.Entry entry) throws IOException {
        QualifiedName name = new QualifiedName(prefix, namespaceURI, localName, "", _v.elementName.getNextIndex());
        entry.addQualifiedName(name);

        int namespaceURIIndex = KeyIntMap.NOT_PRESENT;
        int prefixIndex = KeyIntMap.NOT_PRESENT;
        if (namespaceURI != "") {
            namespaceURIIndex = _v.namespaceName.get(namespaceURI);
            if (namespaceURIIndex == KeyIntMap.NOT_PRESENT) {
                throw new IOException(MessageCenter.getString("message.namespaceURINotIndexed", new Object[]{namespaceURI}));
            }

            if (prefix != "") {
                prefixIndex = _v.prefix.get(prefix);
                if (prefixIndex == KeyIntMap.NOT_PRESENT) {
                    throw new IOException(MessageCenter.getString("message.prefixNotIndexed", new Object[]{prefix}));
                }
            }
        }

        int localNameIndex = _v.localName.obtainIndex(localName);

        _b |= EncodingConstants.ELEMENT_LITERAL_QNAME_FLAG;
        if (namespaceURIIndex >= 0) {
            _b |= EncodingConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG;
            if (prefixIndex >= 0) {
                _b |= EncodingConstants.LITERAL_QNAME_PREFIX_FLAG;
            }
        }
        write(_b);

        if (namespaceURIIndex >= 0) {
            if (prefixIndex >= 0) {
                encodeNonZeroIntegerOnSecondBitFirstBitOne(prefixIndex);
            }
            encodeNonZeroIntegerOnSecondBitFirstBitOne(namespaceURIIndex);
        }

        if (localNameIndex >= 0) {
            encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
        } else {
            encodeNonEmptyOctetStringOnSecondBit(localName);
        }
    }

    /*
     * C.17
     */
    protected final void encodeAttributeQualifiedNameOnSecondBit(String namespaceURI, String prefix, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = _v.attributeName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; i++) {
                if ((prefix == names[i].prefix || prefix.equals(names[i].prefix))
                        && (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName))) {
                    encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
                    return;
                }
            }
        }

        encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, prefix,
                localName, entry);
    }

    /*
     * C.17
     */
    protected final boolean encodeLiteralAttributeQualifiedNameOnSecondBit(String namespaceURI, String prefix, String localName,
                LocalNameQualifiedNamesMap.Entry entry) throws IOException {
        int namespaceURIIndex = KeyIntMap.NOT_PRESENT;
        int prefixIndex = KeyIntMap.NOT_PRESENT;
        if (namespaceURI != "") {
            namespaceURIIndex = _v.namespaceName.get(namespaceURI);
            if (namespaceURIIndex == KeyIntMap.NOT_PRESENT) {
                if (namespaceURI == EncodingConstants.XMLNS_NAMESPACE_NAME || 
                        namespaceURI.equals(EncodingConstants.XMLNS_NAMESPACE_NAME)) {
                    return false;
                } else {
                    throw new IOException(MessageCenter.getString("message.namespaceURINotIndexed", new Object[]{namespaceURI}));
                }
            }
            
            if (prefix != "") {
                prefixIndex = _v.prefix.get(prefix);
                if (prefixIndex == KeyIntMap.NOT_PRESENT) {
                    throw new IOException(MessageCenter.getString("message.prefixNotIndexed", new Object[]{prefix}));
                }
            }
        }

        int localNameIndex = _v.localName.obtainIndex(localName);

        QualifiedName name = new QualifiedName(prefix, namespaceURI, localName, "", _v.attributeName.getNextIndex());
        entry.addQualifiedName(name);

        _b = EncodingConstants.ATTRIBUTE_LITERAL_QNAME_FLAG;
        if (namespaceURI != "") {
            _b |= EncodingConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG;
            if (prefix != "") {
                _b |= EncodingConstants.LITERAL_QNAME_PREFIX_FLAG;
            }
        }

        write(_b);

        if (namespaceURIIndex >= 0) {
            if (prefixIndex >= 0) {
                encodeNonZeroIntegerOnSecondBitFirstBitOne(prefixIndex);
            }
            encodeNonZeroIntegerOnSecondBitFirstBitOne(namespaceURIIndex);
        } else if (namespaceURI != "") {
            // XML prefix and namespace name
            encodeNonEmptyOctetStringOnSecondBit("xml");
            encodeNonEmptyOctetStringOnSecondBit("http://www.w3.org/XML/1998/namespace");
        }

        if (localNameIndex >= 0) {
            encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
        } else {
            encodeNonEmptyOctetStringOnSecondBit(localName);
        }
        
        return true;
    }

    /*
     * C.14
     */
    protected final void encodeNonIdentifyingStringOnFirstBit(String s, StringIntMap map, boolean addToTable) throws IOException {
        if (s == null || s.length() == 0) {
            // C.26 an index (first bit '1') with seven '1' bits for an empty string
            write(0xFF);
        } else {
            if (addToTable) {
                int index = map.obtainIndex(s);
                if (index == KeyIntMap.NOT_PRESENT) {
                    _b = EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG | _nonIdentifyingStringOnFirstBitCES;
                    encodeNonEmptyCharacterStringOnFifthBit(s);
                } else {
                    encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
                }
            } else {
                _b = _nonIdentifyingStringOnFirstBitCES;
                encodeNonEmptyCharacterStringOnFifthBit(s);
            }
        }
    }

    /*
     * C.14
     */
    protected final void encodeNonIdentifyingStringOnFirstBit(String s, CharArrayIntMap map, boolean addToTable) throws IOException {
        if (s == null || s.length() == 0) {
            // C.26 an index (first bit '1') with seven '1' bits for an empty string
            write(0xFF);
        } else {
            if (addToTable) {
                final char[] ch = s.toCharArray();
                final int length = s.length();
                int index = map.obtainIndex(ch, 0, length, false);
                if (index == KeyIntMap.NOT_PRESENT) {
                    _b = EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG | _nonIdentifyingStringOnFirstBitCES;
                    encodeNonEmptyCharacterStringOnFifthBit(ch, 0, length);
                } else {
                    encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
                }
            } else {
                _b = _nonIdentifyingStringOnFirstBitCES;
                encodeNonEmptyCharacterStringOnFifthBit(s);
            }
        }
    }

    /*
     * C.14
     */
    protected final void encodeNonIdentifyingStringOnFirstBit(char[] array, int start, int length, CharArrayIntMap map,
            boolean addToTable, boolean clone) throws IOException {
        if (length == 0) {
            // C.26 an index (first bit '1') with seven '1' bits for an empty string
            write(0xFF);
        } else {
            if (addToTable) {
                int index = map.obtainIndex(array, start, length, clone);
                if (index == KeyIntMap.NOT_PRESENT) {
                    _b = EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG | _nonIdentifyingStringOnFirstBitCES;
                    encodeNonEmptyCharacterStringOnFifthBit(array, start, length);
                } else {
                    encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
                }
            } else {
                _b = _nonIdentifyingStringOnFirstBitCES;
                encodeNonEmptyCharacterStringOnFifthBit(array, start, length);
            }
        }
    }

    /*
     * C.14
     */
    protected final void encodeNonIdentifyingStringOnFirstBit(String URI, int id, Object data) throws FastInfosetException, IOException {
        if (URI != null) {
            id = _v.encodingAlgorithm.get(URI);
            if (id == KeyIntMap.NOT_PRESENT) {
                throw new EncodingAlgorithmException(MessageCenter.getString("message.EncodingAlgorithmURI", new Object[]{URI}));
            }
            id += EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START;

            EncodingAlgorithm ea = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(URI);
            if (ea != null) {
                encodeAIIObjectAlgorithmData(URI, id, data, ea);
            } else {
                if (data instanceof byte[]) {
                    byte[] d = (byte[])data;
                    encodeAIIOctetAlgorithmData(id, d, 0, d.length);
                } else {
                    throw new EncodingAlgorithmException(MessageCenter.getString("message.nullEncodingAlgorithmURI"));
                }
            }
        } else if (id <= EncodingConstants.ENCODING_ALGORITHM_BUILTIN_END) {
            int length = 0;
            switch(id) {
                case EncodingAlgorithmIndexes.HEXADECIMAL:
                    length = ((byte[])data).length;
                    break;
                case EncodingAlgorithmIndexes.BASE64:
                    length = ((byte[])data).length;
                    break;
                case EncodingAlgorithmIndexes.SHORT:
                    length = ((short[])data).length;
                    break;
                case EncodingAlgorithmIndexes.INT:
                    length = ((int[])data).length;
                    break;
                case EncodingAlgorithmIndexes.LONG:
                    length = ((long[])data).length;
                    break;
                case EncodingAlgorithmIndexes.BOOLEAN:
                    length = ((boolean[])data).length;
                    break;
                case EncodingAlgorithmIndexes.FLOAT:
                    length = ((float[])data).length;
                    break;
                case EncodingAlgorithmIndexes.DOUBLE:
                    length = ((double[])data).length;
                    break;
                case EncodingAlgorithmIndexes.UUID:
                    length = ((long[])data).length;
                    break;
                case EncodingAlgorithmIndexes.CDATA:
                    throw new RuntimeException(MessageCenter.getString("message.CDATA"));
                default:
                    throw new EncodingAlgorithmException(MessageCenter.getString("message.UnsupportedBuiltInAlgorithm", new Object[]{new Integer(id)}));
            }
            encodeAIIBuiltInAlgorithmData(id, data, 0, length);
        } else if (id >= EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START) {
            if (data instanceof byte[]) {
                byte[] d = (byte[])data;
                encodeAIIOctetAlgorithmData(id, d, 0, d.length);
            } else {
                throw new EncodingAlgorithmException(MessageCenter.getString("message.nullEncodingAlgorithmURI"));
            }
        } else {
            throw new EncodingAlgorithmException(MessageCenter.getString("message.identifiers10to31Reserved"));
        }
    }

    protected final void encodeAIIOctetAlgorithmData(int id, byte[] d, int start, int length) throws IOException {
        // Encode identification and top four bits of encoding algorithm id
        write (EncodingConstants.NISTRING_ENCODING_ALGORITHM_FLAG |
                ((id & 0xF0) >> 4));

        // Encode bottom 4 bits of enoding algorithm id
        _b = (id & 0x0F) << 4;

        // Encode the length
        encodeNonZeroOctetStringLengthOnFifthBit(length);

        write(d, start, length);
    }

    protected final void encodeAIIObjectAlgorithmData(String URI, int id, Object data, EncodingAlgorithm ea) throws FastInfosetException, IOException {
        // Encode identification and top four bits of encoding algorithm id
        write (EncodingConstants.NISTRING_ENCODING_ALGORITHM_FLAG |
                ((id & 0xF0) >> 4));

        // Encode bottom 4 bits of enoding algorithm id
        _b = (id & 0x0F) << 4;

        _encodingBufferOutputStream.reset();
        ea.encodeToOutputStream(data, _encodingBufferOutputStream);
        encodeNonZeroOctetStringLengthOnFifthBit(_encodingBufferIndex);
        write(_encodingBuffer, _encodingBufferIndex);
    }

    protected final void encodeAIIBuiltInAlgorithmData(int id, Object o, int start, int length) throws IOException {
        // Encode identification and top four bits of encoding algorithm id
        write (EncodingConstants.NISTRING_ENCODING_ALGORITHM_FLAG |
                ((id & 0xF0) >> 4));

        // Encode bottom 4 bits of enoding algorithm id
        _b = (id & 0x0F) << 4;

        final int octetLength = BuiltInEncodingAlgorithmFactory.table[id].
                    getOctetLengthFromPrimitiveLength(length);

        encodeNonZeroOctetStringLengthOnFifthBit(octetLength);

        ensureSize(octetLength);
        BuiltInEncodingAlgorithmFactory.table[id].
                encodeToBytes(o, start, length, _octetBuffer, _octetBufferIndex);
        _octetBufferIndex += octetLength;
    }

    /*
     * C.15
     */
    protected final void encodeNonIdentifyingStringOnThirdBit(char[] array, int start, int length,
            CharArrayIntMap map, boolean addToTable, boolean clone) throws IOException {
        // length cannot be zero since sequence of CIIs has to be > 0

        if (addToTable) {
            int index = map.obtainIndex(array, start, length, clone);
            if (index == KeyIntMap.NOT_PRESENT) {
                _b = EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG | 
                        _nonIdentifyingStringOnThirdBitCES;
                encodeNonEmptyCharacterStringOnSeventhBit(array, start, length);
            } else {
                _b = EncodingConstants.CHARACTER_CHUNK | 0x20;
                encodeNonZeroIntegerOnFourthBit(index);
            }
        } else {
            _b = _nonIdentifyingStringOnThirdBitCES;
            encodeNonEmptyCharacterStringOnSeventhBit(array, start, length);
        }
    }

    protected final void encodeNonIdentifyingStringOnThirdBit(String URI, int id, Object data) throws FastInfosetException, IOException {
        if (URI != null) {
            id = _v.encodingAlgorithm.get(URI);
            if (id == KeyIntMap.NOT_PRESENT) {
                throw new EncodingAlgorithmException(MessageCenter.getString("message.EncodingAlgorithmURI", new Object[]{URI}));
            }
            id += EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START;

            EncodingAlgorithm ea = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(URI);
            if (ea != null) {
                encodeCIIObjectAlgorithmData(URI, id, data, ea);
            } else {
                if (data instanceof byte[]) {
                    byte[] d = (byte[])data;
                    encodeCIIOctetAlgorithmData(id, d, 0, d.length);
                } else {
                    throw new EncodingAlgorithmException(MessageCenter.getString("message.nullEncodingAlgorithmURI"));
                }
            }
        } else if (id <= EncodingConstants.ENCODING_ALGORITHM_BUILTIN_END) {
            int length = 0;
            switch(id) {
                case EncodingAlgorithmIndexes.HEXADECIMAL:
                    length = ((byte[])data).length;
                    break;
                case EncodingAlgorithmIndexes.BASE64:
                    length = ((byte[])data).length;
                    break;
                case EncodingAlgorithmIndexes.SHORT:
                    length = ((short[])data).length;
                    break;
                case EncodingAlgorithmIndexes.INT:
                    length = ((int[])data).length;
                    break;
                case EncodingAlgorithmIndexes.LONG:
                    length = ((int[])data).length;
                    break;
                case EncodingAlgorithmIndexes.BOOLEAN:
                    length = ((boolean[])data).length;
                    break;
                case EncodingAlgorithmIndexes.FLOAT:
                    length = ((float[])data).length;
                    break;
                case EncodingAlgorithmIndexes.DOUBLE:
                    length = ((double[])data).length;
                    break;
                case EncodingAlgorithmIndexes.UUID:
                    length = ((int[])data).length;
                    break;
                case EncodingAlgorithmIndexes.CDATA:
                    throw new RuntimeException(MessageCenter.getString("message.CDATA"));
                default:
                    throw new EncodingAlgorithmException(MessageCenter.getString("message.UnsupportedBuiltInAlgorithm", new Object[]{new Integer(id)}));
            }
            encodeCIIBuiltInAlgorithmData(id, data, 0, length);
        } else if (id >= EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START) {
            if (data instanceof byte[]) {
                byte[] d = (byte[])data;
                encodeCIIOctetAlgorithmData(id, d, 0, d.length);
            } else {
                throw new EncodingAlgorithmException(MessageCenter.getString("message.nullEncodingAlgorithmURI"));
            }
        } else {
            throw new EncodingAlgorithmException(MessageCenter.getString("message.identifiers10to31Reserved"));
        }
    }

    protected final void encodeNonIdentifyingStringOnThirdBit(String URI, int id, byte[] b, int start, int length) throws FastInfosetException, IOException {
        if (URI != null) {
            id = _v.encodingAlgorithm.get(URI);
            if (id == KeyIntMap.NOT_PRESENT) {
                throw new EncodingAlgorithmException(MessageCenter.getString("message.EncodingAlgorithmURI", new Object[]{URI}));
            }
            id += EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START;
        }

        encodeCIIOctetAlgorithmData(id, b, start, length);
    }

    protected final void encodeCIIOctetAlgorithmData(int id, byte[] d, int start, int length) throws IOException {
        // Encode identification and top two bits of encoding algorithm id
        write (EncodingConstants.CHARACTER_CHUNK | EncodingConstants.CHARACTER_CHUNK_ENCODING_ALGORITHM_FLAG |
                ((id & 0xC0) >> 6));

        // Encode bottom 6 bits of enoding algorithm id
        _b = (id & 0x3F) << 2;

        // Encode the length
        encodeNonZeroOctetStringLengthOnSenventhBit(length);

        write(d, start, length);
    }

    protected final void encodeCIIObjectAlgorithmData(String URI, int id, Object data, EncodingAlgorithm ea) throws FastInfosetException, IOException {
        // Encode identification and top two bits of encoding algorithm id
        write (EncodingConstants.CHARACTER_CHUNK | EncodingConstants.CHARACTER_CHUNK_ENCODING_ALGORITHM_FLAG |
                ((id & 0xC0) >> 6));

        // Encode bottom 6 bits of enoding algorithm id
        _b = (id & 0x3F) << 2;

        _encodingBufferOutputStream.reset();
        ea.encodeToOutputStream(data, _encodingBufferOutputStream);
        encodeNonZeroOctetStringLengthOnSenventhBit(_encodingBufferIndex);
        write(_encodingBuffer, _encodingBufferIndex);
    }

    protected final void encodeCIIBuiltInAlgorithmData(int id, Object o, int start, int length) throws FastInfosetException, IOException {
        // Encode identification and top two bits of encoding algorithm id
        write (EncodingConstants.CHARACTER_CHUNK | EncodingConstants.CHARACTER_CHUNK_ENCODING_ALGORITHM_FLAG |
                ((id & 0xC0) >> 6));

        // Encode bottom 6 bits of enoding algorithm id
        _b = (id & 0x3F) << 2;

        final int octetLength = BuiltInEncodingAlgorithmFactory.table[id].
                    getOctetLengthFromPrimitiveLength(length);

        encodeNonZeroOctetStringLengthOnSenventhBit(octetLength);

        ensureSize(octetLength);
        BuiltInEncodingAlgorithmFactory.table[id].
                encodeToBytes(o, start, length, _octetBuffer, _octetBufferIndex);
        _octetBufferIndex += octetLength;
    }

    protected final void encodeCIIBuiltInAlgorithmDataAsCDATA(char[] c, int start, int length) throws FastInfosetException, IOException {
        // Encode identification and top two bits of encoding algorithm id
        write (EncodingConstants.CHARACTER_CHUNK | EncodingConstants.CHARACTER_CHUNK_ENCODING_ALGORITHM_FLAG);

        // Encode bottom 6 bits of enoding algorithm id
        _b = EncodingAlgorithmIndexes.CDATA << 2;

        
        length = encodeUTF8String(c, start, length);
        encodeNonZeroOctetStringLengthOnSenventhBit(length);
        write(_encodingBuffer, length);
    }
    
    /*
     * C.13
     */
    protected final void encodeIdentifyingNonEmptyStringOnFirstBit(String s, StringIntMap map) throws IOException {
        int index = map.obtainIndex(s);
        if (index == KeyIntMap.NOT_PRESENT) {
            // _b = 0;
            encodeNonEmptyOctetStringOnSecondBit(s);
        } else {
            // _b = 0x80;
            encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
        }
    }

    /*
     * C.22
     */
    protected final void encodeNonEmptyOctetStringOnSecondBit(String s) throws IOException {
        final int length = encodeUTF8String(s);
        encodeNonZeroOctetStringLengthOnSecondBit(length);
        write(_encodingBuffer, length);
    }

    /*
     * C.22
     */
    protected final void encodeNonZeroOctetStringLengthOnSecondBit(int length) throws IOException {
        if (length < EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_SMALL_LIMIT) {
            // [1, 64]
            write(length - 1);
        } else if (length < EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_LIMIT) {
            // [65, 320]
            write(EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_FLAG); // 010 00000
            write(length - EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_SMALL_LIMIT);
        } else {
            // [321, 4294967296]
            write(EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_LARGE_FLAG); // 0110 0000
            length -= EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_LIMIT;
            write(length >>> 24);
            write((length >> 16) & 0xFF);
            write((length >> 8) & 0xFF);
            write(length & 0xFF);
        }
    }


    /*
     * C.23
     */
    protected final void encodeNonEmptyCharacterStringOnFifthBit(String s) throws IOException {
        final int length = (_encodingStringsAsUtf8) ? encodeUTF8String(s) : encodeUtf16String(s);
        encodeNonZeroOctetStringLengthOnFifthBit(length);
        write(_encodingBuffer, length);
    }

    /*
     * C.23
     */
    protected final void encodeNonEmptyCharacterStringOnFifthBit(char[] array, int start, int length) throws IOException {
        length = (_encodingStringsAsUtf8) ? encodeUTF8String(array, start, length) : encodeUtf16String(array, start, length);
        encodeNonZeroOctetStringLengthOnFifthBit(length);
        write(_encodingBuffer, length);
    }

    /*
     * C.23
     */
    protected final void encodeNonZeroOctetStringLengthOnFifthBit(int length) throws IOException {
        if (length < EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT) {
            // [1, 8]
            write(_b | (length - 1));
        } else if (length < EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT) {
            // [9, 264]
            write(_b | EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_FLAG); // 000010 00
            write(length - EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT);
        } else {
            // [265, 4294967296]
            write(_b | EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_LARGE_FLAG); // 000011 00
            length -= EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT;
            write(length >>> 24);
            write((length >> 16) & 0xFF);
            write((length >> 8) & 0xFF);
            write(length & 0xFF);
        }
    }

    /*
     * C.24
     */
    protected final void encodeNonEmptyCharacterStringOnSeventhBit(char[] array, int start, int length) throws IOException {
        length = (_encodingStringsAsUtf8) ? encodeUTF8String(array, start, length) : encodeUtf16String(array, start, length);
        encodeNonZeroOctetStringLengthOnSenventhBit(length);
        write(_encodingBuffer, length);
    }

    /*
     * C.24
     */
    protected final void encodeNonEmptyFourBitCharacterStringOnSeventhBit(int[] table, char[] ch, int start, int length) throws FastInfosetException, IOException {
        final int octetPairLength = length / 2;
        final int octetSingleLength = length % 2;
        
        // Encode the length
        encodeNonZeroOctetStringLengthOnSenventhBit(octetPairLength + octetSingleLength);

        ensureSize(octetPairLength + octetSingleLength);
        // Encode all pairs
        int v = 0;
        for (int i = 0; i < octetPairLength; i++) {
            v = (table[ch[start++]] << 4) | table[ch[start++]];
            if (v < 0) {
                throw new FastInfosetException(MessageCenter.getString("message.characterOutofAlphabetRange"));
            }
            _octetBuffer[_octetBufferIndex++] = (byte)v;
        }
        // Encode single character at end with termination bits
        if (octetSingleLength == 1) {
            v = (table[ch[start]] << 4) | 0x0F;
            if (v < 0) {
                throw new FastInfosetException(MessageCenter.getString("message.characterOutofAlphabetRange"));
            }
            _octetBuffer[_octetBufferIndex++] = (byte)v;
        }        
    }
    
    /*
     * C.24
     */
    protected final void encodeNonEmptyNBitCharacterStringOnSeventhBit(String alphabet, char[] ch, int start, int length) throws FastInfosetException, IOException {
        int bitsPerCharacter = 1;
        while ((1 << bitsPerCharacter) <= alphabet.length()) {
            bitsPerCharacter++;
        }
        final int bits = length * bitsPerCharacter;
        final int octets = bits / 8;
        final int bitsOfLastOctet = bits % 8;
        final int totalOctets = octets + ((bitsOfLastOctet > 0) ? 1 : 0);
        
        // Encode the length
        encodeNonZeroOctetStringLengthOnSenventhBit(totalOctets);

        resetBits();
        ensureSize(totalOctets);
        int v = 0;
        for (int i = 0; i < length; i++) {
            final char c = ch[start + i];
            // This is grotesquely slow, need to use hash table of character to int value
            for (v = 0; v < alphabet.length(); v++) {
                if (c == alphabet.charAt(v)) {
                    break;
                }
            }
            if (v == alphabet.length()) {
                throw new FastInfosetException(MessageCenter.getString("message.characterOutofAlphabetRange"));
            }
            writeBits(bitsPerCharacter, v);            
        }        
        
        if (bitsOfLastOctet > 0) {
            _b |= (1 << (8 - bitsOfLastOctet)) - 1;
            write(_b);
        }
    }

    protected int _bitsLeftInOctet;
    
    public final void resetBits() {
        _bitsLeftInOctet = 8;
        _b = 0;
    }
    
    protected final void writeBits(int bits, int v) throws IOException {
        while (bits > 0) {
            final int bit = (v & (1 << --bits)) > 0 ? 1 : 0;
            _b |= bit << (--_bitsLeftInOctet);
            if (_bitsLeftInOctet == 0) {
                write(_b);
                _bitsLeftInOctet = 8;
                _b = 0;
            }
        }
    }
    
    /*
     * C.24
     */
    protected final void encodeNonZeroOctetStringLengthOnSenventhBit(int length) throws IOException {
        if (length < EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT) {
            // [1, 2]
            write(_b | (length - 1));
        } else if (length < EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT) {
            // [3, 258]
            write(_b | EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_FLAG); // 00000010
            write(length - EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT);
        } else {
            // [259, 4294967296]
            write(_b | EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_LARGE_FLAG); // 00000011
            length -= EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT;
            write(length >>> 24);
            write((length >> 16) & 0xFF);
            write((length >> 8) & 0xFF);
            write(length & 0xFF);
        }
    }

    /*
     * C.25 (with first bit set to 1)
     *
     * i is a member of the interval [0, 1048575]
     *
     * In the specification the interval is [1, 1048576]
     *
     * The first bit of the first octet is set. As specified in C.13
     */
    protected final void encodeNonZeroIntegerOnSecondBitFirstBitOne(int i) throws IOException {
        if (i < EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT) {
            // [1, 64] ( [0, 63] ) 6 bits
            write(0x80 | i);
        } else if (i < EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT) {
            // [65, 8256] ( [64, 8255] ) 13 bits
            i -= EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
            _b = (0x80 | EncodingConstants.INTEGER_2ND_BIT_MEDIUM_FLAG) | (i >> 8); // 010 00000
            // _b = 0xC0 | (i >> 8); // 010 00000
            write(_b);
            write(i & 0xFF);
        } else {
            // [8257, 1048576] ( [8256, 1048575] ) 20 bits
            i -= EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
            _b = (0x80 | EncodingConstants.INTEGER_2ND_BIT_LARGE_FLAG) | (i >> 16); // 0110 0000
            // _b = 0xE0 | (i >> 16); // 0110 0000
            write(_b);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        }
    }

    /*
     * C.25 (with first bit set to 0)
     *
     * i is a member of the interval [0, 1048575]
     *
     * In the specification the interval is [1, 1048576]
     *
     * The first bit of the first octet is set. As specified in C.13
     */
    protected final void encodeNonZeroIntegerOnSecondBitFirstBitZero(int i) throws IOException {
        if (i < EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT) {
            // [1, 64] ( [0, 63] ) 6 bits
            write(i);
        } else if (i < EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT) {
            // [65, 8256] ( [64, 8255] ) 13 bits
            i -= EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
            _b = EncodingConstants.INTEGER_2ND_BIT_MEDIUM_FLAG | (i >> 8); // 010 00000
            write(_b);
            write(i & 0xFF);
        } else {
            // [8257, 1048576] ( [8256, 1048575] ) 20 bits
            i -= EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
            _b = EncodingConstants.INTEGER_2ND_BIT_LARGE_FLAG | (i >> 16); // 0110 0000
            write(_b);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        }
    }

    /*
     * C.27
     *
     * i is a member of the interval [0, 1048575]
     *
     * In the specification the interval is [1, 1048576]
     *
     */
    protected final void encodeNonZeroIntegerOnThirdBit(int i) throws IOException {
        if (i < EncodingConstants.INTEGER_3RD_BIT_SMALL_LIMIT) {
            // [1, 32] ( [0, 31] ) 5 bits
            write(_b | i);
        } else if (i < EncodingConstants.INTEGER_3RD_BIT_MEDIUM_LIMIT) {
            // [33, 2080] ( [32, 2079] ) 11 bits
            i -= EncodingConstants.INTEGER_3RD_BIT_SMALL_LIMIT;
            _b |= EncodingConstants.INTEGER_3RD_BIT_MEDIUM_FLAG | (i >> 8); // 00100 000
            write(_b);
            write(i & 0xFF);
        } else if (i < EncodingConstants.INTEGER_3RD_BIT_LARGE_LIMIT) {
            // [2081, 526368] ( [2080, 526367] ) 19 bits
            i -= EncodingConstants.INTEGER_3RD_BIT_MEDIUM_LIMIT;
            _b |= EncodingConstants.INTEGER_3RD_BIT_LARGE_FLAG | (i >> 16); // 00101 000
            write(_b);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        } else {
            // [526369, 1048576] ( [526368, 1048575] ) 20 bits
            i -= EncodingConstants.INTEGER_3RD_BIT_LARGE_LIMIT;
            _b |= EncodingConstants.INTEGER_3RD_BIT_LARGE_LARGE_FLAG; // 00110 000
            write(_b);
            write(i >> 16);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        }
    }

    /*
     * C.28
     *
     * i is a member of the interval [0, 1048575]
     *
     * In the specification the interval is [1, 1048576]
     */
    protected final void encodeNonZeroIntegerOnFourthBit(int i) throws IOException {
        if (i < EncodingConstants.INTEGER_4TH_BIT_SMALL_LIMIT) {
            // [1, 16] ( [0, 15] ) 4 bits
            write(_b | i);
        } else if (i < EncodingConstants.INTEGER_4TH_BIT_MEDIUM_LIMIT) {
            // [17, 1040] ( [16, 1039] ) 10 bits
            i -= EncodingConstants.INTEGER_4TH_BIT_SMALL_LIMIT;
            _b |= EncodingConstants.INTEGER_4TH_BIT_MEDIUM_FLAG | (i >> 8); // 000 100 00
            write(_b);
            write(i & 0xFF);
        } else if (i < EncodingConstants.INTEGER_4TH_BIT_LARGE_LIMIT) {
            // [1041, 263184] ( [1040, 263183] ) 18 bits
            i -= EncodingConstants.INTEGER_4TH_BIT_MEDIUM_LIMIT;
            _b |= EncodingConstants.INTEGER_4TH_BIT_LARGE_FLAG | (i >> 16); // 000 101 00
            write(_b);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        } else {
            // [263185, 1048576] ( [263184, 1048575] ) 20 bits
            i -= EncodingConstants.INTEGER_4TH_BIT_LARGE_LIMIT;
            _b |= EncodingConstants.INTEGER_4TH_BIT_LARGE_LARGE_FLAG; // 000 110 00
            write(_b);
            write(i >> 16);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        }
    }

    protected final void encodeNonEmptyUTF8StringAsOctetString(int b, String s, int[] constants) throws IOException {
        final char[] ch = s.toCharArray();
        encodeNonEmptyUTF8StringAsOctetString(b, ch, 0, ch.length, constants);
    }

    protected final void encodeNonEmptyUTF8StringAsOctetString(int b, char ch[], int start, int length, int[] constants) throws IOException {
        length = encodeUTF8String(ch, start, length);
        encodeNonZeroOctetStringLength(b, length, constants);
        write(_encodingBuffer, length);
    }

    protected final void encodeNonZeroOctetStringLength(int b, int length, int[] constants) throws IOException {
        if (length < constants[EncodingConstants.OCTET_STRING_LENGTH_SMALL_LIMIT]) {
            write(b | (length - 1));
        } else if (length < constants[EncodingConstants.OCTET_STRING_LENGTH_MEDIUM_LIMIT]) {
            write(b | constants[EncodingConstants.OCTET_STRING_LENGTH_MEDIUM_FLAG]);
            write(length - constants[EncodingConstants.OCTET_STRING_LENGTH_SMALL_LIMIT]);
        } else {
            write(b | constants[EncodingConstants.OCTET_STRING_LENGTH_LARGE_FLAG]);
            length -= constants[EncodingConstants.OCTET_STRING_LENGTH_MEDIUM_LIMIT];
            write(length >>> 24);
            write((length >> 16) & 0xFF);
            write((length >> 8) & 0xFF);
            write(length & 0xFF);
        }
    }

    protected final void encodeNonZeroInteger(int b, int i, int[] constants) throws IOException {
        if (i < constants[EncodingConstants.INTEGER_SMALL_LIMIT]) {
            write(b | i);
        } else if (i < constants[EncodingConstants.INTEGER_MEDIUM_LIMIT]) {
            i -= constants[EncodingConstants.INTEGER_SMALL_LIMIT];
            write(b | constants[EncodingConstants.INTEGER_MEDIUM_FLAG] | (i >> 8));
            write(i & 0xFF);
        } else if (i < constants[EncodingConstants.INTEGER_LARGE_LIMIT]) {
            i -= constants[EncodingConstants.INTEGER_MEDIUM_LIMIT];
            write(b | constants[EncodingConstants.INTEGER_LARGE_FLAG] | (i >> 16));
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        } else if (i < EncodingConstants.INTEGER_MAXIMUM_SIZE) {
            i -= constants[EncodingConstants.INTEGER_LARGE_LIMIT];
            write(b | constants[EncodingConstants.INTEGER_LARGE_LARGE_FLAG]);
            write(i >> 16);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        } else {
            throw new IOException(MessageCenter.getString("message.integerMaxSize", new Object[]{new Integer(EncodingConstants.INTEGER_MAXIMUM_SIZE)}));
        }
    }







    protected final int encodeUTF8String(String s) throws IOException {
        final int length = s.length();
        if (length < _charBuffer.length) {
            s.getChars(0, length, _charBuffer, 0);
            return encodeUTF8String(_charBuffer, 0, length);
        } else {
            char[] ch = s.toCharArray();
            return encodeUTF8String(ch, 0, length);
        }
    }
    
    protected final int encodeUtf16String(String s) throws IOException {
        final int length = s.length();
        if (length < _charBuffer.length) {
            s.getChars(0, length, _charBuffer, 0);
            return encodeUtf16String(_charBuffer, 0, length);
        } else {
            char[] ch = s.toCharArray();
            return encodeUtf16String(ch, 0, length);
        }
    }
    
    protected final void mark() throws IOException {
        _markIndex = _octetBufferIndex;
    }

    protected final void resetMark() throws IOException {
        _markIndex = -1;
    }

    protected final void write(int i) throws IOException {
        if (_octetBufferIndex < _octetBuffer.length) {
            _octetBuffer[_octetBufferIndex++] = (byte)i;
        } else {
            if (_markIndex == -1) {
                _s.write(_octetBuffer);
                _octetBufferIndex = 1;
                _octetBuffer[0] = (byte)i;
            } else {
                resize(_octetBuffer.length * 3 / 2);
                _octetBuffer[_octetBufferIndex++] = (byte)i;
            }
        }
    }

    protected final void write(byte[] b, int length) throws IOException {
        write(b, 0,  length);
    }

    protected final void write(byte[] b, int start, int length) throws IOException {
        if ((_octetBufferIndex + length) < _octetBuffer.length) {
            System.arraycopy(b, start, _octetBuffer, _octetBufferIndex, length);
            _octetBufferIndex += length;
        } else {
            if (_markIndex == -1) {
                _s.write(_octetBuffer, 0, _octetBufferIndex);
                _s.write(b, start, length);
                _octetBufferIndex = 0;
            } else {
                resize((_octetBuffer.length + length) * 3 / 2 + 1);
                System.arraycopy(b, start, _octetBuffer, _octetBufferIndex, length);
                _octetBufferIndex += length;
            }
        }
    }

    protected final void writeToBuffer(byte[] b, int length) {
        ensureSize(length);

        System.arraycopy(b, 0, _octetBuffer, _octetBufferIndex, length);
        _octetBufferIndex += length;
    }

    protected final void ensureSize(int length) {
        if ((_octetBufferIndex + length) > _octetBuffer.length) {
            resize((_octetBufferIndex + length) * 3 / 2 + 1);
        }
    }

    protected final void resize(int length) {
        byte[] b = new byte[length];
        System.arraycopy(_octetBuffer, 0, b, 0, _octetBufferIndex);
        _octetBuffer = b;
    }

    protected final void _flush() throws IOException {
        if (_octetBufferIndex > 0) {
            _s.write(_octetBuffer, 0, _octetBufferIndex);
            _octetBufferIndex = 0;
        }
    }

    protected class EncodingBufferOutputStream extends OutputStream {

        public void write(int b) throws IOException {
            if (_encodingBufferIndex < _encodingBuffer.length) {
                _encodingBuffer[_encodingBufferIndex++] = (byte)b;
            } else {
                byte newbuf[] = new byte[Math.max(_encodingBuffer.length << 1, _encodingBufferIndex)];
                System.arraycopy(_encodingBuffer, 0, newbuf, 0, _encodingBufferIndex);
                _encodingBuffer = newbuf;

                _encodingBuffer[_encodingBufferIndex++] = (byte)b;
            }
        }

        public void write(byte b[], int off, int len) throws IOException {
            if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }
            final int newoffset = _encodingBufferIndex + len;
            if (newoffset > _encodingBuffer.length) {
                byte newbuf[] = new byte[Math.max(_encodingBuffer.length << 1, newoffset)];
                System.arraycopy(_encodingBuffer, 0, newbuf, 0, _encodingBufferIndex);
                _encodingBuffer = newbuf;
            }
            System.arraycopy(b, off, _encodingBuffer, _encodingBufferIndex, len);
            _encodingBufferIndex = newoffset;
        }

        public int getLength() {
            return _encodingBufferIndex;
        }

        public void reset() {
            _encodingBufferIndex = 0;
        }
    }

    protected EncodingBufferOutputStream _encodingBufferOutputStream = new EncodingBufferOutputStream();

    protected byte[] _encodingBuffer = new byte[512];

    protected int _encodingBufferIndex;

    protected final void ensureEncodingBufferSizeForUtf8String(int length) {
        final int newLength = 4 * length;
        if (_encodingBuffer.length < newLength) {
            _encodingBuffer = new byte[newLength];
        }
    }
    
    protected final int encodeUTF8String(char[] ch, int start, int length) throws IOException {
        int bpos = 0;

        // Make sure buffer is large enough
        ensureEncodingBufferSizeForUtf8String(length);

        final int end = start + length;
        int c;
        while (end != start) {
            c = ch[start++];
            if (c < 0x80) {
                // 1 byte, 7 bits
                _encodingBuffer[bpos++] = (byte) c;
            } else if (c < 0x800) {
                // 2 bytes, 11 bits
                _encodingBuffer[bpos++] =
                    (byte) (0xC0 | (c >> 6));    // first 5
                _encodingBuffer[bpos++] =
                    (byte) (0x80 | (c & 0x3F));  // second 6
            } else if (c <= '\uFFFF') { 
                // Supplementary UTF-8
//                if (!XMLChar.isHighSurrogate(c) && !XMLChar.isLowSurrogate(c)) {
//                    // 3 bytes, 16 bits
//                    _encodingBuffer[bpos++] =
//                        (byte) (0xE0 | (c >> 12));   // first 4
//                    _encodingBuffer[bpos++] =
//                        (byte) (0x80 | ((c >> 6) & 0x3F));  // second 6
//                    _encodingBuffer[bpos++] =
//                        (byte) (0x80 | (c & 0x3F));  // third 6
//                } else {
//                    // 4 bytes, high and low surrogate
//                    encodeCharacterAsUtf8FourByte(c, ch, start, end, bpos);
//                    bpos += 4;
//                    start++;
//                }
                // 3 bytes, 16 bits
                _encodingBuffer[bpos++] =
                    (byte) (0xE0 | (c >> 12));   // first 4
                _encodingBuffer[bpos++] =
                    (byte) (0x80 | ((c >> 6) & 0x3F));  // second 6
                _encodingBuffer[bpos++] =
                    (byte) (0x80 | (c & 0x3F));  // third 6
            }
        }

        return bpos;
    }
    
    protected final void encodeCharacterAsUtf8FourByte(int c, char[] ch, int chpos, int chend, int bpos) throws IOException {
        if (chpos == chend) {
            throw new IOException("");
        }
        
        final char d = ch[chpos];
        // Supplementary UTF-8
//        if (!XMLChar.isLowSurrogate(d)) {
//            throw new IOException("");
//        }
        
        final int uc = (((c & 0x3ff) << 10) | (d & 0x3ff)) + 0x10000;
        if (uc < 0 || uc >= 0x200000) {
            throw new IOException("");
        }

        _encodingBuffer[bpos++] = (byte)(0xF0 | ((uc >> 18)));
        _encodingBuffer[bpos++] = (byte)(0x80 | ((uc >> 12) & 0x3F));
        _encodingBuffer[bpos++] = (byte)(0x80 | ((uc >> 6) & 0x3F));
        _encodingBuffer[bpos++] = (byte)(0x80 | (uc & 0x3F));
    }
        
    protected final void ensureEncodingBufferSizeForUtf16String(int length) {
        final int newLength = 2 * length;
        if (_encodingBuffer.length < newLength) {
            _encodingBuffer = new byte[newLength];
        }
    }

    protected final int encodeUtf16String(char[] ch, int start, int length) throws IOException {
        int byteLength = 0;

        // Make sure buffer is large enough
        ensureEncodingBufferSizeForUtf16String(length);

        final int n = start + length;
        for (int i = start; i < n; i++) {
            final int c = (int) ch[i];
            _encodingBuffer[byteLength++] = (byte)(c >> 8);
            _encodingBuffer[byteLength++] = (byte)(c & 0xFF);
        }

        return byteLength;
    }
    
    public static String getPrefixFromQualifiedName(String qName) {
        int i = qName.indexOf(':');
        String prefix = "";
        if (i != -1) {
            prefix = qName.substring(0, i);
        }
        return prefix;
    }
}
