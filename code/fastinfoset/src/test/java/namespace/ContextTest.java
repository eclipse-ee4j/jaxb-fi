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
package namespace;

import com.sun.xml.fastinfoset.util.NamespaceContextImplementation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class ContextTest extends TestCase {
    NamespaceContextImplementation nc = new NamespaceContextImplementation();
    
    public void testDefaults() throws Exception {
        String n;
        n = nc.getNamespaceURI("xml");
        assertEquals("http://www.w3.org/XML/1998/namespace", n);
        n = nc.getNamespaceURI("xmlns");
        assertEquals("http://www.w3.org/2000/xmlns/", n);
        
        String p;
        p = nc.getPrefix("http://www.w3.org/XML/1998/namespace");
        assertEquals("xml", p);
        p = nc.getPrefix("http://www.w3.org/2000/xmlns/");
        assertEquals("xmlns", p);
    }
    
    public void testDefaultsWithAdditions() throws Exception {
        nc.pushContext();
        nc.declarePrefix("a", "http://a");
        nc.declarePrefix("b", "http://b");
        
        testDefaults();
    }
    
    public void testSimpleDeclaration() throws Exception {
        nc.pushContext();
        nc.declarePrefix("a", "http://a");
        nc.declarePrefix("b", "http://b");
        
        String n;
        n = nc.getNamespaceURI("a");
        assertEquals("http://a", n);
        n = nc.getNamespaceURI("b");
        assertEquals("http://b", n);
        
        String p;
        p = nc.getPrefix("http://a");
        assertEquals("a", p);
        p = nc.getPrefix("http://b");
        assertEquals("b", p);
    }
    
    public void testDuplicatePrefixes() throws Exception {
        nc.pushContext();
        nc.declarePrefix("a", "http://a");
        nc.declarePrefix("a", "http://new");
        
        String n;
        n = nc.getNamespaceURI("a");
        assertEquals("http://new", n);
    }
    
    public void testMultipleDeclarations() throws Exception {
        for (int c = 0; c < 100; c++) {
            nc.pushContext();
            for (int i = 0; i < 10; i++) {
                String p = "" + c + "_" + i;
                String n = "http://" + p;
                nc.declarePrefix(p, n);
            }
        }
        
        for (int c = 0; c < 100; c++) {
            for (int i = 0; i < 10; i++) {
                String p = "" + c + "_" + i;
                String n = "http://" + p;
                assertEquals(p, nc.getPrefix(n));
                assertEquals(n, nc.getNamespaceURI(p));
            }
        }
        
        for (int c = 99; c >= 0; c--) {        
            nc.popContext();
            for (int i = 0; i < 10; i++) {
                String p = "" + c + "_" + i;
                String n = "http://" + p;
                assertEquals(null, nc.getPrefix(n));
                assertEquals("", nc.getNamespaceURI(p));
            }
        }        
    }
    
    public void testScope() throws Exception {
        nc.pushContext();
        nc.declarePrefix("a", "http://a");
        
        nc.pushContext();
        nc.declarePrefix("a", "http://new");
        
        String n;
        n = nc.getNamespaceURI("a");
        assertEquals("http://new", n);

        String p;
        p = nc.getPrefix("http://a");
        assertEquals(null, p);
        p = nc.getPrefix("http://new");
        assertEquals("a", p);
        
        nc.popContext();
        n = nc.getNamespaceURI("a");
        assertEquals("http://a", n);
        
    }
    
    public void testMultipleNamespaces() throws Exception {
        nc.pushContext();
        nc.declarePrefix("a", "http://a");        
        nc.declarePrefix("x", "http://a");        
        nc.pushContext();
        nc.declarePrefix("b", "http://a");
        nc.declarePrefix("y", "http://a");        
        nc.pushContext();
        nc.declarePrefix("c", "http://a");
        nc.declarePrefix("x", "http://x");        
        nc.declarePrefix("y", "http://y");        
        
        Set prefixes = new HashSet();
        prefixes.add("a");
        prefixes.add("b");
        prefixes.add("c");
        
        Iterator i = nc.getPrefixes("http://a");
        while (i.hasNext()) {
            Object p = i.next();
            assertTrue(prefixes.contains(p));
            prefixes.remove(p);
        }
        
        assertEquals(0, prefixes.size());
    }
}
