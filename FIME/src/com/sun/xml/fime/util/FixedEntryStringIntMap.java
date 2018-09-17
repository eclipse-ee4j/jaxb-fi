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


public class FixedEntryStringIntMap extends StringIntMap {
    
    private Entry _fixedEntry;

    public FixedEntryStringIntMap(String fixedEntry, int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        
        // Add the fixed entry
        final int hash = hashHash(fixedEntry.hashCode());
        final int tableIndex = indexFor(hash, _table.length);
        _table[tableIndex] = _fixedEntry = new Entry(fixedEntry, hash, 0, null);
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
        if (_fixedEntry != null) {
            final int tableIndex = indexFor(_fixedEntry._hash, _table.length);
            _table[tableIndex] = _fixedEntry;
            _fixedEntry._next = null;
            _size = 1;
        }
    }

    public final void setReadOnlyMap(KeyIntMap readOnlyMap, boolean clear) {
        if (!(readOnlyMap instanceof FixedEntryStringIntMap)) {
            throw new IllegalArgumentException(MessageCenter.
                    getString("message.illegalClass", new Object[]{readOnlyMap}));
        }       
        
        setReadOnlyMap((FixedEntryStringIntMap)readOnlyMap, clear);
    }
    
    public final void setReadOnlyMap(FixedEntryStringIntMap readOnlyMap, boolean clear) {
        _readOnlyMap = readOnlyMap;
        if (_readOnlyMap != null) {
            readOnlyMap.removeFixedEntry();
            _readOnlyMapSize = readOnlyMap.size();
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
