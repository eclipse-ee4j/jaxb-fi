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

package com.sun.xml.fastinfoset;

import javax.xml.namespace.QName;

public class QualifiedName {
    public String prefix;
    public String namespaceName;
    public String localName;
    public String qName;
    public int index;
    public int prefixIndex;
    public int namespaceNameIndex;
    public int localNameIndex;
    public int attributeId;
    public int attributeHash;
    private QName qNameObject;
    
    public QualifiedName() { }
    
    public QualifiedName(String prefix, String namespaceName, String localName, String qName) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = qName;
        this.index = -1;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;        
        this.localNameIndex = -1;
    }    

    public void set(String prefix, String namespaceName, String localName, String qName) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = qName;
        this.index = -1;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;        
        this.localNameIndex = -1;
        this.qNameObject = null;
    }    
    
    public QualifiedName(String prefix, String namespaceName, String localName, String qName, int index) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = qName;
        this.index = index;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;        
        this.localNameIndex = -1;
    }    
    
    public final QualifiedName set(String prefix, String namespaceName, String localName, String qName, int index) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = qName;
        this.index = index;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;        
        this.localNameIndex = -1;
        this.qNameObject = null;
        return this;
    }    
    
    public QualifiedName(String prefix, String namespaceName, String localName, String qName, int index, 
            int prefixIndex, int namespaceNameIndex, int localNameIndex) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = qName;
        this.index = index;
        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
    }    
    
    public final QualifiedName set(String prefix, String namespaceName, String localName, String qName, int index, 
            int prefixIndex, int namespaceNameIndex, int localNameIndex) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = qName;
        this.index = index;
        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
        this.qNameObject = null;
        return this;
    }    
    
    public QualifiedName(String prefix, String namespaceName, String localName) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = createQNameString(prefix, localName);
        this.index = -1;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;        
        this.localNameIndex = -1;
    }    

    public final QualifiedName set(String prefix, String namespaceName, String localName) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = createQNameString(prefix, localName);
        this.index = -1;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;        
        this.localNameIndex = -1;
        this.qNameObject = null;
        return this;
    }    
    
    public QualifiedName(String prefix, String namespaceName, String localName, 
            int prefixIndex, int namespaceNameIndex, int localNameIndex, 
            char[] charBuffer) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;

        if (charBuffer != null) {
            final int l1 = prefix.length();
            final int l2 = localName.length();
            final int total = l1 + l2 + 1;
            if (total < charBuffer.length) {
                prefix.getChars(0, l1, charBuffer, 0);
                charBuffer[l1] = ':';
                localName.getChars(0, l2, charBuffer, l1 + 1);
                this.qName = new String(charBuffer, 0, total);
            } else {
                this.qName = createQNameString(prefix, localName);
            }
        } else {
            this.qName = this.localName;
        }

        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
        this.index = -1;
    }    
    
    public final QualifiedName set(String prefix, String namespaceName, String localName, 
            int prefixIndex, int namespaceNameIndex, int localNameIndex, 
            char[] charBuffer) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;

        if (charBuffer != null) {
            final int l1 = prefix.length();
            final int l2 = localName.length();
            final int total = l1 + l2 + 1;
            if (total < charBuffer.length) {
                prefix.getChars(0, l1, charBuffer, 0);
                charBuffer[l1] = ':';
                localName.getChars(0, l2, charBuffer, l1 + 1);
                this.qName = new String(charBuffer, 0, total);
            } else {
                this.qName = createQNameString(prefix, localName);
            }
        } else {
            this.qName = this.localName;
        }

        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
        this.index = -1;
        this.qNameObject = null;
        return this;
    }    
    
    public QualifiedName(String prefix, String namespaceName, String localName, int index) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = createQNameString(prefix, localName);
        this.index = index;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;        
        this.localNameIndex = -1;
    }    
    
    public final QualifiedName set(String prefix, String namespaceName, String localName, int index) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = createQNameString(prefix, localName);
        this.index = index;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;        
        this.localNameIndex = -1;
        this.qNameObject = null;
        return this;
    }    
    
    public QualifiedName(String prefix, String namespaceName, String localName, int index, 
            int prefixIndex, int namespaceNameIndex, int localNameIndex) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = createQNameString(prefix, localName);
        this.index = index;
        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
    }    
    
    public final QualifiedName set(String prefix, String namespaceName, String localName, int index, 
            int prefixIndex, int namespaceNameIndex, int localNameIndex) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = createQNameString(prefix, localName);
        this.index = index;
        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
        this.qNameObject = null;
        return this;
    }    
    
    // Qualified Name as a Namespace Name
    public QualifiedName(String prefix, String namespaceName) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = "";
        this.qName = "";
        this.index = -1;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;       
        this.localNameIndex = -1;
    }
    
    public final QualifiedName set(String prefix, String namespaceName) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = "";
        this.qName = "";
        this.index = -1;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;       
        this.localNameIndex = -1;
        this.qNameObject = null;
        return this;
    }
    
    public final QName getQName() {
        if (qNameObject == null) {
            qNameObject = new QName(namespaceName, localName, prefix);
        }
        
        return qNameObject;
    }
    
    public final String getQNameString() {
        if (this.qName != "") {
            return this.qName;
        }
        
        return this.qName = createQNameString(prefix, localName);
    }
    
    public final void createAttributeValues(int size) {
        attributeId = localNameIndex | (namespaceNameIndex << 20);
        attributeHash = localNameIndex % size;
    }
    
    private final String createQNameString(String p, String l) {
        if (p != null && p.length() > 0) {
            final StringBuffer b = new StringBuffer(p);
            b.append(':');
            b.append(l);
            return b.toString();
        } else {
            return l;
        }
    }
}
