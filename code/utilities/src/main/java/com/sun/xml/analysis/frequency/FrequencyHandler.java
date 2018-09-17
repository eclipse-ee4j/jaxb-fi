/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.jvnet.fastinfoset.Vocabulary;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX-based handler to collect the frequency of occurences of properties
 * of information items in one or more infosets.
 * 
 * @author Paul.Sandoz@Sun.Com
 */
public class FrequencyHandler extends DefaultHandler {
    private Map<String, Set<QName>> namespacesToElements = new HashMap();
    private Map<String, Set<QName>> namespacesToAttributes = new HashMap();
    
    private Map<String, String> namespaceURIToPrefix = new HashMap();
    
    private FrequencySet<String> prefixes = new FrequencySet<String>();
    private FrequencySet<String> namespaces = new FrequencySet<String>();
    private FrequencySet<String> localNames = new FrequencySet<String>();
    private FrequencySet<QName> elements = new FrequencySet<QName>();
    private FrequencySet<QName> attributes = new FrequencySet<QName>();
    private FrequencySet<String> attributeValues = new FrequencySet<String>();
    private FrequencySet<String> textContentValues = new FrequencySet<String>();

    /**
     * The default frequency handler.
     */
    public FrequencyHandler() {
    }

    /**
     * A frequency handler initiated with information generated from a
     * {@link SchemaProcessor}.
     *
     * @param sp the schema processor.
     */
    public FrequencyHandler(SchemaProcessor sp) {
        for (String s : sp.localNames) {
            localNames.add0(s);
        }
        for (String s : sp.namespaces) {
            namespaces.add0(s);
        }
        
        bucketQNamesToNamespace(sp.elements, namespacesToElements);
        bucketQNamesToNamespace(sp.attributes, namespacesToAttributes);
        
        for (String s : sp.textContentValues) {
            textContentValues.add0(s);
        }
        for (String s : sp.attributeValues) {
            attributeValues.add0(s);
        }
    }
    
    public void addXsiAttributes() {
        QName q = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
        addQNameToAttributes(q);
        
        q = new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
        addQNameToAttributes(q);
    }
    
    public void addQNameToElements(QName q) {
        // Add the namespace to the table
        namespaces.add0(q.getNamespaceURI());
        // Add the local name to the table
        localNames.add0(q.getLocalPart());
        
        // bucketQNameToNamespace(q, namespacesToElements);
    }
    
    public void addQNameToAttributes(QName q) {
        // Add the namespace to the table
        namespaces.add0(q.getNamespaceURI());
        // Add the local name to the table
        localNames.add0(q.getLocalPart());
        
        // bucketQNameToNamespace(q, namespacesToAttributes);
    }

    /**
     * Get the frequency based lists of properties of information items.
     *
     * @return the frequency based lists.
     * @deprecated
     */
    public FrequencyBasedLists getLists() {
        // TODO if elements/attributes are empty then
        // need to obtain prefixes for namespaces from some other
        // external source
        
        return  new FrequencyBasedLists(
            prefixes.createFrequencyBasedList(),
            namespaces.createFrequencyBasedList(),
            localNames.createFrequencyBasedList(),
            elements.createFrequencyBasedList(),
            attributes.createFrequencyBasedList(),
            textContentValues.createFrequencyBasedList(),
            attributeValues.createFrequencyBasedList());
    }
    
    /**
     * Get the vocabulary
     *
     * @return the vocabulary.
     */
    public Vocabulary getVocabulary() {
        // TODO if elements/attributes are empty then
        // need to obtain prefixes for namespaces from some other
        // external source

        Vocabulary v = new Vocabulary();
        addAll(v.prefixes, prefixes.createFrequencyBasedSet());
        addAll(v.namespaceNames, namespaces.createFrequencyBasedSet());
        addAll(v.localNames, localNames.createFrequencyBasedSet());
        addAll(v.elements, elements.createFrequencyBasedSet());
        addAll(v.attributes, attributes.createFrequencyBasedSet());
        addAll(v.characterContentChunks, textContentValues.createFrequencyBasedSet());
        addAll(v.attributeValues, attributeValues.createFrequencyBasedSet());
        
        return v;
    }
    
