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
package encoding;

import com.sun.xml.fastinfoset.EncodingConstants;
import com.sun.xml.fastinfoset.util.PrefixArray;
import java.util.Iterator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NamespacesScopeTest extends TestCase {
    
    PrefixArray _prefixArray = new PrefixArray();
    
    public NamespacesScopeTest() {
    }
    
    public void testDefaultPrefixes() throws Exception {
        String n = _prefixArray.getNamespaceFromPrefix("");
        assertEquals("", n);
        
        n = _prefixArray.getNamespaceFromPrefix(EncodingConstants.XML_NAMESPACE_PREFIX);
        assertEquals(EncodingConstants.XML_NAMESPACE_NAME, n);
    }
    
    public void testOnePrefix() throws Exception {
        _prefixArray.add("p");
        
        _prefixArray.pushScopeWithPrefixEntry("p", "n", 1, 1);
        String n = _prefixArray.getNamespaceFromPrefix("p");
        assertEquals("n", n);
        _prefixArray.popScopeWithPrefixEntry(1);
        
        n = _prefixArray.getNamespaceFromPrefix("p");
        assertEquals(null, n);
    }
    
    public void testPrefixWithTwoNamespaces() throws Exception {
        _prefixArray.add("p");
        
        _prefixArray.pushScopeWithPrefixEntry("p", "n1", 1, 1);
        _prefixArray.pushScopeWithPrefixEntry("p", "n2", 2, 2);
        
        String n = _prefixArray.getNamespaceFromPrefix("p");
        assertEquals("n2", n);
        
        _prefixArray.popScopeWithPrefixEntry(2);
        
        n = _prefixArray.getNamespaceFromPrefix("p");
        assertEquals("n1", n);
        
        _prefixArray.popScopeWithPrefixEntry(1);
        
        n = _prefixArray.getNamespaceFromPrefix("p");
        assertEquals(null, n);
    }
    
    public void testTenDistinctPrefixes() throws Exception {
        _testNDistinctPrefixes(10);
        _testNDistinctPrefixes(10);
    }
    
    public void testOneThousandDistinctPrefixes() throws Exception {
        _testNDistinctPrefixes(1000);
    }
    
    public void testTenDistinctPrefixesWithClear() throws Exception {
        _testNDistinctPrefixesWithClear(10);
        _testNDistinctPrefixes(10);
    }
    
    public void _testNDistinctPrefixes(int prefixes) throws Exception {
        for (int i = 1; i <= prefixes; i++) {
            _prefixArray.add("p" + i);
            _prefixArray.pushScopeWithPrefixEntry("p" + i, "n" + i, i, i);
            
            String n = _prefixArray.getNamespaceFromPrefix("p" + i);
            assertEquals("n" + i, n);
            
        }
        
        for (int i = 1; i <= prefixes; i++) {
            String n = _prefixArray.getNamespaceFromPrefix("p" + i);
            assertEquals("n" + i, n);
        }
        
        for (int i = prefixes; i > 0; i--) {
            _prefixArray.popScopeWithPrefixEntry(i);
        }
        
        for (int i = 1; i <= prefixes; i++) {
            String n = _prefixArray.getNamespaceFromPrefix("p" + i);
            assertEquals(null, n);
        }
    }
    
    public void _testNDistinctPrefixesWithClear(int prefixes) throws Exception {
        for (int i = 1; i <= prefixes; i++) {
            _prefixArray.add("p" + i);
            _prefixArray.pushScopeWithPrefixEntry("p" + i, "n" + i, i, i);
        }
        
        _prefixArray.clearCompletely();
    }
    
    public void testGetPrefixes() throws Exception {
        for (int i = 1; i <= 10; i++) {
            _prefixArray.add("p" + i);
            _prefixArray.pushScopeWithPrefixEntry("p" + i, "n" + i, i, i);
        }
        
        int i = 0;
        Iterator p = _prefixArray.getPrefixes();
        while (p.hasNext()) {
            String prefix = (String)p.next();
            if (i == 0) {
                assertEquals("xml", prefix);
            } else {
                assertEquals("p" + i, prefix);
            }
            i++;
        }
    }
    
    public void testGetPrefixesFromNamespace() throws Exception {
        for (int i = 1; i <= 10; i++) {
            _prefixArray.add("p" + i);
            _prefixArray.pushScopeWithPrefixEntry("p" + i, "n", i, 1);
        }
        
        int i = 1;
        Iterator p = _prefixArray.getPrefixesFromNamespace("n");
        while (p.hasNext()) {
            String prefix = (String)p.next();
            assertEquals("p" + i, prefix);
            i++;
        }
    }
    
    private void add(String prefix, String namespaceName, int prefixIndex, int namespaceNameIndex) throws Exception {
        _prefixArray.add(prefix);
        _prefixArray.pushScopeWithPrefixEntry(prefix, namespaceName, prefixIndex, namespaceNameIndex);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(NamespacesScopeTest.class);
        return suite;
    }
    
}