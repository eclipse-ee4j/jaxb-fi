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
package sax;

import com.sun.xml.fastinfoset.sax.Features;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import junit.framework.TestCase;
import org.xml.sax.SAXNotSupportedException;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class FeaturesTest extends TestCase {
    
    public void testNamespaceFeatureTrue() throws Exception {
        SAXDocumentParser sp = new SAXDocumentParser();
        
        boolean exceptionThrown = false;
        try {
            sp.setFeature(Features.NAMESPACES_FEATURE, true);
        } catch (SAXNotSupportedException e) {
            exceptionThrown = true;
        }
        
        assertFalse(exceptionThrown);
    }
    
    public void testNamespaceFeatureFalse() throws Exception {
        SAXDocumentParser sp = new SAXDocumentParser();
        
        boolean exceptionThrown = false;
        try {
            sp.setFeature(Features.NAMESPACES_FEATURE, false);
        } catch (SAXNotSupportedException e) {
            exceptionThrown = true;
        }
        
        assertTrue(exceptionThrown);
    }
    
    public void testStringInternFeature() throws Exception {
        SAXDocumentParser sp = new SAXDocumentParser();
        
        boolean stringIntern = false;
        sp.setFeature(Features.STRING_INTERNING_FEATURE, stringIntern);        
        boolean v = sp.getFeature(Features.STRING_INTERNING_FEATURE);
        assertEquals(stringIntern, v);
        
        stringIntern = true;
        sp.setFeature(Features.STRING_INTERNING_FEATURE, stringIntern);        
        v = sp.getFeature(Features.STRING_INTERNING_FEATURE);
        assertEquals(stringIntern, v);
    }
    
    public void testNamespacePrefixesFeature() throws Exception {
        SAXDocumentParser sp = new SAXDocumentParser();
        
        boolean namespacePrefixes = false;
        sp.setFeature(Features.NAMESPACE_PREFIXES_FEATURE, namespacePrefixes);        
        boolean v = sp.getFeature(Features.NAMESPACE_PREFIXES_FEATURE);
        assertEquals(namespacePrefixes, v);
        
        namespacePrefixes = true;
        sp.setFeature(Features.NAMESPACE_PREFIXES_FEATURE, namespacePrefixes);        
        v = sp.getFeature(Features.NAMESPACE_PREFIXES_FEATURE);
        assertEquals(namespacePrefixes, v);
    }
}
