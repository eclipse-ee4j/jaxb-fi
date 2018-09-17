/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.fime.stax;


import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import com.sun.xml.fime.util.MessageCenter;

public class StAXManager {
    protected static final String STAX_NOTATIONS = "javax.xml.stream.notations";
    protected static final String STAX_ENTITIES = "javax.xml.stream.entities";
    
    Hashtable features = new Hashtable();
    
    public static final int CONTEXT_READER = 1;
    public static final int CONTEXT_WRITER = 2;
    
    
    /** Creates a new instance of StAXManager */
    public StAXManager() {
    }
    
    public StAXManager(int context) {
        switch(context){
            case CONTEXT_READER:{
                initConfigurableReaderProperties();
                break;
            }
            case CONTEXT_WRITER:{
                initWriterProps();
                break;
            }
        }
    }
    
    public StAXManager(StAXManager manager){
        
        Hashtable properties = manager.getProperties();
        //features.putAll(properties);
        for (Enumeration e = properties.keys(); e.hasMoreElements(); ) {
            Object key = e.nextElement();
            features.put(key, properties.get(key));
        }
    }
    
    private Hashtable getProperties(){
        return features ;
    }
    
    private void initConfigurableReaderProperties(){
        //spec v1.0 default values
        features.put(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        features.put(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
        features.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
        features.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        features.put(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        features.put(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
    }
    
    private void initWriterProps(){
        features.put(XMLOutputFactory.IS_REPAIRING_NAMESPACES , Boolean.FALSE);
    }
    
    /**
     * public void reset(){
     * features.clear() ;
     * }
     */
    public boolean containsProperty(String property){
        return features.containsKey(property) ;
    }
    
    public Object getProperty(String name){
        checkProperty(name);
        return features.get(name);
    }
    
    public void setProperty(String name, Object value){
        checkProperty(name);
        if (name.equals(XMLInputFactory.IS_VALIDATING) &&
                Boolean.TRUE.equals(value)){
            throw new IllegalArgumentException(MessageCenter.getString("message.validationNotSupported") +
                    MessageCenter.getString("support_validation"));
        } else if (name.equals(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES) &&
                Boolean.TRUE.equals(value)) {
            throw new IllegalArgumentException(MessageCenter.getString("message.externalEntities") +
                    MessageCenter.getString("resolve_external_entities_"));
        }
        features.put(name,value);

    }
    
    public void checkProperty(String name) {
        if (!features.containsKey(name))
            throw new IllegalArgumentException(MessageCenter.getString("message.propertyNotSupported", new Object[]{name}));
    }

    public String toString(){
        return features.toString();
    }
        
}
