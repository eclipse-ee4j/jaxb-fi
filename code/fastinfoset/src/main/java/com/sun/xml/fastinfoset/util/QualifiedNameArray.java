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

import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.CommonResourceBundle;

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
        _size = _readOnlyArraySize;
    }

    /**
     * Returns cloned version of internal QualifiedName[].
     * @return cloned version of internal QualifiedName[].
     */
    public final QualifiedName[] getArray() {
        if (_array == null) return null;
        
        final QualifiedName[] clonedArray = new QualifiedName[_array.length];
        System.arraycopy(_array, 0, clonedArray, 0, _array.length);
        return clonedArray;
    }
    
    public final void setReadOnlyArray(ValueArray readOnlyArray, boolean clear) {
        if (!(readOnlyArray instanceof QualifiedNameArray)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().
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
            // Return cloned version of internal _array
            return getArray();
//            return _array;
        } else {
            final QualifiedName[] ra = _readOnlyArray.getCompleteArray();
            final QualifiedName[] a = new QualifiedName[_readOnlyArraySize + _array.length];
            System.arraycopy(ra, 0, a, 0, _readOnlyArraySize);
            return a;
        }
    }
 
    public final QualifiedName getNext() {
        return (_size == _array.length) ? null : _array[_size];
    }
    
    public final void add(QualifiedName s) {
        if (_size == _array.length) {
            resize();
        }
            
       _array[_size++] = s;
    }

    protected final void resize() {
        if (_size == _maximumCapacity) {
            throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity"));
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
