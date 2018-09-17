/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.fime.stax;

import com.sun.xml.fime.QualifiedName;
import com.sun.xml.fime.util.QualifiedNameArray;


public class ElementHolder {
    public final QualifiedName qualifiedName; 
    public final QualifiedNameArray namespaces;

    public ElementHolder(QualifiedName qualifiedName, QualifiedNameArray namespaces) 
    {
        this.qualifiedName = qualifiedName;
        this.namespaces = namespaces;
    }
}
