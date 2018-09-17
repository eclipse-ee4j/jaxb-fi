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
package com.sun.xml.fastinfoset.vocab.frequency;

import com.sun.xml.analysis.frequency.FrequencyHandler;
import com.sun.xml.analysis.frequency.FrequencyBasedLists;
import com.sun.xml.analysis.frequency.SchemaProcessor;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.tools.PrintTable;
import com.sun.xml.fastinfoset.util.CharArrayIntMap;
import com.sun.xml.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.fastinfoset.util.DuplicateAttributeVerifier;
import com.sun.xml.fastinfoset.util.KeyIntMap;
import com.sun.xml.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.fastinfoset.util.PrefixArray;
import com.sun.xml.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.fastinfoset.util.StringArray;
import com.sun.xml.fastinfoset.util.StringIntMap;
import com.sun.xml.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import java.io.File;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Generate a Fast Infoset parser and serializer vocabulary from a
 * {@link FrequencyBasedLists}.
 *
 * @deprecated
 * @author Paul.Sandoz@Sun.Com
 */
public class VocabularyGenerator {
    /**
     * Constants for the various XML-based APIs
     */
    public static enum XmlApi {
        SAX, StAX, DOM;
    };
    
    private XmlApi _xapi;
    
    private SerializerVocabulary _serializerVocabulary;
    
    private ParserVocabulary _parserVocabulary;
    
    /**
     * @param fbl the set of frequency-based lists.
     * @param xapi the XML API that the parser and serializer vocabulary
     * will be used with.
     */
    public VocabularyGenerator(FrequencyBasedLists fbl, XmlApi xapi) {
        this(new SerializerVocabulary(), new ParserVocabulary(), fbl, xapi);
    }
    
    /**
     * @param serializerVocabulary the serializer vocabualry to use
     * @param parserVocabulary the parser vocabulary to use
     * @param fbl the set of frequency-based lists.
     * @param xapi the XML API that the parser and serializer vocabulary
     * will be used with.
     */
    public VocabularyGenerator(SerializerVocabulary serializerVocabulary, ParserVocabulary parserVocabulary,
            FrequencyBasedLists fbl, XmlApi xapi) {
        _serializerVocabulary = serializerVocabulary;
        _parserVocabulary = parserVocabulary;
        _xapi = xapi;
        
        generate(fbl);
    }
    
    /**
     * Get the generated serializer vocabulary.
     *
     * @return the serializer vocabulary.
     */
    public SerializerVocabulary getSerializerVocabulary() {
        return _serializerVocabulary;
    }
    
    /**
     * Get the generated parser vocabulary.
     *
     * @return the parser vocabulary.
     */
    public ParserVocabulary getParserVocabulary() {
        return _parserVocabulary;
    }
    
    private void generate(FrequencyBasedLists fbl) {
        for (String prefix : fbl.prefixes) {
            addToTable(prefix, _serializerVocabulary.prefix,
                    _parserVocabulary.prefix);
        }
        
        for (String namespace : fbl.namespaces) {
            addToTable(namespace, _serializerVocabulary.namespaceName,
                    _parserVocabulary.namespaceName);
        }
        
        for (String localName : fbl.localNames) {
            addToTable(localName, _serializerVocabulary.localName, _parserVocabulary.localName);
        }
        
        for (QName element : fbl.elements) {
            addToNameTable(element, _serializerVocabulary.elementName, _parserVocabulary.elementName, false);
        }
        
        for (QName attribute : fbl.attributes) {
            addToNameTable(attribute, _serializerVocabulary.attributeName, _parserVocabulary.attributeName, true);
        }
        
        for (String textContentValue : fbl.textContentValues) {
            addToTable(textContentValue, _serializerVocabulary.characterContentChunk,
                    _parserVocabulary.characterContentChunk);
        }
        
        for (String attributeValue : fbl.attributeValues) {
            addToTable(attributeValue, _serializerVocabulary.attributeValue,
                    _parserVocabulary.attributeValue);
        }
    }
    
