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


public class CharArrayArray extends ValueArray {
    
    private CharArray[] _array;
    
    private CharArrayArray _readOnlyArray;
    
    public CharArrayArray(int initialCapacity, int maximumCapacity) {
        _array = new CharArray[initialCapacity];
        _maximumCapacity = maximumCapacity;
    }

    public CharArrayArray() {
        this(DEFAULT_CAPACITY, MAXIMUM_CAPACITY);
    }
    
    public final void clear() {
        for (int i = 0; i < _size; i++) {
            _array[i] = null;
        }
        _size = 0;
    }

    public final CharArray[] getArray() {
        return _array;
    }
    
    public final void setReadOnlyArray(ValueArray readOnlyArray, boolean clear) {
        if (!(readOnlyArray instanceof CharArrayArray)) {
            throw new IllegalArgumentException(MessageCenter.getString("message.illegalClass", new Object[]{readOnlyArray}));
        }       
        
        setReadOnlyArray((CharArrayArray)readOnlyArray, clear);
    }

    public final void setReadOnlyArray(CharArrayArray readOnlyArray, boolean clear) {
        if (readOnlyArray != null) {
            _readOnlyArray = readOnlyArray;
            _readOnlyArraySize = readOnlyArray.getSize();
                        
            if (clear) {
                clear();
            }
        }
    }
    
    public final CharArray get(int i) {
        if (_readOnlyArray == null) {
            return _array[i];
        } else {
            if (i < _readOnlyArraySize) {
               return _readOnlyArray.get(i); 
            } else {
                return _array[i - _readOnlyArraySize];
            }
        }
   }
    
    public final void add(CharArray s) {
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

        final CharArray[] newArray = new CharArray[newSize];
        System.arraycopy(_array, 0, newArray, 0, _size);
        _array = newArray;
    }
}
