/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.fime.jvnet;

public class ReferencedVocabulary {
    private final String URI;
    private final Vocabulary vocabulary;
    
    public ReferencedVocabulary(String URI, Vocabulary vocabulary) {
        this.URI = URI;
        this.vocabulary = vocabulary; 
    }

    public String getURI() {
        return URI;
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }
}