    private void addToNameTable(QName n,
            LocalNameQualifiedNamesMap m, QualifiedNameArray a,
            boolean isAttribute) {
        
        int namespaceURIIndex = -1;
        int prefixIndex = -1;
        if (n.getNamespaceURI().length() > 0) {
            namespaceURIIndex = _serializerVocabulary.namespaceName.obtainIndex(
                    n.getNamespaceURI());
            if (namespaceURIIndex == KeyIntMap.NOT_PRESENT) {
                namespaceURIIndex = _serializerVocabulary.namespaceName.get(
                        n.getNamespaceURI());
                _parserVocabulary.namespaceName.add(n.getNamespaceURI());
            }
            
            if (n.getPrefix().length() > 0) {
                prefixIndex = _serializerVocabulary.prefix.obtainIndex(
                        n.getPrefix());
                if (prefixIndex == KeyIntMap.NOT_PRESENT) {
                    prefixIndex = _serializerVocabulary.prefix.get(
                            n.getPrefix());
                    _parserVocabulary.prefix.add(n.getPrefix());
                }
            }
        }
        
        int localNameIndex = _serializerVocabulary.localName.obtainIndex(
                n.getLocalPart());
        if (localNameIndex == KeyIntMap.NOT_PRESENT) {
            localNameIndex = _serializerVocabulary.localName.get(
                    n.getLocalPart());
            _parserVocabulary.localName.add(n.getLocalPart());
        }
        
        QualifiedName name = new QualifiedName(
                n.getPrefix(), n.getNamespaceURI(), n.getLocalPart(),
                m.getNextIndex(),
                prefixIndex, namespaceURIIndex, localNameIndex);
        if (isAttribute) {
            name.createAttributeValues(DuplicateAttributeVerifier.MAP_SIZE);
        }
        
        LocalNameQualifiedNamesMap.Entry entry = null;
        if (_xapi == XmlApi.StAX) {
            entry = m.obtainEntry(n.getLocalPart());
        } else {
            String qName = (prefixIndex == -1)
            ? n.getLocalPart()
            : n.getPrefix() + ":" + n.getLocalPart();
            entry = m.obtainEntry(qName);
        }
        
        entry.addQualifiedName(name);
        a.add(name);
    }
    
    private void addToTable(String s, StringIntMap m, StringArray a) {
        if (s.length() == 0) {
            return;
        }
        
        m.obtainIndex(s);
        a.add(s);
    }
    
    private void addToTable(String s, StringIntMap m, PrefixArray a) {
        if (s.length() == 0) {
            return;
        }
        
        m.obtainIndex(s);
        a.add(s);
    }
    
    private void addToTable(String s, CharArrayIntMap m, ContiguousCharArrayArray a) {
        if (s.length() == 0) {
            return;
        }
        
        char[] c = s.toCharArray();
        m.obtainIndex(c, 0, c.length, false);
        a.add(c, c.length);
    }
    
    /**
     * @param args the command line arguments. arg[0] is the path to a schema,
     * args[1] to args[n] are the paths to XML documents.
     */
    public static void main(String[] args) throws Exception {
        SchemaProcessor sp = new SchemaProcessor(new File(args[0]).toURL(), true, false);
        sp.process();
        
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser p = spf.newSAXParser();
        
        FrequencyHandler fh = new FrequencyHandler(sp);
        for (int i = 1; i < args.length; i++) {
            p.parse(new File(args[i]), fh);
        }
        
        VocabularyGenerator vg = new VocabularyGenerator(fh.getLists(), VocabularyGenerator.XmlApi.SAX);
        PrintTable.printVocabulary(vg.getParserVocabulary());
                
        org.jvnet.fastinfoset.Vocabulary v = fh.getVocabulary();
        ParserVocabulary pv = new ParserVocabulary(v);
        System.out.println("");
        PrintTable.printVocabulary(pv);
    }
}