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

public abstract class Vocabulary {
    public static final int RESTRICTED_ALPHABET = 0;
    public static final int ENCODING_ALGORITHM = 1;
    public static final int PREFIX = 2;
    public static final int NAMESPACE_NAME = 3;
    public static final int LOCAL_NAME = 4;
    public static final int OTHER_NCNAME = 5;
    public static final int OTHER_URI = 6;
    public static final int ATTRIBUTE_VALUE = 7;
    public static final int OTHER_STRING = 8;
    public static final int CHARACTER_CONTENT_CHUNK = 9;
    public static final int ELEMENT_NAME = 10;
    public static final int ATTRIBUTE_NAME = 11;
        
    protected boolean _hasInitialReadOnlyVocabulary;
    
    protected String _referencedVocabularyURI;
    
    public boolean hasInitialVocabulary() {
        return _hasInitialReadOnlyVocabulary;
    }

    protected void setInitialReadOnlyVocabulary(boolean hasInitialReadOnlyVocabulary) {
        _hasInitialReadOnlyVocabulary = hasInitialReadOnlyVocabulary;
    }
    
    public boolean hasExternalVocabulary() {
        return _referencedVocabularyURI != null;
    }
    
    public String getExternalVocabularyURI() {
        return _referencedVocabularyURI;
    }
    
    protected void setExternalVocabularyURI(String referencedVocabularyURI) {
        _referencedVocabularyURI = referencedVocabularyURI;
    }

}
