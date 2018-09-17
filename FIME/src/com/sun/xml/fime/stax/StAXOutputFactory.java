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


import java.io.OutputStream;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.sun.xml.fime.util.MessageCenter;

public class StAXOutputFactory extends XMLOutputFactory {
        
    //List of supported properties and default values.
    private StAXManager _manager = null ;
    
    /** Creates a new instance of StAXOutputFactory */
    public StAXOutputFactory() {
        _manager = new StAXManager(StAXManager.CONTEXT_WRITER);
    }
    
    /** this is assumed that user wants to write the file in xml format
     *
     */
    public XMLStreamWriter createXMLStreamWriter(Writer writer) throws XMLStreamException {
        throw new RuntimeException("Not supported");
    }
    
    public XMLStreamWriter createXMLStreamWriter(OutputStream outputStream) throws XMLStreamException {
        return new StAXDocumentSerializer(outputStream, new StAXManager(_manager));
    }
    
    public XMLStreamWriter createXMLStreamWriter(OutputStream outputStream, String encoding) throws XMLStreamException {
        StAXDocumentSerializer serializer = new StAXDocumentSerializer(outputStream, new StAXManager(_manager));
        serializer.setEncoding(encoding);
        return serializer;
    }
    
    public Object getProperty(String name) throws java.lang.IllegalArgumentException {
        if(name == null){
            throw new IllegalArgumentException(MessageCenter.getString("message.propertyNotSupported", new Object[]{name}));
        }
        if(_manager.containsProperty(name))
            return _manager.getProperty(name);
        throw new IllegalArgumentException(MessageCenter.getString("message.propertyNotSupported", new Object[]{name}));
    }
    
    public boolean isPropertySupported(String name) {
        if(name == null)
            return false ;
        else
            return _manager.containsProperty(name);
    }
    
    public void setProperty(String name, Object value) throws java.lang.IllegalArgumentException {
        _manager.setProperty(name,value);
        
    }
        
}
