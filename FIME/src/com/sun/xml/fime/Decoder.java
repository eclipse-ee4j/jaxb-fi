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


import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import com.sun.xml.fime.alphabet.BuiltInRestrictedAlphabets;
import com.sun.xml.fime.jvnet.FastInfosetException;
import com.sun.xml.fime.jvnet.FastInfosetParser;
import com.sun.xml.fime.jvnet.ReferencedVocabulary;
import com.sun.xml.fime.jvnet.Vocabulary;
import com.sun.xml.fime.util.CharArray;
import com.sun.xml.fime.util.CharArrayArray;
import com.sun.xml.fime.util.CharArrayString;
import com.sun.xml.fime.util.ContiguousCharArrayArray;
import com.sun.xml.fime.util.DuplicateAttributeVerifier;
import com.sun.xml.fime.util.MessageCenter;
import com.sun.xml.fime.util.PrefixArray;
import com.sun.xml.fime.util.QualifiedNameArray;
import com.sun.xml.fime.util.StringArray;
import com.sun.xml.fime.util.SystemUtil;
import com.sun.xml.fime.vocab.ParserVocabulary;

public abstract class Decoder implements FastInfosetParser {

    // String interning system property
    public static final String STRING_INTERNING_SYSTEM_PROPERTY =
        "com.sun.xml.fastinfoset.parser.string-interning";

    // Buffer size system property
    public static final String BUFFER_SIZE_SYSTEM_PROPERTY =
        "com.sun.xml.fastinfoset.parser.buffer-size";

    protected static boolean _stringInterningSystemDefault = false;
    
    protected static int _bufferSizeSystemDefault = 1024;

    protected static QualifiedName DEFAULT_NAMESPACE_DECLARATION = new QualifiedName(
            "",
            EncodingConstants.XMLNS_NAMESPACE_NAME,
            EncodingConstants.XMLNS_NAMESPACE_PREFIX,
            EncodingConstants.XMLNS_NAMESPACE_PREFIX);
    
    static {
        String p = SystemUtil.getProperty(STRING_INTERNING_SYSTEM_PROPERTY,
            SystemUtil.getBooleanString(_stringInterningSystemDefault));
        _stringInterningSystemDefault = SystemUtil.getBooleanValue(p);

        p = SystemUtil.getProperty(BUFFER_SIZE_SYSTEM_PROPERTY,
            Integer.toString(_bufferSizeSystemDefault));
        try {
            int i = Integer.valueOf(p).intValue();
            if (i > 0) {
                _bufferSizeSystemDefault = i;
            }
        } catch (NumberFormatException e) {
        }
    }

    
    protected boolean _stringInterning = _stringInterningSystemDefault;

    protected int _bufferSize = _bufferSizeSystemDefault;
    
    protected InputStream _s;

    protected Hashtable _externalVocabularies;    

    protected Hashtable _registeredEncodingAlgorithms = new Hashtable();
    
    protected ParserVocabulary _v;

    protected PrefixArray _prefixTable;
    
    protected QualifiedNameArray _elementNameTable;
    
    protected QualifiedNameArray _attributeNameTable;

    protected ContiguousCharArrayArray _characterContentChunkTable;

    protected StringArray _attributeValueTable;
    
    protected boolean _vIsInternal;

    protected Vector _notations;

    protected Vector _unparsedEntities;

    protected int _b;

    protected boolean _terminate;

    protected boolean _doubleTerminate;

    protected boolean _addToTable;

    protected int _integer;

    protected int _identifier;

    protected byte[] _octetBuffer = new byte[_bufferSizeSystemDefault];

    protected int _octetBufferStart;

    protected int _octetBufferOffset;

    protected int _octetBufferEnd;

    protected int _octetBufferLength;

    protected EncodingAlgorithmInputStream _encodingAlgorithmInputStream = new EncodingAlgorithmInputStream();

    protected char[] _charBuffer = new char[512];

    protected int _charBufferLength;

    protected DuplicateAttributeVerifier _duplicateAttributeVerifier = new DuplicateAttributeVerifier();
    
    public Decoder() {
        _v = new ParserVocabulary();
        _prefixTable = _v.prefix;
        _elementNameTable = _v.elementName;
        _attributeNameTable = _v.attributeName;
        _characterContentChunkTable = _v.characterContentChunk;
        _attributeValueTable = _v.attributeValue;
        _vIsInternal = true;
    }

    
    // FastInfosetParser
        
    public void setStringInterning(boolean stringInterning) {
        _stringInterning = stringInterning;
    } 
    
    public boolean getStringInterning() {
        return _stringInterning;
    }
    
    public void setBufferSize(int bufferSize) {
        if (_bufferSize > _octetBuffer.length) {
            _bufferSize = bufferSize;
        }
    }
    
