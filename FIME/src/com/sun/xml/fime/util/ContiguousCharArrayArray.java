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

public class ContiguousCharArrayArray extends ValueArray {
    public static final int INITIAL_CHARACTER_SIZE = 512;
    public static final int MAXIMUM_CHARACTER_SIZE = Integer.MAX_VALUE;
    
    protected int _maximumCharacterSize;
    
    public int[] _offset;
    public int[] _length;
    
    public char[] _array;
    public int _arrayIndex;
    public int _readOnlyArrayIndex;
    
    private CharArray _charArray = new CharArray();
    
    private ContiguousCharArrayArray _readOnlyArray;
    
    public ContiguousCharArrayArray(int initialCapacity, int maximumCapacity,
            int initialCharacterSize, int maximumCharacterSize) {
        _offset = new int[initialCapacity];
        _length = new int[initialCapacity];
        _array = new char[initialCharacterSize];
        _charArray.ch = _array;
        _maximumCapacity = maximumCapacity;
        _maximumCharacterSize = maximumCharacterSize;
    }

    public ContiguousCharArrayArray() {
        this(DEFAULT_CAPACITY, MAXIMUM_CAPACITY, 
                INITIAL_CHARACTER_SIZE, MAXIMUM_CHARACTER_SIZE);
    }
    
    public final void clear() {
        _arrayIndex = _readOnlyArrayIndex;
        _size = _readOnlyArraySize;
    }

    public final int getArrayIndex() {
        return _arrayIndex;
    }
    
    public final void setReadOnlyArray(ValueArray readOnlyArray, boolean clear) {
        if (!(readOnlyArray instanceof ContiguousCharArrayArray)) {
            throw new IllegalArgumentException(MessageCenter.getString("message.illegalClass", new Object[]{readOnlyArray}));
        }       
        
        setReadOnlyArray((ContiguousCharArrayArray)readOnlyArray, clear);
    }

    public final void setReadOnlyArray(ContiguousCharArrayArray readOnlyArray, boolean clear) {
        if (readOnlyArray != null) {
            _readOnlyArray = readOnlyArray;
            _readOnlyArraySize = readOnlyArray.getSize();
            _readOnlyArrayIndex = readOnlyArray.getArrayIndex();
            
            if (clear) {
                clear();
            }

            _charArray.ch = _array = getCompleteCharArray();
            _offset = getCompleteOffsetArray();
            _length = getCompleteLengthArray();
            _size = _readOnlyArraySize;
        }
    }

    public final char[] getCompleteCharArray() {
        if (_readOnlyArray == null) {
            return _array;
        } else {
            final char[] ra = _readOnlyArray.getCompleteCharArray();
            final char[] a = new char[_readOnlyArrayIndex + _array.length];
            System.arraycopy(ra, 0, a, 0, _readOnlyArrayIndex);
            return a;
        }
    }
    
    public final int[] getCompleteOffsetArray() {
        if (_readOnlyArray == null) {
            return _offset;
        } else {
            final int[] ra = _readOnlyArray.getCompleteOffsetArray();
            final int[] a = new int[_readOnlyArraySize + _offset.length];
            System.arraycopy(ra, 0, a, 0, _readOnlyArraySize);
            return a;
        }
    }
    
    public final int[] getCompleteLengthArray() {
        if (_readOnlyArray == null) {
            return _length;
        } else {
            final int[] ra = _readOnlyArray.getCompleteOffsetArray();
            final int[] a = new int[_readOnlyArraySize + _length.length];
            System.arraycopy(ra, 0, a, 0, _readOnlyArraySize);
            return a;
        }
    }
    
    public final CharArray get(int i) {
        _charArray.start = _offset[i];
        _charArray.length = _length[i];
        return _charArray;
   }
    
    public final void add(int o, int l) {
        if (_size == _offset.length) {
            resize();
        }
            
       _offset[_size] = o;
       _length[_size++] = l;
    }    
    
    public final void add(char[] c, int l) {
        if (_size == _offset.length) {
            resize();
        }
            
       _offset[_size] = _arrayIndex;
       _length[_size++] = l;
       
       final int arrayIndex = _arrayIndex + l;
       if (arrayIndex >= _array.length) {
           resizeArray(arrayIndex);
       }
       
       System.arraycopy(c, 0, _array, _arrayIndex, l);
       _arrayIndex = arrayIndex;
    }  
    
    protected final void resize() {
        if (_size == _maximumCapacity) {
            throw new RuntimeException(MessageCenter.getString("message.arrayMaxCapacity"));
        }

        int newSize = _size * 3 / 2 + 1;
        if (newSize > _maximumCapacity) {
            newSize = _maximumCapacity;
        }

        final int[] offset = new int[newSize];
        System.arraycopy(_offset, 0, offset, 0, _size);
        _offset = offset;

        final int[] length = new int[newSize];
        System.arraycopy(_length, 0, length, 0, _size);
        _length = length;
    }

    protected final void resizeArray(int requestedSize) {
        if (_arrayIndex == _maximumCharacterSize) {
            throw new RuntimeException(MessageCenter.getString("message.maxNumberOfCharacters"));
        }

        int newSize = requestedSize * 3 / 2 + 1;
        if (newSize > _maximumCharacterSize) {
            newSize = _maximumCharacterSize;
        }
        
        final char[] array = new char[newSize];
        System.arraycopy(_array, 0, array, 0, _arrayIndex);
        _charArray.ch = _array = array;
    }    
}
