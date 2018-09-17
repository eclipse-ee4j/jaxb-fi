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

import org.xml.sax.Attributes;


/**
 * Interface for a list of XML attributes that may contain encoding algorithm
 * data.
 *
 * @version 0.1
 * @see org.jvnet.fastinfoset.sax.FastInfosetReader
 * @see org.xml.sax.XMLReader
 */
public interface EncodingAlgorithmAttributes extends Attributes {
    
    /**
     * Return the URI of the encoding algorithm.
     *
     * <p>If the algorithm data corresponds to a built-in encoding algorithm
     *    then the null is returned.</p>
     *
     * <p>If the algorithm data corresponds to an application-defined encoding 
     *    algorithm then the URI of the algorithm is returned.</p>
     *
     * <p>If {@link #getAlgorithmData(int)} returns null then the result of 
     *    this method is undefined.<p>
     *
     * @param index The attribute index (zero-based).
     * @return The URI.
     */
    public String getAlgorithmURI(int index);
 
    /**
     * Return the index of the encoding algorithm.
     *
     * <p>If {@link #getAlgorithmData(int)} returns null then the result of 
     *    this method is undefined.<p>
     *
     * @param index The attribute index (zero-based).
     * @return The index
     * @see org.jvnet.fastinfoset.EncodingAlgorithmIndexes       
     */
    public int getAlgorithmIndex(int index);
    
    /**
     * Return the data of the encoding algorithm.
     *
     * <p>If the algorithm data corresponds to a built-in encoding algorithm
     *    then an Object corresponding to the Java primitive type is returned.</p>
     *
     * <p>If the algorithm data corresponds to an application-defined encoding 
     *    algorithm then an Object that is an instance of <code>byte[]</code>
     *    is returned if there is no EncodingAlgorithm registered for the 
     *    application-defined encoding algorithm URI. Otherwise, an Object produced 
     *    from the registeredEncodingAlgorithm is returned.</p>
     *
     * <p>If there no encoding algorithm data associated an attribute then 
     *    <code>null</code> is returned.<p>
     *
     * @param index The attribute index (zero-based).
     * @return The data
     */
    public Object getAlgorithmData(int index);    
}
