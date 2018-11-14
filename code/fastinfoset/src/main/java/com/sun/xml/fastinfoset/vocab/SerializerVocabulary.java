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
import com.sun.xml.fastinfoset.util.CharArrayIntMap;
import com.sun.xml.fastinfoset.util.FixedEntryStringIntMap;
import com.sun.xml.fastinfoset.util.KeyIntMap;
import com.sun.xml.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.fastinfoset.util.StringIntMap;
import java.util.Iterator;
import javax.xml.namespace.QName;

public class SerializerVocabulary extends Vocabulary {
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

    protected boolean _useLocalNameAsKey;
    
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
        
    public SerializerVocabulary(org.jvnet.fastinfoset.Vocabulary v, 
            boolean useLocalNameAsKey) {
        this();
        
        _useLocalNameAsKey = useLocalNameAsKey;
        convertVocabulary(v);
    }
    
    public SerializerVocabulary getReadOnlyVocabulary() {
        return _readOnlyVocabulary;
    }
    
    protected void setReadOnlyVocabulary(SerializerVocabulary readOnlyVocabulary, 
            boolean clear) {
        for (int i = 0; i < tables.length; i++) {
            tables[i].setReadOnlyMap(readOnlyVocabulary.tables[i], clear);
        }
    }

    public void setInitialVocabulary(SerializerVocabulary initialVocabulary, 
            boolean clear) {
        setExternalVocabularyURI(null);
        setInitialReadOnlyVocabulary(true);
        setReadOnlyVocabulary(initialVocabulary, clear);
    }
    
    public void setExternalVocabulary(String externalVocabularyURI, 
            SerializerVocabulary externalVocabulary, boolean clear) {
        setInitialReadOnlyVocabulary(false);
        setExternalVocabularyURI(externalVocabularyURI);
        setReadOnlyVocabulary(externalVocabulary, clear);
    }
    
    public void clear() {
        for (int i = 0; i < tables.length; i++) {
            tables[i].clear();
        }
    }
    
    private void convertVocabulary(org.jvnet.fastinfoset.Vocabulary v) {
        addToTable(v.restrictedAlphabets.iterator(), restrictedAlphabet);
        addToTable(v.encodingAlgorithms.iterator(), encodingAlgorithm);
        addToTable(v.prefixes.iterator(), prefix);
        addToTable(v.namespaceNames.iterator(), namespaceName);
        addToTable(v.localNames.iterator(), localName);
        addToTable(v.otherNCNames.iterator(), otherNCName);
        addToTable(v.otherURIs.iterator(), otherURI);
        addToTable(v.attributeValues.iterator(), attributeValue);
        addToTable(v.otherStrings.iterator(), otherString);
        addToTable(v.characterContentChunks.iterator(), characterContentChunk);
        addToTable(v.elements.iterator(), elementName);
        addToTable(v.attributes.iterator(), attributeName);                            
    }
    
    private void addToTable(Iterator i, StringIntMap m) {
        while (i.hasNext()) {
            addToTable((String)i.next(), m);
        }        
    }
    
    private void addToTable(String s, StringIntMap m) {
        if (s.length() == 0) {
            return;
        }
        
        m.obtainIndex(s);
    }
    
    private void addToTable(Iterator i, CharArrayIntMap m) {
        while (i.hasNext()) {
            addToTable((String)i.next(), m);
        }        
    }
    
    private void addToTable(String s, CharArrayIntMap m) {
        if (s.length() == 0) {
            return;
        }
        
        char[] c = s.toCharArray();
        m.obtainIndex(c, 0, c.length, false);
    }
    
    private void addToTable(Iterator i, LocalNameQualifiedNamesMap m) {
        while (i.hasNext()) {
            addToNameTable((QName)i.next(), m);
        }        
    }
    
    private void addToNameTable(QName n, LocalNameQualifiedNamesMap m) {                
        int namespaceURIIndex = -1;
        int prefixIndex = -1;
        if (n.getNamespaceURI().length() > 0) {
            namespaceURIIndex = namespaceName.obtainIndex(n.getNamespaceURI());
            if (namespaceURIIndex == KeyIntMap.NOT_PRESENT) {
                namespaceURIIndex = namespaceName.get(n.getNamespaceURI());
            }
            
            if (n.getPrefix().length() > 0) {
                prefixIndex = prefix.obtainIndex(n.getPrefix());
                if (prefixIndex == KeyIntMap.NOT_PRESENT) {
                    prefixIndex = prefix.get(n.getPrefix());
                }
            }
        }
        
        int localNameIndex = localName.obtainIndex(n.getLocalPart());
        if (localNameIndex == KeyIntMap.NOT_PRESENT) {
            localNameIndex = localName.get(n.getLocalPart());
        }
        
        QualifiedName name = new QualifiedName(n.getPrefix(), n.getNamespaceURI(), n.getLocalPart(),
                m.getNextIndex(), 
                prefixIndex, namespaceURIIndex, localNameIndex);
        
        LocalNameQualifiedNamesMap.Entry entry = null;
        if (_useLocalNameAsKey) {
            entry = m.obtainEntry(n.getLocalPart());
        } else {
            String qName = (prefixIndex == -1)
                ? n.getLocalPart() 
                : n.getPrefix() + ":" + n.getLocalPart();
            entry = m.obtainEntry(qName);
        }
        
        entry.addQualifiedName(name);
    }    
}
