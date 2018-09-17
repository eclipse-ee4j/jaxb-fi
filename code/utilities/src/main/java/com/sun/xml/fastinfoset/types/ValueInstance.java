/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2011 Oracle and/or its affiliates. All rights reserved.
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
package com.sun.xml.fastinfoset.types;

/**
 * TODO implement CharSequence iface.
 * @author Paul.Sandoz@Sun.Com
 */
public final class ValueInstance {
    public static enum Type {
        encodingAlgorithm,
        alphabet,
        indexedString
    };

    public Type type;
    public int id;
    public String alphabet;
    public Object instance;
    
    public ValueInstance(int id, Object instance) {
        set(id, instance);
    }
    
    public ValueInstance(String alphabet, Object instance) {
        set(alphabet, instance);
    }
    
    public ValueInstance(Object instance) {
        set(instance);
    }
    
    public void set(int id, Object instance) {
        this.type = Type.encodingAlgorithm;
        this.id = id;
        this.instance = instance;
    }
    
    public void set(String alphabet, Object instance) {
        this.type = Type.alphabet;
        this.alphabet = alphabet;
        this.instance = instance;
    }
    
    public void set(Object instance) {
        this.type = Type.indexedString;
        this.instance = instance;
    }
}
