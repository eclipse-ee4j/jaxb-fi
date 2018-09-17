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

import java.util.Hashtable;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public interface FastInfosetSerializer {
    public static final String UTF_8 = "UTF-8";
    
    public static final String UTF_16BE = "UTF-16BE";
    
    /**
     * Sets the character encoding scheme.
     *
     * The character encoding can be either UTF-8 or UTF-16BE for the
     * the encoding of chunks of CIIs, the [normalized value]
     * property of attribute information items, comment information
     * items and processing instruction information items.
     *
     * @param characterEncodingScheme The set of registered algorithms.
     */
    public void setCharacterEncodingScheme(String characterEncodingScheme);
    
    /**
     * Gets the character encoding scheme.
     *
     * @return The character encoding scheme.
     */
    public String getCharacterEncodingScheme();
    
    /**
     * Sets the set of registered encoding algorithms.
     *
     * @param algorithms The set of registered algorithms.
     */
    public void setRegisteredEncodingAlgorithms(Hashtable algorithms);
    
    /**
     * Gets the set of registered encoding algorithms.
     *
     * @return The set of registered algorithms.
     */
    public Hashtable getRegisteredEncodingAlgorithms();
    
    // Before serializing
    
    public void setExternalVocabulary(ReferencedVocabulary referencedVocabulary);
    
    public void setIntitialVocabulary(Vocabulary initialVocabulary);
    
    public void setDynamicVocabulary(Vocabulary dynamicVocabulary);

    
    // After serializing

    public Vocabulary getDynamicVocabulary();

    public Vocabulary getFinalVocabulary();    
}
