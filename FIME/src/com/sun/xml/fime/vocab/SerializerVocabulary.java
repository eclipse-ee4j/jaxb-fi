/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * FIME (Fast Infoset ME) software ("Software")
 * 
 * Software is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at:
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations.
 * 
 *    Sun supports and benefits from the global community of open source
 * developers, and thanks the community for its important contributions and
 * open standards-based technology, which Sun has adopted into many of its
 * products.
 * 
 *    Please note that portions of Software may be provided with notices and
 * open source licenses from such communities and third parties that govern the
 * use of those portions, and any licenses granted hereunder do not alter any
 * rights and obligations you may have under such open source licenses,
 * however, the disclaimer of warranty and limitation of liability provisions
 * in this License will apply to all Software in this distribution.
 * 
 *    You acknowledge that the Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
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
