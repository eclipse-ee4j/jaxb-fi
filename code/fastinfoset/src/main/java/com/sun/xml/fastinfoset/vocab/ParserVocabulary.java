/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.fastinfoset.vocab;

import com.sun.xml.fastinfoset.EncodingConstants;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.util.CharArray;
import com.sun.xml.fastinfoset.util.CharArrayArray;
import com.sun.xml.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.fastinfoset.util.DuplicateAttributeVerifier;
import com.sun.xml.fastinfoset.util.FixedEntryStringIntMap;
import com.sun.xml.fastinfoset.util.KeyIntMap;
import com.sun.xml.fastinfoset.util.PrefixArray;
import com.sun.xml.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.fastinfoset.util.StringArray;
import com.sun.xml.fastinfoset.util.StringIntMap;
import com.sun.xml.fastinfoset.util.ValueArray;
import java.util.Iterator;
import javax.xml.namespace.QName;

public class ParserVocabulary extends Vocabulary {
    public static final String IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY = 
        "com.sun.xml.fastinfoset.vocab.ParserVocabulary.IdentifyingStringTable.maximumItems";
    public static final String NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY = 
        "com.sun.xml.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumItems";
    public static final String NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS_PEOPERTY = 
        "com.sun.xml.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumCharacters";

    protected static final int IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS =
            getIntegerValueFromProperty(IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY);
    protected static final int NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS = 
            getIntegerValueFromProperty(NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY);
    protected static final int NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS =
            getIntegerValueFromProperty(NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS_PEOPERTY);
        
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
    public final StringArray encodingAlgorithm = new StringArray(ValueArray.DEFAULT_CAPACITY, 256, true);

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
        namespaceName = new StringArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, false);
        prefix = new PrefixArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        localName = new StringArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, false);
        otherNCName = new StringArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, false);
        otherURI = new StringArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, true);
        attributeValue = new StringArray(ValueArray.DEFAULT_CAPACITY, NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, true);
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

    
    public ParserVocabulary(org.jvnet.fastinfoset.Vocabulary v) {
        this();
        
        convertVocabulary(v);
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
        if (!referencedVocabularyURI.equals(getExternalVocabularyURI())) {
            setInitialReadOnlyVocabulary(false);
            setExternalVocabularyURI(referencedVocabularyURI);
            setReadOnlyVocabulary(referencedVocabulary, clear);
        }
    }
    
    public void clear() {
        for (int i = 0; i < tables.length; i++) {
            tables[i].clear();
        }        
    }
    
    private void convertVocabulary(org.jvnet.fastinfoset.Vocabulary v) {
        final StringIntMap prefixMap = new FixedEntryStringIntMap(
                EncodingConstants.XML_NAMESPACE_PREFIX, 8);
        final StringIntMap namespaceNameMap = new FixedEntryStringIntMap(
                EncodingConstants.XML_NAMESPACE_NAME, 8);
        final StringIntMap localNameMap = new StringIntMap();
                
        addToTable(v.restrictedAlphabets.iterator(), restrictedAlphabet);
        addToTable(v.encodingAlgorithms.iterator(), encodingAlgorithm);
        addToTable(v.prefixes.iterator(), prefix, prefixMap);
        addToTable(v.namespaceNames.iterator(), namespaceName, namespaceNameMap);
        addToTable(v.localNames.iterator(), localName, localNameMap);
        addToTable(v.otherNCNames.iterator(), otherNCName);
        addToTable(v.otherURIs.iterator(), otherURI);
        addToTable(v.attributeValues.iterator(), attributeValue);
        addToTable(v.otherStrings.iterator(), otherString);
        addToTable(v.characterContentChunks.iterator(), characterContentChunk);
        addToTable(v.elements.iterator(), elementName, false,
                prefixMap, namespaceNameMap, localNameMap);
        addToTable(v.attributes.iterator(), attributeName, true,
                prefixMap, namespaceNameMap, localNameMap);                            
    }
    
    private void addToTable(Iterator i, StringArray a) {
        while (i.hasNext()) {
            addToTable((String)i.next(), a, null);
        }        
    }
    
    private void addToTable(Iterator i, StringArray a, StringIntMap m) {
        while (i.hasNext()) {
            addToTable((String)i.next(), a, m);
        }        
    }
    
    private void addToTable(String s, StringArray a, StringIntMap m) {
        if (s.length() == 0) {
            return;
        }
        
        if (m != null) m.obtainIndex(s);
        a.add(s);
    }
    
    private void addToTable(Iterator i, PrefixArray a, StringIntMap m) {
        while (i.hasNext()) {
            addToTable((String)i.next(), a, m);
        }        
    }
    
    private void addToTable(String s, PrefixArray a, StringIntMap m) {
        if (s.length() == 0) {
            return;
        }
        
        if (m != null) m.obtainIndex(s);
        a.add(s);
    }
    
    private void addToTable(Iterator i, ContiguousCharArrayArray a) {
        while (i.hasNext()) {
            addToTable((String)i.next(), a);
        }        
    }
    
    private void addToTable(String s, ContiguousCharArrayArray a) {
        if (s.length() == 0) {
            return;
        }
        
        char[] c = s.toCharArray();
        a.add(c, c.length);
    }
    
    private void addToTable(Iterator i, CharArrayArray a) {
        while (i.hasNext()) {
            addToTable((String)i.next(), a);
        }        
    }
    
    private void addToTable(String s, CharArrayArray a) {
        if (s.length() == 0) {
            return;
        }
        
        char[] c = s.toCharArray();
        a.add(new CharArray(c, 0, c.length, false));
    }
    
    private void addToTable(Iterator i, QualifiedNameArray a,
            boolean isAttribute, 
            StringIntMap prefixMap, StringIntMap namespaceNameMap, 
            StringIntMap localNameMap) {
        while (i.hasNext()) {
            addToNameTable((QName)i.next(), a, isAttribute,
                    prefixMap, namespaceNameMap, localNameMap);
        }        
    }
    
    private void addToNameTable(QName n, QualifiedNameArray a, 
            boolean isAttribute,
            StringIntMap prefixMap, StringIntMap namespaceNameMap, 
            StringIntMap localNameMap) {            
        int namespaceURIIndex = -1;
        int prefixIndex = -1;
        if (n.getNamespaceURI().length() > 0) {
            namespaceURIIndex = namespaceNameMap.obtainIndex(n.getNamespaceURI());
            if (namespaceURIIndex == KeyIntMap.NOT_PRESENT) {
                namespaceURIIndex = namespaceName.getSize();
                namespaceName.add(n.getNamespaceURI());
            }
            
            if (n.getPrefix().length() > 0) {
                prefixIndex = prefixMap.obtainIndex(n.getPrefix());
                if (prefixIndex == KeyIntMap.NOT_PRESENT) {
                    prefixIndex = prefix.getSize();
                    prefix.add(n.getPrefix());
                }
            }
        }
        
        int localNameIndex = localNameMap.obtainIndex(n.getLocalPart());
        if (localNameIndex == KeyIntMap.NOT_PRESENT) {
            localNameIndex = localName.getSize();
            localName.add(n.getLocalPart());
        }
        
        QualifiedName name = new QualifiedName(n.getPrefix(), n.getNamespaceURI(), n.getLocalPart(),
                a.getSize(), 
                prefixIndex, namespaceURIIndex, localNameIndex);
        if (isAttribute) {
            name.createAttributeValues(DuplicateAttributeVerifier.MAP_SIZE);
        }
        a.add(name);
    }    
}
