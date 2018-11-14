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

package com.sun.xml.fastinfoset.stax.events;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;

import com.sun.xml.fastinfoset.CommonResourceBundle;

/**
 * allows a user to register a way to allocate events given an XMLStreamReader.  
 * The XMLEventAllocator can be set on an XMLInputFactory
 * using the property "javax.xml.stream.allocator"
 *
 * This base class uses EventFactory to create events as recommended in the JavaDoc of XMLEventAllocator.
 * However, creating new object per each event reduces performance. The implementation of
 * EventReader therefore will set the Allocator to StAXEventAllocator which implements the 
 * Allocate methods without creating new objects.
 *
 * The spec for the first Allocate method states that it must NOT modify the state of the Reader
 * while the second MAY. For consistency, both Allocate methods in this implementation will
 * NOT modify the state.
 *
 */
public class StAXEventAllocatorBase implements XMLEventAllocator {
    XMLEventFactory factory;
    
    /** Creates a new instance of XMLEventAllocator */
    public StAXEventAllocatorBase() {
        if (System.getProperty("javax.xml.stream.XMLEventFactory")==null) {
            System.setProperty("javax.xml.stream.XMLEventFactory", 
                       "com.sun.xml.fastinfoset.stax.factory.StAXEventFactory");
        }
        factory = XMLEventFactory.newInstance();
    }

    // ---------------------methods defined by XMLEventAllocator-----------------//
    
  /**
   * This method creates an instance of the XMLEventAllocator. This
   * allows the XMLInputFactory to allocate a new instance per reader.
   */
    public XMLEventAllocator newInstance() {
        return new StAXEventAllocatorBase();
    }

  /**
   * This method allocates an event given the current state of the XMLStreamReader.  
   * If this XMLEventAllocator does not have a one-to-one mapping between reader state
   * and events this method will return null.  
   * @param streamReader The XMLStreamReader to allocate from
   * @return the event corresponding to the current reader state
   */
    public XMLEvent allocate(XMLStreamReader streamReader) throws XMLStreamException {
        if(streamReader == null )
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.nullReader"));
        return getXMLEvent(streamReader);
    }
    
  /**
   * This method allocates an event or set of events given the current state of 
   * the XMLStreamReader and adds the event or set of events to the consumer that 
   * was passed in.  
   * @param streamReader The XMLStreamReader to allocate from
   * @param consumer The XMLEventConsumer to add to.
   */
    public void allocate(XMLStreamReader streamReader, XMLEventConsumer consumer) throws XMLStreamException {
        consumer.add(getXMLEvent(streamReader));

    }
    // ---------------------end of methods defined by XMLEventAllocator-----------------//
    
    
    XMLEvent getXMLEvent(XMLStreamReader reader){
        XMLEvent event = null;
        //returns the current event
        int eventType = reader.getEventType();
        //this needs to be set before creating events
        factory.setLocation(reader.getLocation());
        switch(eventType){
            
            case XMLEvent.START_ELEMENT:
            {
                StartElementEvent startElement = (StartElementEvent)factory.createStartElement(reader.getPrefix(),
                                    reader.getNamespaceURI(), reader.getLocalName());

                addAttributes(startElement,reader);
                addNamespaces(startElement, reader);
                //need to fix it along with the Reader
                //setNamespaceContext(startElement,reader);
                event = startElement;
                break;
            }
            case XMLEvent.END_ELEMENT:
            {
                EndElementEvent endElement = (EndElementEvent)factory.createEndElement(
                        reader.getPrefix(), reader.getNamespaceURI(), reader.getLocalName());
                addNamespaces(endElement,reader);
                event = endElement ;
                break;
            }
            case XMLEvent.PROCESSING_INSTRUCTION:
            {
                event = factory.createProcessingInstruction(reader.getPITarget(),reader.getPIData());
                break;
            }
            case XMLEvent.CHARACTERS:
            {
                if (reader.isWhiteSpace())
                  event = factory.createSpace(reader.getText());
                else
                  event = factory.createCharacters(reader.getText());
                
                break;
            }
            case XMLEvent.COMMENT:
            {
                event = factory.createComment(reader.getText());
                break;
            }
            case XMLEvent.START_DOCUMENT:
            {
                StartDocumentEvent docEvent = (StartDocumentEvent)factory.createStartDocument(
                        reader.getVersion(), reader.getEncoding(), reader.isStandalone());
                if(reader.getCharacterEncodingScheme() != null){
                    docEvent.setDeclaredEncoding(true);
                }else{
                    docEvent.setDeclaredEncoding(false);
                }
                event = docEvent ;
                break;
            }
            case XMLEvent.END_DOCUMENT:{
                EndDocumentEvent endDocumentEvent = new EndDocumentEvent() ;
                event = endDocumentEvent ;
                break;
            }
            case XMLEvent.ENTITY_REFERENCE:{
                event = factory.createEntityReference(reader.getLocalName(), 
                        new EntityDeclarationImpl(reader.getLocalName(),reader.getText()));
                break;
                
            }
            case XMLEvent.ATTRIBUTE:{
                event = null ;
                break;
            }
            case XMLEvent.DTD:{
                event = factory.createDTD(reader.getText());
                break;
            }
            case XMLEvent.CDATA:{
                event = factory.createCData(reader.getText());
                break;
            }
            case XMLEvent.SPACE:{
                event = factory.createSpace(reader.getText());
                break;
            }
        }
        return event ;
    }
    
    //use event.addAttribute instead of addAttributes to avoid creating another list
    protected void addAttributes(StartElementEvent event,XMLStreamReader streamReader){        
        AttributeBase attr = null;
        for(int i=0; i<streamReader.getAttributeCount() ;i++){
            attr = (AttributeBase)factory.createAttribute(streamReader.getAttributeName(i), 
                                    streamReader.getAttributeValue(i));
            attr.setAttributeType(streamReader.getAttributeType(i));
            attr.setSpecified(streamReader.isAttributeSpecified(i));
            event.addAttribute(attr);
        }
    }
    
    //add namespaces to StartElement/EndElement
    protected void addNamespaces(StartElementEvent event,XMLStreamReader streamReader){
        Namespace namespace = null;
        for(int i=0; i<streamReader.getNamespaceCount(); i++){
            namespace = factory.createNamespace(streamReader.getNamespacePrefix(i),
                                streamReader.getNamespaceURI(i));
            event.addNamespace(namespace);
        }
    }
    protected void addNamespaces(EndElementEvent event,XMLStreamReader streamReader){
        Namespace namespace = null;
        for(int i=0; i<streamReader.getNamespaceCount(); i++){
            namespace = factory.createNamespace(streamReader.getNamespacePrefix(i),
                                streamReader.getNamespaceURI(i));
            event.addNamespace(namespace);
        }
    }
    
    
}
