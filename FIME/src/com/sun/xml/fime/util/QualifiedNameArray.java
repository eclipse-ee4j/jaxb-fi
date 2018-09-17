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

import com.sun.xml.fime.QualifiedName;

public class QualifiedNameArray extends ValueArray {
    
    public QualifiedName[] _array;
    
    private QualifiedNameArray _readOnlyArray;
        
    public QualifiedNameArray(int initialCapacity, int maximumCapacity) {
        _array = new QualifiedName[initialCapacity];
        _maximumCapacity = maximumCapacity;
    }

    public QualifiedNameArray() {
        this(DEFAULT_CAPACITY, MAXIMUM_CAPACITY);
    }
    
    public final void clear() {
        for (int i = _readOnlyArraySize; i < _size; i++) {
            _array[i] = null;
        }
        _size = _readOnlyArraySize;
    }

    public final QualifiedName[] getArray() {
        return _array;
    }
    
    public final void setReadOnlyArray(ValueArray readOnlyArray, boolean clear) {
        if (!(readOnlyArray instanceof QualifiedNameArray)) {
            throw new IllegalArgumentException(MessageCenter.
                    getString("message.illegalClass", new Object[]{readOnlyArray}));
        }       
        
        setReadOnlyArray((QualifiedNameArray)readOnlyArray, clear);
    }

    public final void setReadOnlyArray(QualifiedNameArray readOnlyArray, boolean clear) {
        if (readOnlyArray != null) {
            _readOnlyArray = readOnlyArray;
            _readOnlyArraySize = readOnlyArray.getSize();
                        
            if (clear) {
                clear();
            }
            
            _array = getCompleteArray();
            _size = _readOnlyArraySize;
        }
    }

    public final QualifiedName[] getCompleteArray() {
        if (_readOnlyArray == null) {
            return _array;
        } else {
            final QualifiedName[] ra = _readOnlyArray.getCompleteArray();
            final QualifiedName[] a = new QualifiedName[_readOnlyArraySize + _array.length];
            System.arraycopy(ra, 0, a, 0, _readOnlyArraySize);
            return a;
        }
    }
 
    public final QualifiedName get(int i) {
        return _array[i];
    }
    
    public final void add(QualifiedName s) {
        if (_size == _array.length) {
            resize();
        }
            
       _array[_size++] = s;
    }

    protected final void resize() {
        if (_size == _maximumCapacity) {
            throw new RuntimeException(MessageCenter.getString("message.arrayMaxCapacity"));
        }

        int newSize = _size * 3 / 2 + 1;
        if (newSize > _maximumCapacity) {
            newSize = _maximumCapacity;
        }

        final QualifiedName[] newArray = new QualifiedName[newSize];
        System.arraycopy(_array, 0, newArray, 0, _size);
        _array = newArray;
    }

}
