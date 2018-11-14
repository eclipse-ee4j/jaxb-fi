/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.xml.fastinfoset.util;
import com.sun.xml.fastinfoset.CommonResourceBundle;

public class StringArray extends ValueArray {
    
    public String[] _array;
    
    private StringArray _readOnlyArray;

    private boolean _clear;
    
    public StringArray(int initialCapacity, int maximumCapacity, boolean clear) {
        _array = new String[initialCapacity];
        _maximumCapacity = maximumCapacity;
        _clear = clear;
    }

    public StringArray() {
        this(DEFAULT_CAPACITY, MAXIMUM_CAPACITY, false);
    }

    public final void clear() {
        if (_clear) for (int i = _readOnlyArraySize; i < _size; i++) {
            _array[i] = null;
        }
        _size = _readOnlyArraySize;
    }

    /**
     * Returns cloned version of internal String[].
     * @return cloned version of internal String[].
     */
    public final String[] getArray() {
        if (_array == null) return null;
        
        final String[] clonedArray = new String[_array.length];
        System.arraycopy(_array, 0, clonedArray, 0, _array.length);
        return clonedArray;
    }
    
    public final void setReadOnlyArray(ValueArray readOnlyArray, boolean clear) {
        if (!(readOnlyArray instanceof StringArray)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().
                    getString("message.illegalClass", new Object[]{readOnlyArray}));
        }       
        
        setReadOnlyArray((StringArray)readOnlyArray, clear);
    }

    public final void setReadOnlyArray(StringArray readOnlyArray, boolean clear) {
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

    public final String[] getCompleteArray() {
        if (_readOnlyArray == null) {
            // Return cloned version of internal _array
            return getArray();
//            return _array;
        } else {
            final String[] ra = _readOnlyArray.getCompleteArray();
            final String[] a = new String[_readOnlyArraySize + _array.length];
            System.arraycopy(ra, 0, a, 0, _readOnlyArraySize);
            return a;
        }
    }
    
    public final String get(int i) {
        return _array[i];
    }
 
    public final int add(String s) {
        if (_size == _array.length) {
            resize();
        }
            
       _array[_size++] = s;
       return _size;
    }
    
    protected final void resize() {
        if (_size == _maximumCapacity) {
            throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity"));
        }

        int newSize = _size * 3 / 2 + 1;
        if (newSize > _maximumCapacity) {
            newSize = _maximumCapacity;
        }

        final String[] newArray = new String[newSize];
        System.arraycopy(_array, 0, newArray, 0, _size);
        _array = newArray;
    }
}
