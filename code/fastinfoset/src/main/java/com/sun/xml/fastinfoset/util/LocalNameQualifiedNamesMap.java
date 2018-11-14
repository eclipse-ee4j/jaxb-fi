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

public class LocalNameQualifiedNamesMap extends KeyIntMap {

    private LocalNameQualifiedNamesMap _readOnlyMap;
   
    private int _index;
    
    public static class Entry {
        final String _key;
        final int _hash;
        public QualifiedName[] _value;
        public int _valueIndex;
        Entry _next;
        
        public Entry(String key, int hash, Entry next) {
            _key = key;
            _hash = hash;
            _next = next;
            _value = new QualifiedName[1];
        }
        
        public void addQualifiedName(QualifiedName name) {
            if (_valueIndex < _value.length) {
                _value[_valueIndex++] = name;
            } else if (_valueIndex == _value.length) {
                QualifiedName[] newValue = new QualifiedName[_valueIndex * 3 / 2 + 1];
                System.arraycopy(_value, 0, newValue, 0, _valueIndex);
                _value = newValue;              
                _value[_valueIndex++] = name;
            }
        }
    }
    
    private Entry[] _table;
    
    public LocalNameQualifiedNamesMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);

        _table = new Entry[_capacity];        
    }
    
    public LocalNameQualifiedNamesMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public LocalNameQualifiedNamesMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
        
    public final void clear() {
        for (int i = 0; i < _table.length; i++) {
            _table[i] = null;
        }
        _size = 0;
        
        if (_readOnlyMap != null) {        
            _index = _readOnlyMap.getIndex();
        } else {
            _index = 0;
        }
    }

    public final void setReadOnlyMap(KeyIntMap readOnlyMap, boolean clear) {
        if (!(readOnlyMap instanceof LocalNameQualifiedNamesMap)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().
                    getString("message.illegalClass", new Object[]{readOnlyMap}));
        }       
        
        setReadOnlyMap((LocalNameQualifiedNamesMap)readOnlyMap, clear);
    }
    
    public final void setReadOnlyMap(LocalNameQualifiedNamesMap readOnlyMap, boolean clear) {
        _readOnlyMap = readOnlyMap;
        if (_readOnlyMap != null) {
            _readOnlyMapSize = _readOnlyMap.size();
            _index = _readOnlyMap.getIndex();
            if (clear) {
                clear();
            }
        }  else {
            _readOnlyMapSize = 0;
            _index = 0;
        }     
    }
    
    public final boolean isQNameFromReadOnlyMap(QualifiedName name) {
        return (_readOnlyMap != null && name.index <= _readOnlyMap.getIndex());
    }
    
    public final int getNextIndex() {
        return _index++;
    }

    public final int getIndex() {
        return _index;        
    }
    
    public final Entry obtainEntry(String key) {
        final int hash = hashHash(key.hashCode());
        
        if (_readOnlyMap != null) {
            final Entry entry = _readOnlyMap.getEntry(key, hash);
            if (entry != null) {
                return entry;
            }
        }
        
        final int tableIndex = indexFor(hash, _table.length);
        for (Entry e = _table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && eq(key, e._key)) {
                return e;
            }
        }

        return addEntry(key, hash, tableIndex);        
    }
    
    public final Entry obtainDynamicEntry(String key) {
        final int hash = hashHash(key.hashCode());
        
        final int tableIndex = indexFor(hash, _table.length);
        for (Entry e = _table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && eq(key, e._key)) {
                return e;
            }
        }

        return addEntry(key, hash, tableIndex);        
    }

    private final Entry getEntry(String key, int hash) {
        if (_readOnlyMap != null) {
            final Entry entry = _readOnlyMap.getEntry(key, hash);
            if (entry != null) {
                return entry;
            }
        }

        final int tableIndex = indexFor(hash, _table.length);
        for (Entry e = _table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && eq(key, e._key)) {
                return e;
            }
        }
                
        return null;
    }


    private final Entry addEntry(String key, int hash, int bucketIndex) {
	Entry e = _table[bucketIndex];
        _table[bucketIndex] = new Entry(key, hash, e);
        e = _table[bucketIndex];
        if (_size++ >= _threshold) {
            resize(2 * _table.length);
        }
        
        return e;
    }
    
    private final void resize(int newCapacity) {
        _capacity = newCapacity;
        Entry[] oldTable = _table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            _threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[_capacity];
        transfer(newTable);
        _table = newTable;
        _threshold = (int)(_capacity * _loadFactor);        
    }

    private final void transfer(Entry[] newTable) {
        Entry[] src = _table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++) {
            Entry e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    Entry next = e._next;
                    int i = indexFor(e._hash, newCapacity);  
                    e._next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }
        
    private final boolean eq(String x, String y) {
        return x == y || x.equals(y);
    }

}
