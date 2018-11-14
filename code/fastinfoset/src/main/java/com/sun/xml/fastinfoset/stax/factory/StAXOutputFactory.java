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

package com.sun.xml.fastinfoset.stax.factory;

import com.sun.xml.fastinfoset.stax.*;
import com.sun.xml.fastinfoset.stax.events.StAXEventWriter;
import javax.xml.transform.Result;
import javax.xml.stream.XMLOutputFactory ;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import com.sun.xml.fastinfoset.CommonResourceBundle;

public class StAXOutputFactory extends XMLOutputFactory {
        
    //List of supported properties and default values.
    private StAXManager _manager = null ;
    
    /** Creates a new instance of StAXOutputFactory */
    public StAXOutputFactory() {
        _manager = new StAXManager(StAXManager.CONTEXT_WRITER);
    }
    
    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return new StAXEventWriter(createXMLStreamWriter(result));
    }
    
    public XMLEventWriter createXMLEventWriter(Writer writer) throws XMLStreamException {
        return new StAXEventWriter(createXMLStreamWriter(writer));
    }
    
    public XMLEventWriter createXMLEventWriter(OutputStream outputStream) throws XMLStreamException {
        return new StAXEventWriter(createXMLStreamWriter(outputStream));
    }
    
    public XMLEventWriter createXMLEventWriter(OutputStream outputStream, String encoding) throws XMLStreamException {
        return new StAXEventWriter(createXMLStreamWriter(outputStream, encoding));
    }
    
    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        if (result instanceof StreamResult) {
            StreamResult streamResult = (StreamResult) result;
            if (streamResult.getWriter() != null) {
                return createXMLStreamWriter(streamResult.getWriter());
            } else if (streamResult.getOutputStream() != null) {
                return createXMLStreamWriter(streamResult.getOutputStream());
            } else if (streamResult.getSystemId() != null) {
                FileWriter writer = null;
                boolean isError = true;
                
                try {
                    writer = new FileWriter(new File(streamResult.getSystemId()));
                    final XMLStreamWriter streamWriter = createXMLStreamWriter(writer);
                    isError = false;
                    
                    return streamWriter;
                } catch (IOException ie) {
                    throw new XMLStreamException(ie);
                } finally {
                    if (isError && writer != null) {
                        try {
                            writer.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        } else {
            FileWriter writer = null;
            boolean isError = true;
            
            try {
                //xxx: should we be using FileOutputStream - nb.
                writer = new FileWriter(new File(result.getSystemId()));
                final XMLStreamWriter streamWriter = createXMLStreamWriter(writer);
                isError = false;

                return streamWriter;
            } catch (IOException ie) {
                throw new XMLStreamException(ie);
            } finally {
                if (isError && writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        throw new java.lang.UnsupportedOperationException();
    }
    
    /** this is assumed that user wants to write the file in xml format
     *
     */
    public XMLStreamWriter createXMLStreamWriter(Writer writer) throws XMLStreamException {
        throw new java.lang.UnsupportedOperationException();
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
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[]{null}));
        }
        if(_manager.containsProperty(name))
            return _manager.getProperty(name);
        throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[]{name}));
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
