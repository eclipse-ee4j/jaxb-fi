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
package com.sun.xml.analysis.frequency.tools;

import com.sun.xml.analysis.frequency.*;
import java.io.File;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * A simple class that prints out in the order of descreasing frequency
 * information items declared in a schema and occuring 0 or more times in 
 * a set of infosets.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class PrintFrequency {
    
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
        fh.generateQNamesWithPrefix();
        
        FrequencyBasedLists l = fh.getLists();
        System.out.println("Prefixes");
        for (String s : l.prefixes) {
            System.out.println(s);
        }
        
        System.out.println("Local names: " + l.localNames.size());
        for (String s : l.localNames) {
            System.out.println(s);
        }
        
        int i = 0;
        System.out.println("Elements: " + l.elements.size());
        for (QName q : l.elements) {
            System.out.println(q.getPrefix() + " " + q + " " + (i++));
        }
        
        System.out.println("Attributes" + l.attributes.size());
        for (QName q : l.attributes) {
            System.out.println(q.getPrefix() + " " + q);
        }
    }
}
