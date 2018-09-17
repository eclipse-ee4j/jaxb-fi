/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.fime.vocab;

import com.sun.xml.fime.util.CharArrayArray;
import com.sun.xml.fime.util.ContiguousCharArrayArray;
import com.sun.xml.fime.util.PrefixArray;
import com.sun.xml.fime.util.QualifiedNameArray;
import com.sun.xml.fime.util.StringArray;
import com.sun.xml.fime.util.ValueArray;

public class ParserVocabulary extends Vocabulary {
    public static final String IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY = 
        "com.sun.xml.fastinfoset.vocab.ParserVocabulary.IdentifyingStringTable.maximumItems";
    public static final String NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY = 
        "com.sun.xml.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumItems";
    public static final String NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS_PEOPERTY = 
        "com.sun.xml.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumCharacters";

    protected static int IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS;
    protected static int NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS; 
    protected static int NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS;
    
    static {
        IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS = 
                getIntegerValueFromProperty(IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY);
        NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS = 
                getIntegerValueFromProperty(NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY);
        NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS = 
                getIntegerValueFromProperty(NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS_PEOPERTY);
    }
    
    private static int getIntegerValueFromProperty(String property) {
        String value = System.getProperty(property);
        if (value == null) {
            return Integer.MAX_VALUE;
        }
        
        try {
            return Math.max(Integer.parseInt(value), ValueArray.DEFAULT_CAPACITY);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
    
    public final CharArrayArray restrictedAlphabet = new CharArrayArray(ValueArray.DEFAULT_CAPACITY, 256);
    public final StringArray encodingAlgorithm = new StringArray(ValueArray.DEFAULT_CAPACITY, 256);

    public final StringArray namespaceName;
    public final PrefixArray prefix;
    public final StringArray localName;
    public final StringArray otherNCName ;
    public final StringArray otherURI;
    public final StringArray attributeValue;
    public final CharArrayArray otherString;

    public final ContiguousCharArrayArray characterContentChunk;

    public final QualifiedNameArray elementName;
    public final QualifiedNameArray attributeName;

    public final ValueArray[] tables = new ValueArray[12];
    
    protected SerializerVocabulary _readOnlyVocabulary;
    
    /** Creates a new instance of ParserVocabulary */
    public ParserVocabulary() {
        namespaceName = new StringArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        prefix = new PrefixArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        localName = new StringArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        otherNCName = new StringArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        otherURI = new StringArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        attributeValue = new StringArray(ValueArray.DEFAULT_CAPACITY, NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        otherString = new CharArrayArray(ValueArray.DEFAULT_CAPACITY, NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);

        characterContentChunk = new ContiguousCharArrayArray(ValueArray.DEFAULT_CAPACITY, 
                NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, 
                ContiguousCharArrayArray.INITIAL_CHARACTER_SIZE, 
                NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS);

        elementName = new QualifiedNameArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        attributeName = new QualifiedNameArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);

        tables[RESTRICTED_ALPHABET] = restrictedAlphabet;
        tables[ENCODING_ALGORITHM] = encodingAlgorithm;
        tables[PREFIX] = prefix;
        tables[NAMESPACE_NAME] = namespaceName;
        tables[LOCAL_NAME] = localName;
        tables[OTHER_NCNAME] = otherNCName;
        tables[OTHER_URI] = otherURI;
        tables[ATTRIBUTE_VALUE] = attributeValue;
        tables[OTHER_STRING] = otherString;
        tables[CHARACTER_CONTENT_CHUNK] = characterContentChunk;
        tables[ELEMENT_NAME] = elementName;
        tables[ATTRIBUTE_NAME] = attributeName;
    }

    
    public ParserVocabulary(SerializerVocabulary vocab) {
        this();
        
    }
    
    void setReadOnlyVocabulary(ParserVocabulary readOnlyVocabulary, boolean clear) {
        for (int i = 0; i < tables.length; i++) {
            tables[i].setReadOnlyArray(readOnlyVocabulary.tables[i], clear);
        }
    }
    
    public void setInitialVocabulary(ParserVocabulary initialVocabulary, boolean clear) {
        setExternalVocabularyURI(null);
        setInitialReadOnlyVocabulary(true);
        setReadOnlyVocabulary(initialVocabulary, clear);
    }
    
    public void setReferencedVocabulary(String referencedVocabularyURI, ParserVocabulary referencedVocabulary, boolean clear) {
        setInitialReadOnlyVocabulary(false);
        setExternalVocabularyURI(referencedVocabularyURI);
        setReadOnlyVocabulary(referencedVocabulary, clear);
    }
    
    public void clear() {
        for (int i = 0; i < tables.length; i++) {
            tables[i].clear();
        }        
    }
}
