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

public class FixedEntryStringIntMap extends StringIntMap {
    
    private Entry _fixedEntry;

    public FixedEntryStringIntMap(String fixedEntry, int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        
        // Add the fixed entry
        final int hash = hashHash(fixedEntry.hashCode());
        final int tableIndex = indexFor(hash, _table.length);
        _table[tableIndex] = _fixedEntry = new Entry(fixedEntry, hash, _index++, null);
        if (_size++ >= _threshold) {
            resize(2 * _table.length);
        }
    }
    
    public FixedEntryStringIntMap(String fixedEntry, int initialCapacity) {
        this(fixedEntry, initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public FixedEntryStringIntMap(String fixedEntry) {
        this(fixedEntry, DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public final void clear() {
        for (int i = 0; i < _table.length; i++) {
            _table[i] = null;
        }
        _lastEntry = NULL_ENTRY;
        
        if (_fixedEntry != null) {
            final int tableIndex = indexFor(_fixedEntry._hash, _table.length);
            _table[tableIndex] = _fixedEntry;
            _fixedEntry._next = null;
            _size = 1;
            _index = _readOnlyMapSize + 1;
        } else {
            _size = 0;
            _index = _readOnlyMapSize;
        }
    }

    public final void setReadOnlyMap(KeyIntMap readOnlyMap, boolean clear) {
        if (!(readOnlyMap instanceof FixedEntryStringIntMap)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().
                    getString("message.illegalClass", new Object[]{readOnlyMap}));
        }       
        
        setReadOnlyMap((FixedEntryStringIntMap)readOnlyMap, clear);
    }
    
    public final void setReadOnlyMap(FixedEntryStringIntMap readOnlyMap, boolean clear) {
        _readOnlyMap = readOnlyMap;
        if (_readOnlyMap != null) {
            readOnlyMap.removeFixedEntry();
            _readOnlyMapSize = readOnlyMap.size();
            _index = _readOnlyMapSize + _size;
            if (clear) {
                clear();
            }
        }  else {
            _readOnlyMapSize = 0;
        }     
    }
    
    private final void removeFixedEntry() {
        if (_fixedEntry != null) {
            final int tableIndex = indexFor(_fixedEntry._hash, _table.length);
            final Entry firstEntry = _table[tableIndex];
            if (firstEntry == _fixedEntry) {
                _table[tableIndex] = _fixedEntry._next;
            } else {
                Entry previousEntry = firstEntry;
                while (previousEntry._next != _fixedEntry) {
                    previousEntry = previousEntry._next;
                }
                previousEntry._next = _fixedEntry._next;
            }
            
            _fixedEntry = null;
            _size--;
        }
    }    
}
