/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */


package com.sun.xml.fime.vocab;

import com.sun.xml.fime.EncodingConstants;
import com.sun.xml.fime.util.CharArrayIntMap;
import com.sun.xml.fime.util.FixedEntryStringIntMap;
import com.sun.xml.fime.util.KeyIntMap;
import com.sun.xml.fime.util.LocalNameQualifiedNamesMap;
import com.sun.xml.fime.util.StringIntMap;


public class SerializerVocabulary extends Vocabulary {
    public final static int ATTRIBUTE_VALUE_SIZE_CONSTRAINT = 7;
    public final static int CHARACTER_CONTENT_CHUNK_SIZE_CONSTRAINT = 7;
    
    public final StringIntMap restrictedAlphabet;
    public final StringIntMap encodingAlgorithm;

    public final StringIntMap namespaceName;
    public final StringIntMap prefix;
    public final StringIntMap localName;
    public final StringIntMap otherNCName;
    public final StringIntMap otherURI;
    public final StringIntMap attributeValue;
    public final CharArrayIntMap otherString;

    public final CharArrayIntMap characterContentChunk;

    public final LocalNameQualifiedNamesMap elementName;
    public final LocalNameQualifiedNamesMap attributeName;
    
    public final KeyIntMap[] tables = new KeyIntMap[12];

    public int attributeValueSizeConstraint = ATTRIBUTE_VALUE_SIZE_CONSTRAINT;
    public int characterContentChunkSizeContraint = CHARACTER_CONTENT_CHUNK_SIZE_CONSTRAINT;
    
    protected SerializerVocabulary _readOnlyVocabulary;
    
    public SerializerVocabulary() {
        tables[RESTRICTED_ALPHABET] = restrictedAlphabet = new StringIntMap(4);
        tables[ENCODING_ALGORITHM] = encodingAlgorithm = new StringIntMap(4);
        tables[PREFIX] = prefix = new FixedEntryStringIntMap(EncodingConstants.XML_NAMESPACE_PREFIX, 8);
        tables[NAMESPACE_NAME] = namespaceName = new FixedEntryStringIntMap(EncodingConstants.XML_NAMESPACE_NAME, 8);
        tables[LOCAL_NAME] = localName = new StringIntMap();
        tables[OTHER_NCNAME] = otherNCName = new StringIntMap(4);
        tables[OTHER_URI] = otherURI = new StringIntMap(4);
        tables[ATTRIBUTE_VALUE] = attributeValue = new StringIntMap();
        tables[OTHER_STRING] = otherString = new CharArrayIntMap(4);
        tables[CHARACTER_CONTENT_CHUNK] = characterContentChunk = new CharArrayIntMap();
        tables[ELEMENT_NAME] = elementName = new LocalNameQualifiedNamesMap();
        tables[ATTRIBUTE_NAME] = attributeName = new LocalNameQualifiedNamesMap();        
    }
        
    public SerializerVocabulary getReadOnlyVocabulary() {
        return _readOnlyVocabulary;
    }
    
    protected void setReadOnlyVocabulary(SerializerVocabulary readOnlyVocabulary, boolean clear) {
        for (int i = 0; i < tables.length; i++) {
            tables[i].setReadOnlyMap(readOnlyVocabulary.tables[i], clear);
        }
    }

    public void setInitialVocabulary(SerializerVocabulary initialVocabulary, boolean clear) {
        setExternalVocabularyURI(null);
        setInitialReadOnlyVocabulary(true);
        setReadOnlyVocabulary(initialVocabulary, clear);
    }
    
    public void setExternalVocabulary(String externalVocabularyURI, SerializerVocabulary externalVocabulary, boolean clear) {
        setInitialReadOnlyVocabulary(false);
        setExternalVocabularyURI(externalVocabularyURI);
        setReadOnlyVocabulary(externalVocabulary, clear);
    }
    
    public void clear() {
        for (int i = 0; i < tables.length; i++) {
            tables[i].clear();
        }
    }
}
