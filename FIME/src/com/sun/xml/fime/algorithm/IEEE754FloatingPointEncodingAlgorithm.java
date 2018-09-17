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

public abstract class IEEE754FloatingPointEncodingAlgorithm extends BuiltInEncodingAlgorithm {
    public final static int FLOAT_SIZE  = 4;
    public final static int DOUBLE_SIZE = 8;

    public final static int FLOAT_MAX_CHARACTER_SIZE    = 14;
    public final static int DOUBLE_MAX_CHARACTER_SIZE   = 24;
        
}
