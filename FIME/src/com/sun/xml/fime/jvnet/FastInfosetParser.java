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
public interface FastInfosetParser {
    /**
     * The property name to be used for getting and setting the string 
     * interning property of a parser.
     *
     */
    public static final String STRING_INTERNING_PROPERTY = 
        "http://jvnet.org/fastinfoset/parser/properties/string-interning";

    /**
     * The property name to be used for getting and setting the buffer size
     * of a parser.
     */
    public static final String BUFFER_SIZE_PROPERTY = 
        "http://jvnet.org/fastinfoset/parser/properties/buffer-size";

    /**
     * The property name to be used for getting and setting the 
     * Map containing encoding algorithms.
     *
     */    
    public static final String REGISTERED_ENCODING_ALGORITHMS_PROPERTY =
        "http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms";
    
   /**
     * The property name to be used for getting and setting the 
     * Map containing external vocabularies.
     *
     */    
    public static final String EXTERNAL_VOCABULARIES_PROPERTY =
        "http://jvnet.org/fastinfoset/parser/properties/external-vocabularies";
    
   
    /**
     * Set the string interning property.
     *
     * <p>If the string interning property is set to true then 
     * <code>String</code> objects instantiated for [namespace name], [prefix] 
     * and [local name] infoset properties will be interned using the method 
     * {@link String#intern()}.
     *
     * @param stringInterning The string interning property.
     */
    public void setStringInterning(boolean stringInterning);
    
    /**
     * Return the string interning property.
     *
     * @return The string interning property.
     */
    public boolean getStringInterning();
    
    /**
     * Set the buffer size.
     *
     * <p>The size of the buffer for parsing is set using this
     * method. Requests for sizes smaller then the current size will be ignored.
     * Otherwise the buffer will be resized when the next parse is performed.<p>
     *
     * @param bufferSize The requested buffer size.
     */
    public void setBufferSize(int bufferSize);
    
    
    /**
     * Get the buffer size.
     *
     * @return The buffer size.
     */
    public int getBufferSize();
    

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


    
    // Vocabulary methods
    
    // Before parsing 
    
    public void setExternalVocabularies(Hashtable referencedVocabualries);
    
    public void setDynamicVocabulary(Vocabulary v);
    
    
    // After parsing
    
    public ReferencedVocabulary getExternalVocabulary();
    
    public Vocabulary getIntitialVocabulary();

    public Vocabulary getDynamicVocabulary();

    public Vocabulary getFinalVocabulary();    
}
