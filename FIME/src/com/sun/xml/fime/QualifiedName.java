/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.fime;

import javax.xml.namespace.QName;

public class QualifiedName {
    public final String prefix;
    public final String namespaceName;
    public final String localName;
    public final String qName;
    public final int index;
    public final int prefixIndex;
    public final int namespaceNameIndex;
    public final int localNameIndex;
    public int attributeId;
    public int attributeHash;
    private QName qNameObject;
    
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
    
    public QualifiedName(String prefix, String namespaceName, String localName, 
            boolean intern) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;

        if (this.prefix != null && this.prefix != "") {
            StringBuffer b = new StringBuffer(this.prefix);
            b.append(':');
            b.append(this.localName);
            this.qName = (intern) ? b.toString().intern() : b.toString();
        } else {
            this.qName = this.localName;
        }

        this.index = -1;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;        
        this.localNameIndex = -1;
    }    

    public QualifiedName(String prefix, String namespaceName, String localName, 
            int prefixIndex, int namespaceNameIndex, int localNameIndex, 
            char[] charBuffer, boolean intern) {
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
                this.qName = (intern) ? new String(charBuffer, 0, total).intern() 
                    : new String(charBuffer, 0, total);
            } else {
                StringBuffer b = new StringBuffer(this.prefix);
                b.append(':');
                b.append(this.localName);
                this.qName = (intern) ? b.toString().intern() : b.toString();
            }
        } else {
            this.qName = this.localName;
        }

        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
        this.index = -1;
    }    
    
    public QualifiedName(String prefix, String namespaceName, String localName, int index, boolean intern) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;

        if (this.prefix != null && this.prefix != "") {
            StringBuffer b = new StringBuffer(this.prefix);
            b.append(':');
            b.append(this.localName);
            this.qName = (intern) ? b.toString().intern() : b.toString();
        } else {
            this.qName = this.localName;
        }

        this.index = index;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;        
        this.localNameIndex = -1;
    }    
    
    public QualifiedName(String prefix, String namespaceName, String localName, int index, 
            int prefixIndex, int namespaceNameIndex, int localNameIndex, 
            boolean intern) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;

        if (this.prefix != null && this.prefix != "") {
            StringBuffer b = new StringBuffer(this.prefix);
            b.append(':');
            b.append(this.localName);
            this.qName = (intern) ? b.toString().intern() : b.toString();
        } else {
            this.qName = this.localName;
        }

        this.index = index;
        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
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
    
    public final QName getQName() {
        if (qNameObject == null) {
            qNameObject = new QName(namespaceName, localName, prefix);
        }
        
        return qNameObject;
    }
    
    public final void createAttributeValues(int size) {
        attributeId = localNameIndex | (namespaceNameIndex << 20);
        attributeHash = localNameIndex % size;
    }
}
