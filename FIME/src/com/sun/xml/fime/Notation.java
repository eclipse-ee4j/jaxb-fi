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

public class Notation {
    public final String name;
    public final String systemIdentifier;
    public final String publicIdentifier;
    
    /** Creates a new instance of Notation */
    public Notation(String _name, String _systemIdentifier, String _publicIdentifier) {
        name = _name;
        systemIdentifier = _systemIdentifier;
        publicIdentifier = _publicIdentifier;
    }
    
}
