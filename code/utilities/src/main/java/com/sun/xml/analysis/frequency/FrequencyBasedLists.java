/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2011 Oracle and/or its affiliates. All rights reserved.
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
package com.sun.xml.analysis.frequency;

import java.util.List;
import javax.xml.namespace.QName;

/**
 * A container for frequency based lists of values in the order of decreasing
 * frequency.
 * @deprecated
 * @author Paul.Sandoz@Sun.Com
 */
public class FrequencyBasedLists {
    /**
     * List of prefixes in the order of decreasing frequency
     */
    public final List<String> prefixes;
    /**
     * List of namespaces in the order of decreasing frequency
     */
    public final List<String> namespaces;
    /**
     * List of local names in the order of decreasing frequency
     */
    public final List<String> localNames;
    /**
     * List of elements in the order of decreasing frequency
     */
    public final List<QName> elements;
    /**
     * List of attributes in the order of decreasing frequency
     */
    public final List<QName> attributes;
    /**
     * List of text content values in the order of decreasing frequency
     */
    public final List<String> textContentValues;
    /**
     * List of attribute values in the order of decreasing frequency
     */
    public final List<String> attributeValues;
    
    
    FrequencyBasedLists(List<String> p, List<String> n, List<String> l, 
            List<QName> e, List<QName> a,
            List<String> tcv, List<String> av) {
        prefixes = p;
        namespaces = n;
        localNames = l;
        elements = e;
        attributes = a;
        textContentValues = tcv;
        attributeValues = av;
    }
}
