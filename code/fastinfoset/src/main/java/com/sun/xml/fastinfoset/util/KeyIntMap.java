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

public abstract class KeyIntMap {
    public static final int NOT_PRESENT = -1;
    
    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 20;

    /**
     * The load factor used when none specified in constructor.
     **/
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    int _readOnlyMapSize;
    
    /**
     * The number of key-value mappings contained in this identity hash map.
     */
    int _size;
  
    int _capacity;
    
    /**
     * The next size value at which to resize (capacity * load factor).
     */
    int _threshold;
  
    /**
     * The load factor for the hash table.
     */
    final float _loadFactor;

    static class BaseEntry {
        final int _hash;
        final int _value;

        public BaseEntry(int hash, int value) {
            _hash = hash;
            _value = value;
        }
    }
 
    public KeyIntMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().
                    getString("message.illegalInitialCapacity", new Object[]{Integer.valueOf(initialCapacity)}));
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().
                    getString("message.illegalLoadFactor", new Object[]{Float.valueOf(loadFactor)}));

        // Find a power of 2 >= initialCapacity
        if (initialCapacity != DEFAULT_INITIAL_CAPACITY) {
            _capacity = 1;
            while (_capacity < initialCapacity) 
                _capacity <<= 1;

            _loadFactor = loadFactor;
            _threshold = (int)(_capacity * _loadFactor);
        } else {
            _capacity = DEFAULT_INITIAL_CAPACITY;
            _loadFactor = DEFAULT_LOAD_FACTOR;
            _threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);        
        }
    }
    
    public KeyIntMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public KeyIntMap() {
        _capacity = DEFAULT_INITIAL_CAPACITY;
        _loadFactor = DEFAULT_LOAD_FACTOR;
        _threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
    }

    public final int size() {
        return _size + _readOnlyMapSize;
    }

    public abstract void clear();
    
    public abstract void setReadOnlyMap(KeyIntMap readOnlyMap, boolean clear);

    
    public static final int hashHash(int h) {
        h += ~(h << 9);
        h ^=  (h >>> 14);
        h +=  (h << 4);
        h ^=  (h >>> 10);
        return h;
    }

    public static final int indexFor(int h, int length) {
        return h & (length-1);
    }

}