    /**
     * Generate qualified names that have not been processed.
     * <p>
     * Prefixes are automatically chosen.
     * <p>
     * TODO: check for clashes with prefixes generated and prefixes
     * that have already occured.
     */
    public void generateQNamesWithPrefix() {
        Set<String> namespaceURIs = new HashSet<String>();
        namespaceURIs.addAll(namespacesToElements.keySet());
        namespaceURIs.addAll(namespacesToAttributes.keySet());
        
        // Generate prefixes for namespace URIs
        for (String namespaceURI : namespaceURIs) {
            if (namespaceURI.length() == 0) {
                namespaceURIToPrefix.put(namespaceURI, "");
            } else {
                String prefix = getNewPrefix();
                namespaceURIToPrefix.put(namespaceURI, prefix);
                
                // Add the prefix to the table
                prefixes.add0(prefix);
            }
        }
        
        // Add qNames for elements
        generateQNamesWithPrefix(namespacesToElements, elements, namespaceURIToPrefix);
        // Add qNames for attributes
        generateQNamesWithPrefix(namespacesToAttributes, attributes, namespaceURIToPrefix);
    }
     
    public Map<String, String> getNamespaceURIToPrefixMap() {
        return namespaceURIToPrefix;
    }
    
    private void addAll(Set to, Set<?> from) {
        for(Object o : from) {
            to.add(o);
        }
    }
    
    private void bucketQNamesToNamespace(Set<QName> s, Map<String, Set<QName>> m) {
        for (QName q : s) {
            Set<QName> subs = m.get(q.getNamespaceURI());
            if (subs == null) {
                subs = new HashSet();
                m.put(q.getNamespaceURI(), subs);
            }
            subs.add(q);
        }
    }
    
//    private void bucketQNameToNamespace(QName q, Map<String, Set<QName>> m) {
//        Set<QName> subs = m.get(q.getNamespaceURI());
//        if (subs == null) {
//            subs = new HashSet();
//            m.put(q.getNamespaceURI(), subs);
//        }
//        subs.add(q);        
//    }
    
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        prefixes.add(prefix);
        namespaces.add(uri);
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        QName q = createQName(uri, localName, qName);
        addQName(q, namespacesToElements, elements);
        
        for (int i = 0; i < atts.getLength(); i++) {
            q = createQName(atts.getURI(i), atts.getLocalName(i), atts.getQName(i));
            addQName(q, namespacesToAttributes, attributes);
        }
    }
    
    private void addQName(QName q, Map<String, Set<QName>> m, FrequencySet<QName> fhm) {
        Set<QName> s = m.get(q.getNamespaceURI());
        if (s == null) {
            fhm.add(q);
        } else {
            // Remove the set of qNames for the namespace
            m.put(q.getNamespaceURI(), null);
            
            String prefix = q.getPrefix();
            if (prefix.length() == 0) {
                // If 'q' is in the set then remove it
                if (s.contains(q)) {
                    s.remove(q);
                }
                fhm.add(q);
                
                // Add each qName in the set to the frequence map
                for (QName qInSet : s) {
                    fhm.add0(qInSet);
                }
            } else {
                // Remove the prefix from the qName
                QName qWithoutPrefix = new QName(q.getNamespaceURI(), q.getLocalPart());
                
                // If 'qWithoutPrefix' is not in the set then 
                // add 'q' to the frequency map
                if (s.contains(qWithoutPrefix)) {
                    s.remove(qWithoutPrefix);
                }
                fhm.add(q);
                
                // Add each qName in the set to the frequence map
                // include the prefix of 'q'
                for (QName qInSet : s) {
                    fhm.add0(new QName(qInSet.getNamespaceURI(), qInSet.getLocalPart(), prefix));
                }
            }
        }
    }
    
    private QName createQName(String uri, String localName, String qName) {
        if (uri != "") {
            final int i = qName.indexOf(':');
            final String prefix = (i != -1) ? qName.substring(0, i) : "";
            return new QName(uri, localName, prefix);
        } else {
            return new QName(localName);
        }
    }
    
    private void generateQNamesWithPrefix(Map<String, Set<QName>> m, FrequencySet<QName> fhm, 
            Map<String, String> namespaceURIToPrefix) {
        for (Map.Entry<String, Set<QName>> entry : m.entrySet()) {
            String namespaceURI = entry.getKey();
            String prefix = namespaceURIToPrefix.get(namespaceURI);
            for (QName qInSet : entry.getValue()) {
                fhm.add0(new QName(namespaceURI, qInSet.getLocalPart(), prefix));
            }
        }
    }
    
    private StringBuilder prefixBuilder;
    private char prefixCharacter = 'a';
    
    private String getNewPrefix() {
        if (prefixBuilder == null) {
            prefixBuilder = new StringBuilder();
            prefixBuilder.append(prefixCharacter);
            return prefixBuilder.toString();
        }
        
        prefixCharacter++;
        if (prefixCharacter > 'z') {
            prefixCharacter = 'a';
            prefixBuilder.append(prefixCharacter);
        } else {
            prefixBuilder.setCharAt(prefixBuilder.length() - 1, prefixCharacter);
        }
        
        return prefixBuilder.toString();
    }
}