    public int getBufferSize() {
        return _bufferSize;
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
    
    public void setExternalVocabularies(Hashtable referencedVocabualries) {
        throw new RuntimeException();
    }

    public void setDynamicVocabulary(Vocabulary v) {
        throw new RuntimeException();
    }

    public ReferencedVocabulary getExternalVocabulary() {
        throw new RuntimeException();
    }

    public Vocabulary getIntitialVocabulary() {
        throw new RuntimeException();
    }

    public Vocabulary getDynamicVocabulary() {
        throw new RuntimeException();
    }

    public Vocabulary getFinalVocabulary() {
        throw new RuntimeException();
    }
    
    
    
    public void reset() {
        _terminate = _doubleTerminate = false;
    }

    public void setVocabulary(ParserVocabulary v) {
        _v = v;
        _prefixTable = _v.prefix;
        _elementNameTable = _v.elementName;
        _attributeNameTable = _v.attributeName;
        _characterContentChunkTable = _v.characterContentChunk;
        _attributeValueTable = _v.attributeValue;
        _vIsInternal = false;
    }

    public void setInputStream(InputStream s) {
        _s = s;
        _octetBufferOffset = 0;
        _octetBufferEnd = 0;
        if (_vIsInternal == true) {
            _v.clear();
        }
    }

    public final void decodeDII() throws FastInfosetException, IOException {
        final int b = read();
        if (b == EncodingConstants.DOCUMENT_INITIAL_VOCABULARY_FLAG) {
            decodeInitialVocabulary();
        } else if (b != 0) {
            throw new IOException(MessageCenter.
                getString("message.optinalValues"));
        }
    }

    public final void decodeAdditionalData() throws FastInfosetException, IOException {
        for (int i = 0; i < decodeNumberOfItemsOfSequence(); i++) {
            decodeNonEmptyOctetStringLengthOnSecondBit();
            ensureOctetBufferSize();
            _octetBufferStart = _octetBufferOffset;
            _octetBufferOffset += _octetBufferLength;
        }
    }

    public final void decodeInitialVocabulary() throws FastInfosetException, IOException {
        // First 5 optionals of 13 bit optional field
        int b = read();
        // Next 8 optionals of 13 bit optional field
        int b2 = read();

        // Optimize for the most common case
        if (b == EncodingConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG && b2 == 0) {
            decodeExternalVocabularyURI();
            return;
        }

        if ((b & EncodingConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG) > 0) {
            decodeExternalVocabularyURI();
        }

        if ((b & EncodingConstants.INITIAL_VOCABULARY_RESTRICTED_ALPHABETS_FLAG) > 0) {
            decodeTableItems(_v.restrictedAlphabet);
        }

        if ((b & EncodingConstants.INITIAL_VOCABULARY_ENCODING_ALGORITHMS_FLAG) > 0) {
            decodeTableItems(_v.encodingAlgorithm);
        }

        if ((b & EncodingConstants.INITIAL_VOCABULARY_PREFIXES_FLAG) > 0) {
            decodeTableItems(_v.prefix);
        }

        if ((b & EncodingConstants.INITIAL_VOCABULARY_NAMESPACE_NAMES_FLAG) > 0) {
            decodeTableItems(_v.namespaceName);
        }

        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_LOCAL_NAMES_FLAG) > 0) {
            decodeTableItems(_v.localName);
        }

        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_OTHER_NCNAMES_FLAG) > 0) {
            decodeTableItems(_v.otherNCName);
        }

        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_OTHER_URIS_FLAG) > 0) {
            decodeTableItems(_v.otherURI);
        }

        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_ATTRIBUTE_VALUES_FLAG) > 0) {
            decodeTableItems(_v.attributeValue);
        }

        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_CONTENT_CHARACTER_CHUNKS_FLAG) > 0) {
            decodeTableItems(_v.characterContentChunk);
        }

        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_OTHER_STRINGS_FLAG) > 0) {
            decodeTableItems(_v.otherString);
        }

        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_ELEMENT_NAME_SURROGATES_FLAG) > 0) {
            decodeTableItems(_v.elementName, false);
        }

        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_ATTRIBUTE_NAME_SURROGATES_FLAG) > 0) {
            decodeTableItems(_v.attributeName, true);
        }
    }

    public void decodeExternalVocabularyURI() throws FastInfosetException, IOException {
        if (_externalVocabularies == null) {
            throw new IOException(MessageCenter.getString("message.noExternalVocabularies"));
        }

        String externalVocabularyURI = decodeNonEmptyOctetStringOnSecondBitAsUtf8String();
        ParserVocabulary externalVocabulary =
            (ParserVocabulary) _externalVocabularies.get(externalVocabularyURI);
        if (externalVocabulary == null) {
            throw new FastInfosetException(MessageCenter.getString("message.externalVocabularyNotRegistered", new Object[]{externalVocabularyURI}));
        }

        _v.setReferencedVocabulary(externalVocabularyURI, externalVocabulary, false);
    }

    public final void decodeTableItems(StringArray array) throws FastInfosetException, IOException {
        for (int i = 0; i < decodeNumberOfItemsOfSequence(); i++) {
            array.add(decodeNonEmptyOctetStringOnSecondBitAsUtf8String());
        }
    }

    public final void decodeTableItems(PrefixArray array) throws FastInfosetException, IOException {
        for (int i = 0; i < decodeNumberOfItemsOfSequence(); i++) {
            array.add(decodeNonEmptyOctetStringOnSecondBitAsUtf8String());
        }
    }
    
    public final void decodeTableItems(ContiguousCharArrayArray array) throws FastInfosetException, IOException {
        for (int i = 0; i < decodeNumberOfItemsOfSequence(); i++) {
            switch(decodeNonIdentifyingStringOnFirstBit()) {
                case NISTRING_STRING:
                    array.add(_charBuffer, _charBufferLength);
                    break;
                default:
                    throw new FastInfosetException(MessageCenter.getString("message.illegalState"));
            }
        }
    }
    
    public final void decodeTableItems(CharArrayArray array) throws FastInfosetException, IOException {
        for (int i = 0; i < decodeNumberOfItemsOfSequence(); i++) {
            switch(decodeNonIdentifyingStringOnFirstBit()) {
                case NISTRING_STRING:
                    array.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    break;
                default:
                    throw new FastInfosetException(MessageCenter.getString("message.illegalState"));
            }
        }
    }

    public final void decodeTableItems(QualifiedNameArray array, boolean isAttribute) throws FastInfosetException, IOException {
        for (int i = 0; i < decodeNumberOfItemsOfSequence(); i++) {
            final int b = read();

            String prefix = "";
            int prefixIndex = -1;
            if ((b & EncodingConstants.NAME_SURROGATE_PREFIX_FLAG) > 0) {
                prefixIndex = decodeIntegerIndexOnSecondBit();
                prefix = _v.prefix.get(prefixIndex);
            }
            
            String namespaceName = "";
            int namespaceNameIndex = -1;
            if ((b & EncodingConstants.NAME_SURROGATE_NAME_FLAG) > 0) {
                namespaceNameIndex = decodeIntegerIndexOnSecondBit();
                namespaceName = _v.prefix.get(prefixIndex);
            }
            
            if (namespaceName == "" && prefix != "") {
                throw new FastInfosetException(MessageCenter.getString("message.missingNamespace"));
            }

            final int localNameIndex = decodeIntegerIndexOnSecondBit();
            final String localName = _v.localName.get(localNameIndex);

            QualifiedName qualifiedName = new QualifiedName(prefix, namespaceName, localName, 
                    prefixIndex, namespaceNameIndex, localNameIndex, 
                    _charBuffer, _stringInterning);
            if (isAttribute) {
                qualifiedName.createAttributeValues(DuplicateAttributeVerifier.MAP_SIZE);
            }
            array.add(qualifiedName);
        }
    }

    public final int decodeNumberOfItemsOfSequence() throws IOException {
        final int b = read();
        if (b < 128) {
            return b;
        } else {
            return ((b & 0x0F) << 16) | (read() << 8) | read();
        }
    }

    public final void decodeNotations() throws FastInfosetException, IOException {
        if (_notations == null) {
            _notations = new Vector();
        } else {
            _notations.removeAllElements();
        }

        int b = read();
        while ((b & EncodingConstants.NOTATIONS_MASK) == EncodingConstants.NOTATIONS) {
            String name = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);

            String system_identifier = ((_b & EncodingConstants.NOTATIONS_SYSTEM_IDENTIFIER_FLAG) > 0)
                ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
            String public_identifier = ((_b & EncodingConstants.NOTATIONS_PUBLIC_IDENTIFIER_FLAG) > 0)
                ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";

            Notation notation = new Notation(name, system_identifier, public_identifier);
            _notations.addElement(notation);

            b = read();
        }
        if (b != EncodingConstants.TERMINATOR) {
            throw new FastInfosetException(MessageCenter.getString("message.IIsNotTerminatedCorrectly"));
        }
    }

    public final void decodeUnparsedEntities() throws FastInfosetException, IOException {
        if (_unparsedEntities == null) {
            _unparsedEntities = new Vector();
        } else {
            _unparsedEntities.removeAllElements();
        }

        int b = read();
        while ((b & EncodingConstants.UNPARSED_ENTITIES_MASK) == EncodingConstants.UNPARSED_ENTITIES) {
            String name = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);
            String system_identifier = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI);

            String public_identifier = ((_b & EncodingConstants.UNPARSED_ENTITIES_PUBLIC_IDENTIFIER_FLAG) > 0)
                ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";

            String notation_name = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);

            UnparsedEntity unparsedEntity = new UnparsedEntity(name, system_identifier, public_identifier, notation_name);
            _unparsedEntities.addElement(unparsedEntity);

            b = read();
        }
        if (b != EncodingConstants.TERMINATOR) {
            throw new FastInfosetException(MessageCenter.getString("message.unparsedEntities"));
        }
    }

    public final String decodeCharacterEncodingScheme() throws FastInfosetException, IOException {
        return decodeNonEmptyOctetStringOnSecondBitAsUtf8String();
    }
    
    public final String decodeVersion() throws FastInfosetException, IOException {
        switch(decodeNonIdentifyingStringOnFirstBit()) {
            case NISTRING_STRING:
                final String data = new String(_charBuffer, 0, _charBufferLength);
                if (_addToTable) {
                    _v.otherString.add(new CharArrayString(data));
                }
                return data;
            case NISTRING_ENCODING_ALGORITHM:
                throw new FastInfosetException(MessageCenter.getString("message.decodingNotSupported"));
            case NISTRING_INDEX:
                return _v.otherString.get(_integer).toString();
            case NISTRING_EMPTY_STRING:
            default:
                return "";
        }
    }

    protected final QualifiedName decodeEIIIndexMedium() throws FastInfosetException, IOException {
        final int i = (((_b & EncodingConstants.INTEGER_3RD_BIT_MEDIUM_MASK) << 8) | read())
            + EncodingConstants.INTEGER_3RD_BIT_SMALL_LIMIT;
        return _v.elementName._array[i];
    }

    protected final QualifiedName decodeEIIIndexLarge() throws FastInfosetException, IOException {
        int i;
        if ((_b & EncodingConstants.INTEGER_3RD_BIT_LARGE_LARGE_FLAG) == 0x20) {
            // EII large index
            i = (((_b & EncodingConstants.INTEGER_3RD_BIT_LARGE_MASK) << 16) | (read() << 8) | read())
                + EncodingConstants.INTEGER_3RD_BIT_MEDIUM_LIMIT;
        } else {
            // EII large large index
            i = (((read() & EncodingConstants.INTEGER_3RD_BIT_LARGE_LARGE_MASK) << 16) | (read() << 8) | read())
                + EncodingConstants.INTEGER_3RD_BIT_LARGE_LIMIT;
        }
        return _v.elementName._array[i];
    }

    protected final QualifiedName decodeLiteralQualifiedName(int state) throws FastInfosetException, IOException {
        switch (state) {
            // no prefix, no namespace
            case 0:
                return new QualifiedName(
                        "", 
                        "", 
                        decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName),
                        -1,
                        -1,
                        _identifier,
                        null,
                        _stringInterning);
            // no prefix, namespace
            case 1:
                return new QualifiedName(
                        "",
                        decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), 
                        decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName),
                        -1,
                        _namespaceNameIndex,
                        _identifier,
                        null,
                        _stringInterning);
            // prefix, no namespace
            case 2:
                throw new FastInfosetException(MessageCenter.getString("message.qNameMissingNamespaceName"));
            // prefix, namespace
            case 3:
                return new QualifiedName(
                        decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), 
                        decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), 
                        decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName),
                        _prefixIndex,
                        _namespaceNameIndex,
                        _identifier,
                        _charBuffer,
                        _stringInterning);
            default:
                throw new FastInfosetException(MessageCenter.getString("message.decodingEII"));                
        }        
    }

    public static final int NISTRING_STRING              = 0;
    public static final int NISTRING_INDEX               = 1;
    public static final int NISTRING_ENCODING_ALGORITHM  = 2;
    public static final int NISTRING_EMPTY_STRING        = 3;

    /*
     * C.14
     * decodeNonIdentifyingStringOnFirstBit
     */
    public final int decodeNonIdentifyingStringOnFirstBit() throws FastInfosetException, IOException {
        final int b = read();
        switch(DecoderStateTables.NISTRING[b]) {
            case DecoderStateTables.NISTRING_UTF8_SMALL_LENGTH:
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                _octetBufferLength = (b & EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_MASK) + 1;
                decodeUtf8StringAsCharBuffer();
                return NISTRING_STRING;
            case DecoderStateTables.NISTRING_UTF8_MEDIUM_LENGTH:
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT;
                decodeUtf8StringAsCharBuffer();
                return NISTRING_STRING;
            case DecoderStateTables.NISTRING_UTF8_LARGE_LENGTH:
            {
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                final int length = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT;
                decodeUtf8StringAsCharBuffer();
                return NISTRING_STRING;
            }
            case DecoderStateTables.NISTRING_UTF16_SMALL_LENGTH:
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                _octetBufferLength = (b & EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_MASK) + 1;
                decodeUtf16StringAsCharBuffer();
                return NISTRING_STRING;
            case DecoderStateTables.NISTRING_UTF16_MEDIUM_LENGTH:
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT;
                decodeUtf16StringAsCharBuffer();
                return NISTRING_STRING;
            case DecoderStateTables.NISTRING_UTF16_LARGE_LENGTH:
            {
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                final int length = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT;
                decodeUtf16StringAsCharBuffer();
                return NISTRING_STRING;
            }
            case DecoderStateTables.NISTRING_RA:
            {
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                // Decode resitricted alphabet integer
                _identifier = (b & 0x0F) << 4;
                final int b2 = read();
                _identifier |= (b2 & 0xF0) >> 4;

                decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b2);
                
                decodeRestrictedAlphabetAsCharBuffer();
                return NISTRING_STRING;
            }
            case DecoderStateTables.NISTRING_EA:
            {
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                // Decode encoding algorithm integer
                _identifier = (b & 0x0F) << 4;
                final int b2 = read();
                _identifier |= (b2 & 0xF0) >> 4;

                decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b2);
                return NISTRING_ENCODING_ALGORITHM;
            }
            case DecoderStateTables.NISTRING_INDEX_SMALL:
                _integer = b & EncodingConstants.INTEGER_2ND_BIT_SMALL_MASK;
                return NISTRING_INDEX;
            case DecoderStateTables.NISTRING_INDEX_MEDIUM:
                _integer = (((b & EncodingConstants.INTEGER_2ND_BIT_MEDIUM_MASK) << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
                return NISTRING_INDEX;
            case DecoderStateTables.NISTRING_INDEX_LARGE:
                _integer = (((b & EncodingConstants.INTEGER_2ND_BIT_LARGE_MASK) << 16) | (read() << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
                return NISTRING_INDEX;
            case DecoderStateTables.NISTRING_EMPTY:
                return NISTRING_EMPTY_STRING;
            default:
                throw new FastInfosetException(MessageCenter.getString("message.decodingNonIdentifyingString"));
        }
    }

    public final void decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(int b) throws FastInfosetException, IOException {
        // Remove top 4 bits of restricted alphabet or encoding algorithm integer
        b &= 0x0F;
        // Reuse UTF8 length states
        switch(DecoderStateTables.NISTRING[b]) {
            case DecoderStateTables.NISTRING_UTF8_SMALL_LENGTH:
                _octetBufferLength = b + 1;
                break;
            case DecoderStateTables.NISTRING_UTF8_MEDIUM_LENGTH:
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT;
                break;
            case DecoderStateTables.NISTRING_UTF8_LARGE_LENGTH:
                final int length = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT;
                break;
            default:
                throw new FastInfosetException(MessageCenter.getString("message.decodingOctets"));
        }
        ensureOctetBufferSize();
        _octetBufferStart = _octetBufferOffset;
        _octetBufferOffset += _octetBufferLength;
    }

    public final void decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(int b) throws FastInfosetException, IOException {
        // Remove top 6 bits of restricted alphabet or encoding algorithm integer
        switch (b & 0x03) {
            // Small length
            case 0:
                _octetBufferLength = 1;
                break;
            // Small length
            case 1:
                _octetBufferLength = 2;
                break;
            // Medium length
            case 2:
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT;
                break;
            // Large length
            case 3:
                _octetBufferLength = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength += EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT;
                break;
        }

        ensureOctetBufferSize();
        _octetBufferStart = _octetBufferOffset;
        _octetBufferOffset += _octetBufferLength;
    }

    /*
     * C.13
     */
    public final String decodeIdentifyingNonEmptyStringOnFirstBit(StringArray table) throws FastInfosetException, IOException {
        final int b = read();
        switch(DecoderStateTables.ISTRING[b]) {
            case DecoderStateTables.ISTRING_SMALL_LENGTH:
            {
                _octetBufferLength = b + 1;
                final String s = (_stringInterning) ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
                _identifier = table.add(s) - 1;
                return s;
            }
            case DecoderStateTables.ISTRING_MEDIUM_LENGTH:
            {
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_SMALL_LIMIT;
                final String s = (_stringInterning) ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
                _identifier = table.add(s) - 1;
                return s;
            }
            case DecoderStateTables.ISTRING_LARGE_LENGTH:
            {
                final int length = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_LIMIT;
                final String s = (_stringInterning) ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
                _identifier = table.add(s) - 1;
                return s;
            }
            case DecoderStateTables.ISTRING_INDEX_SMALL:
                _identifier = b & EncodingConstants.INTEGER_2ND_BIT_SMALL_MASK;
                return table._array[_identifier];
            case DecoderStateTables.ISTRING_INDEX_MEDIUM:
                _identifier = (((b & EncodingConstants.INTEGER_2ND_BIT_MEDIUM_MASK) << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
                return table._array[_identifier];
            case DecoderStateTables.ISTRING_INDEX_LARGE:
                _identifier = (((b & EncodingConstants.INTEGER_2ND_BIT_LARGE_MASK) << 16) | (read() << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
                return table._array[_identifier];
            default:
                throw new FastInfosetException(MessageCenter.getString("message.decodingIdentifyingString"));
        }
    }

    protected int _prefixIndex;
    
    /*
     * C.13
     */
    public final String decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(boolean namespaceNamePresent) throws FastInfosetException, IOException {
        final int b = read();
        switch(DecoderStateTables.ISTRING_PREFIX_NAMESPACE[b]) {
            case DecoderStateTables.ISTRING_PREFIX_NAMESPACE_LENGTH_3:
            {
                _octetBufferLength = EncodingConstants.XML_NAMESPACE_PREFIX_LENGTH;
                decodeUtf8StringAsCharBuffer();
                
                if (_charBuffer[0] == 'x' &&
                        _charBuffer[1] == 'm' &&
                        _charBuffer[2] == 'l') {
                    throw new FastInfosetException(MessageCenter.getString("message.prefixIllegal"));
                }
                
                final String s = (_stringInterning) ? new String(_charBuffer, 0, _charBufferLength).intern() :
                    new String(_charBuffer, 0, _charBufferLength);
                _prefixIndex = _v.prefix.add(s);
                return s;
            }
            case DecoderStateTables.ISTRING_PREFIX_NAMESPACE_LENGTH_5:
            {
                _octetBufferLength = EncodingConstants.XMLNS_NAMESPACE_PREFIX_LENGTH;
                decodeUtf8StringAsCharBuffer();
                
                if (_charBuffer[0] == 'x' &&
                        _charBuffer[1] == 'm' &&
                        _charBuffer[2] == 'l' &&
                        _charBuffer[3] == 'n' &&
                        _charBuffer[4] == 's') {
                    throw new FastInfosetException(MessageCenter.getString("message.xmlns"));
                }
                
                final String s = (_stringInterning) ? new String(_charBuffer, 0, _charBufferLength).intern() :
                    new String(_charBuffer, 0, _charBufferLength);
                _prefixIndex = _v.prefix.add(s);
                return s;
            }
            case DecoderStateTables.ISTRING_SMALL_LENGTH:
            case DecoderStateTables.ISTRING_PREFIX_NAMESPACE_LENGTH_29:
            case DecoderStateTables.ISTRING_PREFIX_NAMESPACE_LENGTH_36:
            {
                _octetBufferLength = b + 1;
                final String s = (_stringInterning) ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
                _prefixIndex = _v.prefix.add(s);
                return s;
            }
            case DecoderStateTables.ISTRING_MEDIUM_LENGTH:
            {
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_SMALL_LIMIT;
                final String s = (_stringInterning) ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
                _prefixIndex = _v.prefix.add(s);
                return s;
            }
            case DecoderStateTables.ISTRING_LARGE_LENGTH:
            {
                final int length = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_LIMIT;
                final String s = (_stringInterning) ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
                _prefixIndex = _v.prefix.add(s);
                return s;
            }
            case DecoderStateTables.ISTRING_PREFIX_NAMESPACE_INDEX_ZERO:
                if (namespaceNamePresent) {
                    _prefixIndex = 0;
                    // Peak at next byte and check the index of the XML namespace name
                    if (DecoderStateTables.ISTRING_PREFIX_NAMESPACE[peak()] 
                            != DecoderStateTables.ISTRING_PREFIX_NAMESPACE_INDEX_ZERO) {
                        throw new FastInfosetException(MessageCenter.getString("message.wrongNamespaceName"));
                    }
                    return EncodingConstants.XML_NAMESPACE_PREFIX;
                } else {                    
                    throw new FastInfosetException(MessageCenter.getString("message.missingNamespaceName"));
                }
            case DecoderStateTables.ISTRING_INDEX_SMALL:
                _prefixIndex = b & EncodingConstants.INTEGER_2ND_BIT_SMALL_MASK;
                return _v.prefix._array[_prefixIndex - 1];
            case DecoderStateTables.ISTRING_INDEX_MEDIUM:
                _prefixIndex = (((b & EncodingConstants.INTEGER_2ND_BIT_MEDIUM_MASK) << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
                return _v.prefix._array[_prefixIndex - 1];
            case DecoderStateTables.ISTRING_INDEX_LARGE:
                _prefixIndex = (((b & EncodingConstants.INTEGER_2ND_BIT_LARGE_MASK) << 16) | (read() << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
                return _v.prefix._array[_prefixIndex - 1];
            default:
                throw new FastInfosetException(MessageCenter.getString("message.decodingIdentifyingStringForPrefix"));
        }
    }
    
    /*
     * C.13
     */
    public final String decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(boolean namespaceNamePresent) throws FastInfosetException, IOException {
        final int b = read();
        switch(DecoderStateTables.ISTRING_PREFIX_NAMESPACE[b]) {
            case DecoderStateTables.ISTRING_PREFIX_NAMESPACE_INDEX_ZERO:
                if (namespaceNamePresent) {
                    _prefixIndex = 0;
                    // Peak at next byte and check the index of the XML namespace name
                    if (DecoderStateTables.ISTRING_PREFIX_NAMESPACE[peak()] 
                            != DecoderStateTables.ISTRING_PREFIX_NAMESPACE_INDEX_ZERO) {
                        throw new FastInfosetException(MessageCenter.getString("message.wrongNamespaceName"));
                    }
                    return EncodingConstants.XML_NAMESPACE_PREFIX;
                } else {                    
                    throw new FastInfosetException(MessageCenter.getString("message.missingNamespaceName"));
                }
            case DecoderStateTables.ISTRING_INDEX_SMALL:
                _prefixIndex = b & EncodingConstants.INTEGER_2ND_BIT_SMALL_MASK;
                return _v.prefix._array[_prefixIndex - 1];
            case DecoderStateTables.ISTRING_INDEX_MEDIUM:
                _prefixIndex = (((b & EncodingConstants.INTEGER_2ND_BIT_MEDIUM_MASK) << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
                return _v.prefix._array[_prefixIndex - 1];
            case DecoderStateTables.ISTRING_INDEX_LARGE:
                _prefixIndex = (((b & EncodingConstants.INTEGER_2ND_BIT_LARGE_MASK) << 16) | (read() << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
                return _v.prefix._array[_prefixIndex - 1];
            default:
                throw new FastInfosetException(MessageCenter.getString("message.decodingIdentifyingStringForPrefix"));
        }
    }
    
    protected int _namespaceNameIndex;
    
    /*
     * C.13
     */
    public final String decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(boolean prefixPresent) throws FastInfosetException, IOException {
        final int b = read();
        switch(DecoderStateTables.ISTRING_PREFIX_NAMESPACE[b]) {
            case DecoderStateTables.ISTRING_PREFIX_NAMESPACE_LENGTH_3:
            case DecoderStateTables.ISTRING_PREFIX_NAMESPACE_LENGTH_5:
            case DecoderStateTables.ISTRING_SMALL_LENGTH:
            {
                _octetBufferLength = b + 1;
                final String s = (_stringInterning) ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
                _namespaceNameIndex = _v.namespaceName.add(s);
                return s;
            }
            case DecoderStateTables.ISTRING_PREFIX_NAMESPACE_LENGTH_29:
            {
                _octetBufferLength = EncodingConstants.XMLNS_NAMESPACE_NAME_LENGTH;
                decodeUtf8StringAsCharBuffer();
                
                if (compareCharsWithCharBufferFromEndToStart(EncodingConstants.XMLNS_NAMESPACE_NAME_CHARS)) {
                    throw new FastInfosetException(MessageCenter.getString("message.xmlnsConnotBeBoundToPrefix"));
                }
                
                final String s = (_stringInterning) ? new String(_charBuffer, 0, _charBufferLength).intern() :
                    new String(_charBuffer, 0, _charBufferLength);
                _namespaceNameIndex = _v.namespaceName.add(s);
                return s;
            }
            case DecoderStateTables.ISTRING_PREFIX_NAMESPACE_LENGTH_36:
            {
                _octetBufferLength = EncodingConstants.XML_NAMESPACE_NAME_LENGTH;
                decodeUtf8StringAsCharBuffer();
                
                if (compareCharsWithCharBufferFromEndToStart(EncodingConstants.XML_NAMESPACE_NAME_CHARS)) {
                    throw new FastInfosetException(MessageCenter.getString("message.illegalNamespaceName"));
                }
                
                final String s = (_stringInterning) ? new String(_charBuffer, 0, _charBufferLength).intern() :
                    new String(_charBuffer, 0, _charBufferLength);
                _namespaceNameIndex = _v.namespaceName.add(s);
                return s;
            }
            case DecoderStateTables.ISTRING_MEDIUM_LENGTH:
            {
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_SMALL_LIMIT;
                final String s = (_stringInterning) ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
                _namespaceNameIndex = _v.namespaceName.add(s);
                return s;
            }
            case DecoderStateTables.ISTRING_LARGE_LENGTH:
            {
                final int length = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_LIMIT;
                final String s = (_stringInterning) ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
                _namespaceNameIndex = _v.namespaceName.add(s);
                return s;
            }
            case DecoderStateTables.ISTRING_PREFIX_NAMESPACE_INDEX_ZERO:
                if (prefixPresent) {
                    _namespaceNameIndex = 0;
                    return EncodingConstants.XML_NAMESPACE_NAME;
                } else {
                    throw new FastInfosetException(MessageCenter.getString("message.namespaceWithoutPrefix"));
                }
            case DecoderStateTables.ISTRING_INDEX_SMALL: 
                _namespaceNameIndex = b & EncodingConstants.INTEGER_2ND_BIT_SMALL_MASK;
                return _v.namespaceName._array[_namespaceNameIndex - 1];
            case DecoderStateTables.ISTRING_INDEX_MEDIUM:
                _namespaceNameIndex = (((b & EncodingConstants.INTEGER_2ND_BIT_MEDIUM_MASK) << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
                return _v.namespaceName._array[_namespaceNameIndex - 1];
            case DecoderStateTables.ISTRING_INDEX_LARGE:
                _namespaceNameIndex = (((b & EncodingConstants.INTEGER_2ND_BIT_LARGE_MASK) << 16) | (read() << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
                return _v.namespaceName._array[_namespaceNameIndex - 1];
            default:
                throw new FastInfosetException(MessageCenter.getString("message.decodingForNamespaceName"));
        }
    }
    
    /*
     * C.13
     */
    public final String decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(boolean prefixPresent) throws FastInfosetException, IOException {
        final int b = read();
        switch(DecoderStateTables.ISTRING_PREFIX_NAMESPACE[b]) {
            case DecoderStateTables.ISTRING_PREFIX_NAMESPACE_INDEX_ZERO:
                if (prefixPresent) {
                    _namespaceNameIndex = 0;
                    return EncodingConstants.XML_NAMESPACE_NAME;
                } else {
                    throw new FastInfosetException(MessageCenter.getString("message.namespaceWithoutPrefix"));
                }
            case DecoderStateTables.ISTRING_INDEX_SMALL: 
                _namespaceNameIndex = b & EncodingConstants.INTEGER_2ND_BIT_SMALL_MASK;
                return _v.namespaceName._array[_namespaceNameIndex - 1];
            case DecoderStateTables.ISTRING_INDEX_MEDIUM:
                _namespaceNameIndex = (((b & EncodingConstants.INTEGER_2ND_BIT_MEDIUM_MASK) << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
                return _v.namespaceName._array[_namespaceNameIndex - 1];
            case DecoderStateTables.ISTRING_INDEX_LARGE:
                _namespaceNameIndex = (((b & EncodingConstants.INTEGER_2ND_BIT_LARGE_MASK) << 16) | (read() << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
                return _v.namespaceName._array[_namespaceNameIndex - 1];
            default:
                throw new FastInfosetException(MessageCenter.getString("message.decodingForNamespaceName"));
        }
    }
    
    public final boolean compareCharsWithCharBufferFromEndToStart(char[] c) {
        int i = _charBufferLength ;
        while (--i >= 0) {
            if (c[i] != _charBuffer[i]) {
                return false;
            }
        }        
        return true;
    }
    
    /*
     * C.22
     */
    public final String decodeNonEmptyOctetStringOnSecondBitAsUtf8String() throws FastInfosetException, IOException {
        decodeNonEmptyOctetStringOnSecondBitAsUtf8CharArray();
        return new String(_charBuffer, 0, _charBufferLength);
    }

    /*
     * C.22
     */
    public final void decodeNonEmptyOctetStringOnSecondBitAsUtf8CharArray() throws FastInfosetException, IOException {
        decodeNonEmptyOctetStringLengthOnSecondBit();
        decodeUtf8StringAsCharBuffer();
    }

    /*
     * C.22
     */
    public final void decodeNonEmptyOctetStringLengthOnSecondBit() throws FastInfosetException, IOException {
        final int b = read();
        switch(DecoderStateTables.ISTRING[b]) {
            case DecoderStateTables.ISTRING_SMALL_LENGTH:
                _octetBufferLength = b + 1;
                break;
            case DecoderStateTables.ISTRING_MEDIUM_LENGTH:
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_SMALL_LIMIT;
                break;
            case DecoderStateTables.ISTRING_LARGE_LENGTH:
            {
                final int length = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_LIMIT;
                break;
            }
            case DecoderStateTables.ISTRING_INDEX_SMALL:
            case DecoderStateTables.ISTRING_INDEX_MEDIUM:
            case DecoderStateTables.ISTRING_INDEX_LARGE:
            default:
                throw new FastInfosetException(MessageCenter.getString("message.decodingNonEmptyOctet"));
        }
    }
    
    /*
     * C.25
     */
    public final int decodeIntegerIndexOnSecondBit() throws FastInfosetException, IOException {
        final int b = read();
        switch(DecoderStateTables.ISTRING[b]) {
            case DecoderStateTables.ISTRING_INDEX_SMALL:
                return b & EncodingConstants.INTEGER_2ND_BIT_SMALL_MASK;
            case DecoderStateTables.ISTRING_INDEX_MEDIUM:
                return (((b & EncodingConstants.INTEGER_2ND_BIT_MEDIUM_MASK) << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
            case DecoderStateTables.ISTRING_INDEX_LARGE:
                return (((b & EncodingConstants.INTEGER_2ND_BIT_LARGE_MASK) << 16) | (read() << 8) | read())
                    + EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
            case DecoderStateTables.ISTRING_SMALL_LENGTH:
            case DecoderStateTables.ISTRING_MEDIUM_LENGTH:
            case DecoderStateTables.ISTRING_LARGE_LENGTH:
            default:
                throw new FastInfosetException(MessageCenter.getString("message.decodingIndexOnSecondBit"));
        }
    }

    public final void decodeHeader() throws FastInfosetException, IOException {
        if (!_isFastInfosetDocument()) {
            throw new FastInfosetException(MessageCenter.getString("message.notFIDocument"));
        }
    }

    protected final void decodeRestrictedAlphabetAsCharBuffer() throws FastInfosetException, IOException {
        if (_identifier <= EncodingConstants.RESTRICTED_ALPHABET_BUILTIN_END) {
            decodeFourBitAlphabetOctetsAsCharBuffer(BuiltInRestrictedAlphabets.table[_identifier]);
            // decodeAlphabetOctetsAsCharBuffer(BuiltInRestrictedAlphabets.table[_identifier]);
        } else if (_identifier >= EncodingConstants.RESTRICTED_ALPHABET_APPLICATION_START) {
            CharArray ca = _v.restrictedAlphabet.get(_identifier - EncodingConstants.RESTRICTED_ALPHABET_APPLICATION_START);
            if (ca == null) {
                throw new FastInfosetException(MessageCenter.getString("message.alphabetNotPresent", new Object[]{new Integer(_identifier)}));
            }
            decodeAlphabetOctetsAsCharBuffer(ca.ch);            
        } else {
            // Reserved built-in algorithms for future use
            // TODO should use sax property to decide if event will be
            // reported, allows for support through handler if required.
            throw new FastInfosetException(MessageCenter.getString("message.alphabetIdentifiersReserved"));
        }
    }
    
    protected final String decodeRestrictedAlphabetAsString() throws FastInfosetException, IOException {
        decodeRestrictedAlphabetAsCharBuffer();
        return new String(_charBuffer, 0, _charBufferLength);
    }
    
    public final String decodeRAOctetsAsString(char[] restrictedAlphabet) throws FastInfosetException, IOException {
        decodeAlphabetOctetsAsCharBuffer(null);
        return new String(_charBuffer, 0, _charBufferLength);
    }

    public final void decodeFourBitAlphabetOctetsAsCharBuffer(char[] restrictedAlphabet) throws FastInfosetException, IOException {
        _charBufferLength = 0;
        final int characters = _octetBufferLength / 2;
        if (_charBuffer.length < characters) {
            _charBuffer = new char[characters];
        }
        
        int v = 0;
        for (int i = 0; i < _octetBufferLength - 1; i++) {
            v = _octetBuffer[_octetBufferStart++] & 0xFF;
            _charBuffer[_charBufferLength++] = restrictedAlphabet[v >> 4];
            _charBuffer[_charBufferLength++] = restrictedAlphabet[v & 0x0F];            
        }
        v = _octetBuffer[_octetBufferStart++] & 0xFF;            
        _charBuffer[_charBufferLength++] = restrictedAlphabet[v >> 4];
        v &= 0x0F;
        if (v != 0x0F) {
            _charBuffer[_charBufferLength++] = restrictedAlphabet[v & 0x0F];
        }
    }
    
    public final void decodeAlphabetOctetsAsCharBuffer(char[] restrictedAlphabet) throws FastInfosetException, IOException {
        if (restrictedAlphabet.length < 2) {
            throw new IllegalArgumentException(MessageCenter.getString("message.alphabetMustContain2orMoreChars"));
        }

        int bitsPerCharacter = 1;
        while ((1 << bitsPerCharacter) <= restrictedAlphabet.length) {
            bitsPerCharacter++;
        }
        final int terminatingValue = (1 << bitsPerCharacter) - 1;
        
        int characters = (_octetBufferLength << 3) / bitsPerCharacter;
        if (characters == 0) {
            throw new IOException("");
        }

        _charBufferLength = 0;
        if (_charBuffer.length < characters) {
            _charBuffer = new char[characters];
        }
        
        resetBits();
        for (int i = 0; i < characters; i++) {
            int value = readBits(bitsPerCharacter);
            if (bitsPerCharacter < 8 && value == terminatingValue) {
                int octetPosition = (i * bitsPerCharacter) >>> 3;
                if (octetPosition != _octetBufferLength - 1) {
                    throw new FastInfosetException(MessageCenter.getString("message.alphabetIncorrectlyTerminated"));
                }
                break;
            }
            _charBuffer[_charBufferLength++] = restrictedAlphabet[value];
        }
    }

    protected int _bitsLeftInOctet;
    
    public final void resetBits() {
        _bitsLeftInOctet = 0;
    }
    
    public final int readBits(int bits) throws IOException {
        int value = 0;
        while (bits > 0) {
            if (_bitsLeftInOctet == 0) {
                _b = _octetBuffer[_octetBufferStart++] & 0xFF;
                _bitsLeftInOctet = 8;
            }
            int bit = ((_b & (1 << --_bitsLeftInOctet)) > 0) ? 1 : 0;
            value |= (bit << --bits);
        }
        
        return value;
    }
    
    public final void decodeUtf8StringAsCharBuffer() throws IOException {
        ensureOctetBufferSize();
        decodeUtf8StringIntoCharBuffer();
    }

    public final String decodeUtf8StringAsString() throws IOException {
        decodeUtf8StringAsCharBuffer();
        return new String(_charBuffer, 0, _charBufferLength);
    }

    public final void decodeUtf16StringAsCharBuffer() throws IOException {
        ensureOctetBufferSize();
        decodeUtf16StringIntoCharBuffer();
    }

    public final String decodeUtf16StringAsString() throws IOException {
        decodeUtf16StringAsCharBuffer();
        return new String(_charBuffer, 0, _charBufferLength);
    }

    public final void ensureOctetBufferSize() throws IOException {
        if (_octetBufferEnd < (_octetBufferOffset + _octetBufferLength)) {
            final int bytesRemaining = _octetBufferEnd - _octetBufferOffset;

            if (_octetBuffer.length < _octetBufferLength) {
                byte[] newOctetBuffer = new byte[_octetBufferLength];
                System.arraycopy(_octetBuffer, _octetBufferOffset, newOctetBuffer, 0, bytesRemaining);
                _octetBuffer = newOctetBuffer;
            } else {
                System.arraycopy(_octetBuffer, _octetBufferOffset, _octetBuffer, 0, bytesRemaining);
            }
            _octetBufferOffset = 0;

            final int bytesRead = _s.read(_octetBuffer, bytesRemaining, _octetBuffer.length - bytesRemaining);
            if (bytesRead < 0) {
                throw new EOFException("Unexpeceted EOF");
            }

            if (bytesRead < _octetBufferLength - bytesRemaining) {
                // TODO keep reading until require bytes have been obtained
                throw new IOException(MessageCenter.getString("message.fullBytesNotRead"));
            }

            _octetBufferEnd = bytesRemaining + bytesRead;
        }
    }
    
    public final void decodeUtf8StringIntoCharBuffer() throws IOException {
        if (_charBuffer.length < _octetBufferLength) {
            _charBuffer = new char[_octetBufferLength];
        }
        
        _charBufferLength = 0;
        final int end = _octetBufferLength + _octetBufferOffset;
        int b1;
        while (end != _octetBufferOffset) {
            b1 = _octetBuffer[_octetBufferOffset++] & 0xFF;
            if (DecoderStateTables.UTF8[b1] == DecoderStateTables.UTF8_ONE_BYTE) {
                _charBuffer[_charBufferLength++] = (char) b1;
            } else {
                decodeTwoToFourByteUtf8Character(b1, end);
            }
        }        
    }

    public final void decodeTwoToFourByteUtf8Character(int b1, int end) throws IOException {
        switch(DecoderStateTables.UTF8[b1]) {
            case DecoderStateTables.UTF8_TWO_BYTES:
            {
                // Decode byte 2
                if (end == _octetBufferOffset) {
                    decodeUtf8StringLengthTooSmall();
                }
                final int b2 = _octetBuffer[_octetBufferOffset++] & 0xFF;
                if ((b2 & 0xC0) != 0x80) {
                    decodeUtf8StringIllegalState();
                }

                // Character guaranteed to be in [0x20, 0xD7FF] range
                // since a character encoded in two bytes will be in the 
                // range [0x80, 0x1FFF]
                _charBuffer[_charBufferLength++] = (char) (
                    ((b1 & 0x1F) << 6)
                    | (b2 & 0x3F));
                break;
            }
            case DecoderStateTables.UTF8_THREE_BYTES:
                final char c = decodeUtf8ThreeByteChar(end, b1);
                // XMLChar
//                if (XMLChar.isContent(c)) {
//                    _charBuffer[_charBufferLength++] = c;
//                    break;
//                } else {
//                    decodeUtf8StringIllegalState();
//                }
              _charBuffer[_charBufferLength++] = c;
              // supplementary UTF-8
//            case DecoderStateTables.UTF8_FOUR_BYTES:
//            {
//                final int supplemental = decodeUtf8FourByteChar(end, b1);
//                if (XMLChar.isContent(supplemental)) {
//                    _charBuffer[_charBufferLength++] = _utf8_highSurrogate;
//                    _charBuffer[_charBufferLength++] = _utf8_lowSurrogate;
//                } else {
//                    decodeUtf8StringIllegalState();
//                }
//                break;
//            }
            default:
                decodeUtf8StringIllegalState();
        }
    }
    
    public final void decodeUtf8NCNameIntoCharBuffer() throws IOException {
        _charBufferLength = 0;
        if (_charBuffer.length < _octetBufferLength) {
            _charBuffer = new char[_octetBufferLength];
        }

        final int end = _octetBufferLength + _octetBufferOffset;

        int b1 = _octetBuffer[_octetBufferOffset++] & 0xFF;
        if (DecoderStateTables.UTF8_NCNAME[b1] == DecoderStateTables.UTF8_NCNAME_NCNAME) {
            _charBuffer[_charBufferLength++] = (char) b1;
        } else {
            decodeUtf8NCNameStartTwoToFourByteCharacters(b1, end);
        }

        while (end != _octetBufferOffset) {
            b1 = _octetBuffer[_octetBufferOffset++] & 0xFF;
            if (DecoderStateTables.UTF8_NCNAME[b1] < DecoderStateTables.UTF8_TWO_BYTES) {
                _charBuffer[_charBufferLength++] = (char) b1;
            } else {
                decodeUtf8NCNameTwoToFourByteCharacters(b1, end);
            }
        }
    }

    public final void decodeUtf8NCNameStartTwoToFourByteCharacters(int b1, int end) throws IOException {
        switch(DecoderStateTables.UTF8_NCNAME[b1]) {
            case DecoderStateTables.UTF8_TWO_BYTES:
            {
                // Decode byte 2
                if (end == _octetBufferOffset) {
                    decodeUtf8StringLengthTooSmall();
                }
                final int b2 = _octetBuffer[_octetBufferOffset++] & 0xFF;
                if ((b2 & 0xC0) != 0x80) {
                    decodeUtf8StringIllegalState();
                }

                final char c = (char) (
                    ((b1 & 0x1F) << 6)
                    | (b2 & 0x3F));
                // XMLChar
//                if (XMLChar.isNCNameStart(c)) {
//                    _charBuffer[_charBufferLength++] = c;
//                    break;
//                } else {
//                    decodeUtf8NCNameIllegalState();
//                }
              _charBuffer[_charBufferLength++] = c;
            }
            case DecoderStateTables.UTF8_THREE_BYTES:
                final char c = decodeUtf8ThreeByteChar(end, b1);
                // XMLChar
//                if (XMLChar.isNCNameStart(c)) {
//                    _charBuffer[_charBufferLength++] = c;
//                    break;
//                } else {
//                    decodeUtf8NCNameIllegalState();
//                }
                _charBuffer[_charBufferLength++] = c;
                break;
                // Supplementary UTF-8
//            case DecoderStateTables.UTF8_FOUR_BYTES:
//            {
//                final int supplemental = decodeUtf8FourByteChar(end, b1);
//                if (XMLChar.isNCNameStart(supplemental)) {
//                    _charBuffer[_charBufferLength++] = _utf8_highSurrogate;
//                    _charBuffer[_charBufferLength++] = _utf8_lowSurrogate;
//                } else {
//                    decodeUtf8NCNameIllegalState();
//                }                
//                break;
//            }
            case DecoderStateTables.UTF8_NCNAME_NCNAME_CHAR:
            default:
                decodeUtf8NCNameIllegalState();
        }
        
    }

    public final void decodeUtf8NCNameTwoToFourByteCharacters(int b1, int end) throws IOException {
        switch(DecoderStateTables.UTF8_NCNAME[b1]) {
            case DecoderStateTables.UTF8_TWO_BYTES:
            {
                // Decode byte 2
                if (end == _octetBufferOffset) {
                    decodeUtf8StringLengthTooSmall();
                }
                final int b2 = _octetBuffer[_octetBufferOffset++] & 0xFF;
                if ((b2 & 0xC0) != 0x80) {
                    decodeUtf8StringIllegalState();
                }

                final char c = (char) (
                    ((b1 & 0x1F) << 6)
                    | (b2 & 0x3F));
                // XMLChar
//                if (XMLChar.isNCName(c)) {
//                    _charBuffer[_charBufferLength++] = c;
//                    break;
//                } else {
//                    decodeUtf8NCNameIllegalState();
//                }
                _charBuffer[_charBufferLength++] = c;
            }
            case DecoderStateTables.UTF8_THREE_BYTES:
                final char c = decodeUtf8ThreeByteChar(end, b1);
                // XMLChar
//                if (XMLChar.isNCName(c)) {
//                    _charBuffer[_charBufferLength++] = c;
//                    break;
//                } else {
//                    decodeUtf8NCNameIllegalState();
//                }
                _charBuffer[_charBufferLength++] = c;
                break;
                // Supplementary UTF-8
//            case DecoderStateTables.UTF8_FOUR_BYTES:
//            {
//                final int supplemental = decodeUtf8FourByteChar(end, b1);
//                if (XMLChar.isNCName(supplemental)) {
//                    _charBuffer[_charBufferLength++] = _utf8_highSurrogate;
//                    _charBuffer[_charBufferLength++] = _utf8_lowSurrogate;
//                } else {
//                    decodeUtf8NCNameIllegalState();
//                }                
//                break;
//            }
            default:
                decodeUtf8NCNameIllegalState();
        }
    }
    
    public final char decodeUtf8ThreeByteChar(int end, int b1) throws IOException {
        // Decode byte 2
        if (end == _octetBufferOffset) {
            decodeUtf8StringLengthTooSmall();
        }
        final int b2 = _octetBuffer[_octetBufferOffset++] & 0xFF;
        if ((b2 & 0xC0) != 0x80
            || (b1 == 0xED && b2 >= 0xA0)
            || ((b1 & 0x0F) == 0 && (b2 & 0x20) == 0)) {
            decodeUtf8StringIllegalState();
        }

        // Decode byte 3
        if (end == _octetBufferOffset) {
            decodeUtf8StringLengthTooSmall();
        }
        final int b3 = _octetBuffer[_octetBufferOffset++] & 0xFF;
        if ((b3 & 0xC0) != 0x80) {
            decodeUtf8StringIllegalState();
        }

        return (char) (
            (b1 & 0x0F) << 12
            | (b2 & 0x3F) << 6
            | (b3 & 0x3F));
    }

    // Supplementary UTF-8
/*    
    private char _utf8_highSurrogate;
    private char _utf8_lowSurrogate;
    
  public final int decodeUtf8FourByteChar(int end, int b1) throws IOException {
        // Decode byte 2
        if (end == _octetBufferOffset) {
            decodeUtf8StringLengthTooSmall();
        }
        final int b2 = _octetBuffer[_octetBufferOffset++] & 0xFF;
        if ((b2 & 0xC0) != 0x80
            || ((b2 & 0x30) == 0 && (b1 & 0x07) == 0)) {
            decodeUtf8StringIllegalState();
        }

        // Decode byte 3
        if (end == _octetBufferOffset) {
            decodeUtf8StringLengthTooSmall();
        }
        final int b3 = _octetBuffer[_octetBufferOffset++] & 0xFF;
        if ((b3 & 0xC0) != 0x80) {
            decodeUtf8StringIllegalState();
        }

        // Decode byte 4
        if (end == _octetBufferOffset) {
            decodeUtf8StringLengthTooSmall();
        }
        final int b4 = _octetBuffer[_octetBufferOffset++] & 0xFF;
        if ((b4 & 0xC0) != 0x80) {
            decodeUtf8StringIllegalState();
        }

        final int uuuuu = ((b1 << 2) & 0x001C) | ((b2 >> 4) & 0x0003);
        if (uuuuu > 0x10) {
            decodeUtf8StringIllegalState();
        }
        final int wwww = uuuuu - 1;

        _utf8_highSurrogate = (char) (0xD800 |
             ((wwww << 6) & 0x03C0) | ((b2 << 2) & 0x003C) |
             ((b3 >> 4) & 0x0003));
        _utf8_lowSurrogate = (char) (0xDC00 | ((b3 << 6) & 0x03C0) | (b4 & 0x003F));
        
        return XMLChar.supplemental(_utf8_highSurrogate, _utf8_lowSurrogate);
    }
*/
    public final void decodeUtf8StringLengthTooSmall() throws IOException {
        throw new IOException(MessageCenter.getString("message.deliminatorTooSmall"));
    }

    public final void decodeUtf8StringIllegalState() throws IOException {
        throw new IOException(MessageCenter.getString("message.UTF8Encoded"));
    }

    public final void decodeUtf8NCNameIllegalState() throws IOException {
        throw new IOException(MessageCenter.getString("message.UTF8EncodedNCName"));
    }

    public final void decodeUtf16StringIntoCharBuffer() throws IOException {
        _charBufferLength = _octetBufferLength / 2;
        if (_charBuffer.length < _charBufferLength) {
            _charBuffer = new char[_charBufferLength];
        }

        for (int i = 0; i < _charBufferLength; i++) {
            final char c = (char)((read() << 8) | read());
            // TODO check c is a valid Char character
            _charBuffer[i] = c;
        }
        
    }

    public String createQualifiedNameString(char[] first, String second) {
        final int l1 = first.length;
        final int l2 = second.length();
        final int total = l1 + l2 + 1;
        if (total < _charBuffer.length) {
            System.arraycopy(first, 0, _charBuffer, 0, l1);
            _charBuffer[l1] = ':';
            second.getChars(0, l2, _charBuffer, l1 + 1);
            return new String(_charBuffer, 0, total);
        } else {
            StringBuffer b = new StringBuffer(new String(first));
            b.append(':');
            b.append(second);
            return b.toString();
        }
    }
    
    protected final int read() throws IOException {
        if (_octetBufferOffset < _octetBufferEnd) {
            return _octetBuffer[_octetBufferOffset++] & 0xFF;
        } else {
            _octetBufferEnd = _s.read(_octetBuffer);
            if (_octetBufferEnd < 0) {
                throw new EOFException(MessageCenter.getString("message.EOF"));
            }

            _octetBufferOffset = 1;
            return _octetBuffer[0] & 0xFF;
        }
    }

    protected final int peak() throws IOException {
        if (_octetBufferOffset < _octetBufferEnd) {
            return _octetBuffer[_octetBufferOffset] & 0xFF;
        } else {
            _octetBufferEnd = _s.read(_octetBuffer);
            if (_octetBufferEnd < 0) {
                throw new EOFException(MessageCenter.getString("message.EOF"));
            }

            _octetBufferOffset = 0;
            return _octetBuffer[0] & 0xFF;
        }
    }
    
    protected class EncodingAlgorithmInputStream extends InputStream {

        public int read() throws IOException {
            if (_octetBufferStart < _octetBufferOffset) {
                return (_octetBuffer[_octetBufferStart++] & 0xFF);
            } else {
                return -1;
            }
        }

        public int read(byte b[]) throws IOException {
            return read(b, 0, b.length);
        }

        public int read(byte b[], int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if ((off < 0) || (off > b.length) || (len < 0) ||
                       ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }

            final int newOctetBufferStart = _octetBufferStart + len;
            if (newOctetBufferStart < _octetBufferOffset) {
                System.arraycopy(_octetBuffer, _octetBufferStart, b, off, len);
                _octetBufferStart = newOctetBufferStart;
                return len;
            } else if (_octetBufferStart < _octetBufferOffset) {
                final int bytesToRead = _octetBufferOffset - _octetBufferStart;
                System.arraycopy(_octetBuffer, _octetBufferStart, b, off, bytesToRead);
                _octetBufferStart += bytesToRead;
                return bytesToRead;
            } else {
                return -1;
            }
        }
    }

    protected final boolean _isFastInfosetDocument() throws IOException {
        // Fill up the octet buffer
        peak();
        
        _octetBufferLength = EncodingConstants.BINARY_HEADER.length;
        ensureOctetBufferSize();
        _octetBufferOffset += _octetBufferLength;
        
        // Check for binary header
        if (_octetBuffer[0] != EncodingConstants.BINARY_HEADER[0] ||
                _octetBuffer[1] != EncodingConstants.BINARY_HEADER[1] ||
                _octetBuffer[2] != EncodingConstants.BINARY_HEADER[2] ||
                _octetBuffer[3] != EncodingConstants.BINARY_HEADER[3]) {
                     
            // Check for each form of XML declaration
            for (int i = 0; i < EncodingConstants.XML_DECLARATION_VALUES.length; i++) {
                _octetBufferLength = EncodingConstants.XML_DECLARATION_VALUES[i].length - _octetBufferOffset;
                ensureOctetBufferSize();
                _octetBufferOffset += _octetBufferLength;

                // Check XML declaration
                if (arrayEquals(_octetBuffer, 0, 
                        EncodingConstants.XML_DECLARATION_VALUES[i], 
                        EncodingConstants.XML_DECLARATION_VALUES[i].length)) {
                    _octetBufferLength = EncodingConstants.BINARY_HEADER.length;
                    ensureOctetBufferSize();
                    
                    // Check for binary header
                    if (_octetBuffer[_octetBufferOffset++] != EncodingConstants.BINARY_HEADER[0] ||
                            _octetBuffer[_octetBufferOffset++] != EncodingConstants.BINARY_HEADER[1] ||
                            _octetBuffer[_octetBufferOffset++] != EncodingConstants.BINARY_HEADER[2] ||
                            _octetBuffer[_octetBufferOffset++] != EncodingConstants.BINARY_HEADER[3]) {
                        return false;
                    } else {
                        // Fast Infoset document with XML declaration and binary header
                        return true;
                    }
                }
            }
            
            return false;
        }

        // Fast Infoset document with binary header
        return true;
    }

    protected final boolean arrayEquals(byte[] b1, int offset, byte[] b2, int length) {
        for (int i = 0; i < length; i++) {
            if (b1[offset + i] != b2[i]) {
                return false;
            }
        }
        
        return true;
    }

    static public boolean isFastInfosetDocument(InputStream s) throws IOException {
        // TODO
        // Check for <?xml declaration with 'finf' encoding

        final byte[] header = new byte[4];
        s.read(header);
        if (header[0] != EncodingConstants.BINARY_HEADER[0] ||
                header[1] != EncodingConstants.BINARY_HEADER[1] ||
                header[2] != EncodingConstants.BINARY_HEADER[2] ||
                header[3] != EncodingConstants.BINARY_HEADER[3]) {
            return false;
        }

        // TODO
        return true;
    }
}
