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

package org.jvnet.fastinfoset.sax;

import org.xml.sax.Attributes;


/**
 * Interface for a list of XML attributes that may contain encoding algorithm
 * data.
 * <p>
 * Implementations shall ensure that the {@link Attributes#getValue(int)} method
 * correctly returns a String object even if the attribute is represented
 * as algorithm data.
 * <p>
 * If an attribute has algorithm data then the {@link #getAlgorithmData} method
 * shall return a non <code>null</code> value.
 *
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
     * @return The URI. If the index is out of range then null is returned.
     */
    public String getAlgorithmURI(int index);
 
    /**
     * Return the index of the encoding algorithm.
     *
     * <p>If {@link #getAlgorithmData(int)} returns null then the result of 
     *    this method is undefined.<p>
     *
     * @param index The attribute index (zero-based).
     * @return The algorithm index. If index is out of range then -1 is returned.
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
     * @return The data. If the index is out of range then null is returned.
     */
    public Object getAlgorithmData(int index);    
    
    /**
     * Return the alphabet associated with the attribute value.
     *
     * @param index The attribute index (zero-based). 
     * @return The alphabet. 
     *         If the index is out of range then null is returned.
     *         If there is is no alphabet then null is returned.
     */
    public String getAlpababet(int index);
    
    /**
     * Return the whether the attribute value should be indexed or not.
     *
     * @param index The attribute index (zero-based). 
     * @return True if attribute value should be indexed, otherwise false.
     */
    public boolean getToIndex(int index);
}
