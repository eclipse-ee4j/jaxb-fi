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


import java.io.InputStream;
import java.io.Reader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.sun.xml.fime.util.MessageCenter;

public class StAXInputFactory extends XMLInputFactory {    
    //List of supported properties and default values.
    private StAXManager _manager = new StAXManager(StAXManager.CONTEXT_READER) ;
    
    public StAXInputFactory() {
    }
    
    public static XMLInputFactory newInstance() {
        return XMLInputFactory.newInstance();
    }

  /**
   * Create a new XMLStreamReader from a reader
   * @param reader the XML data to read from
   * @throws XMLStreamException 
   */
    public XMLStreamReader createXMLStreamReader(Reader xmlfile) throws XMLStreamException {
        throw new RuntimeException("Not supported");
    }    
    
    public XMLStreamReader createXMLStreamReader(InputStream s) throws XMLStreamException {
        return new StAXDocumentParser(s, _manager);
    }
    
    /** Get the value of a feature/property from the underlying implementation
     * @param name The name of the property (may not be null)
     * @return The value of the property
     * @throws IllegalArgumentException if the property is not supported
     */
    public Object getProperty(String name) throws IllegalArgumentException {
        if(name == null){
            throw new IllegalArgumentException(MessageCenter.getString("message.nullPropertyName"));
        }
        if(_manager.containsProperty(name))
            return _manager.getProperty(name);
        throw new IllegalArgumentException(MessageCenter.getString("message.propertyNotSupported", new Object[]{name}));
    }
    
    /** Query the set of Properties that this factory supports.
     *
     * @param name The name of the property (may not be null)
     * @return true if the property is supported and false otherwise
     */
    public boolean isPropertySupported(String name) {
        if(name == null)
            return false ;
        else
            return _manager.containsProperty(name);
    }
    
    /** Allows the user to set specific feature/property on the underlying implementation. The underlying implementation
     * is not required to support every setting of every property in the specification and may use IllegalArgumentException
     * to signal that an unsupported property may not be set with the specified value.
     * @param name The name of the property (may not be null)
     * @param value The value of the property
     * @throws IllegalArgumentException if the property is not supported
     */
    public void setProperty(String name, Object value) throws IllegalArgumentException {
        _manager.setProperty(name,value);
    }

    public XMLStreamReader createXMLStreamReader(Reader reader, boolean requiringXMLDeclaration) throws XMLStreamException {
        throw new RuntimeException("Not supported");
    }

    public XMLStreamReader createXMLStreamReader(InputStream s, boolean requiringXMLDeclaration) throws XMLStreamException {
        return new StAXDocumentParser(s, _manager);
    }
    
}
