/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.fime.algorithm;

public abstract class IntegerEncodingAlgorithm extends BuiltInEncodingAlgorithm {
    public final static int SHORT_SIZE  = 2;
    public final static int INT_SIZE    = 4;
    public final static int LONG_SIZE   = 8;

    public final static int SHORT_MAX_CHARACTER_SIZE    = 6;
    public final static int INT_MAX_CHARACTER_SIZE      = 11;
    public final static int LONG_MAX_CHARACTER_SIZE     = 20;
        
}
