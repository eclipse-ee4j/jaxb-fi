/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.fime.util;

public abstract class ValueArray {
    public static final int DEFAULT_CAPACITY = 10;
    public static final int MAXIMUM_CAPACITY = Integer.MAX_VALUE;

    protected int _size;
    
    protected int _readOnlyArraySize;  
    
    protected int _maximumCapacity;
            
    public int getSize() {
        return _size;
    }

    public int getMaximumCapacity() {
        return _maximumCapacity;
    }
    
    public void setMaximumCapacity(int maximumCapacity) {
        _maximumCapacity = maximumCapacity;
    }
    
    public abstract void setReadOnlyArray(ValueArray array, boolean clear);
    
    public abstract void clear();
}
