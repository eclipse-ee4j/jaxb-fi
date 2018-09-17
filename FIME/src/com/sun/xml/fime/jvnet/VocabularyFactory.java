/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.fime.jvnet;

public abstract class VocabularyFactory {
    
    protected VocabularyFactory() {
    }
    
    public static VocabularyFactory newInstance() {
        // return implementation based on system property
        return null;
    }
    
    public abstract Vocabulary newVocabulary();    
}
