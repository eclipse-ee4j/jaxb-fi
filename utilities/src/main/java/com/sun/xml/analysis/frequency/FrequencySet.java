/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.xml.analysis.frequency;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A Set that manages how many occurances of a value occurs in the set.
 * <p>
 * TODO: Sort entries lexically for set of values with 0 occurences.
 * @author Paul.Sandoz@Sun.Com
 */
public class FrequencySet<T> extends HashMap<T, Integer> {

    private static final long serialVersionUID = 3211015452350934703L;

    public FrequencySet(int initialCapacity) {
        super(initialCapacity);
    }

    public FrequencySet() {
    }

    /**
     * Add a value to the set.
     *
     * @param value the value to put in the set.
     */
    public void add(T value) {
        _add(value, 1);
    }
    
    /**
     * Add a value to the set with 0 occurences.
     *
     * @param value the value to put in the set.
     */
    public void add0(T value) {
        _add(value, 0);
    }
    
    private void _add(T value, int v) {
        Integer f = get(value);
        f = (f == null) ? v : f + 1;        
        put(value, f);
    }
    
    private class FrequencyComparator implements Comparator<Map.Entry<T, Integer>> {
        @Override
        public int compare(Map.Entry<T, Integer> e1, Map.Entry<T, Integer> e2) {
            int diff = e2.getValue() - e1.getValue();
            if (diff == 0) {
                if (e1.getKey().equals(e2.getKey())) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return diff;
            }
        }
    }

    /**
     * Create an ordered list of values in the order of decreasing frequency
     * of occurence.
     *
     * @return the list of values in the order of decreasing frequency
     *         of occurence.
     */
    public List<T> createFrequencyBasedList() {
        Set<Map.Entry<T, Integer>> s = new TreeSet<>(new FrequencyComparator());
        s.addAll(entrySet());

        List<T> l = new ArrayList<>();
        for (Map.Entry<T, Integer> e : s) {
            l.add(e.getKey());
        }
    
        return java.util.Collections.unmodifiableList(l);
    }    
    
    /**
     * Create an ordered set of values in the order of decreasing frequency
     * of occurence.
     *
     * @return the set of values in the order of decreasing frequency
     *         of occurence.
     */
    public Set<T> createFrequencyBasedSet() {
        Set<Map.Entry<T, Integer>> s = new TreeSet<>(new FrequencyComparator());
        s.addAll(entrySet());

        Set<T> l = new LinkedHashSet<>();
        for (Map.Entry<T, Integer> e : s) {
            l.add(e.getKey());
        }
    
        return java.util.Collections.unmodifiableSet(l);
    }    
}
