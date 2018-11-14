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

import com.sun.xml.fastinfoset.stax.*;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import com.sun.xml.fastinfoset.CommonResourceBundle;



public class StAXEventReader implements javax.xml.stream.XMLEventReader{
    protected XMLStreamReader _streamReader ;
    protected XMLEventAllocator _eventAllocator;
    private XMLEvent _currentEvent;     //the current event
    private XMLEvent[] events = new XMLEvent[3];
    private int size = 3;
    private int currentIndex = 0;
    private boolean hasEvent = false;   //true when current event exists, false initially & at end
    
    //only constructor will do because we delegate everything to underlying XMLStreamReader
    public StAXEventReader(XMLStreamReader reader) throws  XMLStreamException {
        _streamReader = reader ;
        _eventAllocator = (XMLEventAllocator)reader.getProperty(XMLInputFactory.ALLOCATOR);
        if(_eventAllocator == null){
            _eventAllocator = new StAXEventAllocatorBase();
        }
        //initialize
        if (_streamReader.hasNext())
        {
            _streamReader.next();
            _currentEvent =_eventAllocator.allocate(_streamReader);
            events[0] = _currentEvent;
            hasEvent = true;
        } else {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.noElement"));
        }
    }
        
    public boolean hasNext() {        
        return hasEvent;
    }    
    
    public XMLEvent nextEvent() throws XMLStreamException {
        XMLEvent event = null;
        XMLEvent nextEvent = null;
        if (hasEvent) 
        {
            event = events[currentIndex];
            events[currentIndex] = null;
            if (_streamReader.hasNext())
            {
                //advance and read the next
                _streamReader.next();
                nextEvent = _eventAllocator.allocate(_streamReader);
                if (++currentIndex==size)
                    currentIndex = 0;
                events[currentIndex] = nextEvent;
                hasEvent = true;
            } else {
                _currentEvent = null;
                hasEvent = false;
            }
            return event;
        }
        else{
            throw new NoSuchElementException();
        }        
    }
    
    public void remove(){
        //stream reader is read-only.
        throw new java.lang.UnsupportedOperationException();
    }
    
    
    public void close() throws XMLStreamException {
        _streamReader.close();
    }
    
    /** Reads the content of a text-only element. Precondition:
     * the current event is START_ELEMENT. Postcondition:
     * The current event is the corresponding END_ELEMENT.
     * @throws XMLStreamException if the current event is not a START_ELEMENT
     * or if a non text element is encountered
     */
    public String getElementText() throws XMLStreamException {
        if(!hasEvent) {
            throw new NoSuchElementException();
        }
                
        if(!_currentEvent.isStartElement()) {
            StAXDocumentParser parser = (StAXDocumentParser)_streamReader;
            return parser.getElementText(true);
        } else {
            return _streamReader.getElementText();
        }
    }
    
    /** Get the value of a feature/property from the underlying implementation
     * @param name The name of the property
     * @return The value of the property
     * @throws IllegalArgumentException if the property is not supported
     */
    public Object getProperty(java.lang.String name) throws java.lang.IllegalArgumentException {
        return _streamReader.getProperty(name) ;
    }
    
    /** Skips any insignificant space events until a START_ELEMENT or
     * END_ELEMENT is reached. If anything other than space characters are
     * encountered, an exception is thrown. This method should
     * be used when processing element-only content because
     * the parser is not able to recognize ignorable whitespace if
     * the DTD is missing or not interpreted.
     * @throws XMLStreamException if anything other than space characters are encountered
     */
    public XMLEvent nextTag() throws XMLStreamException {
        if(!hasEvent) {
            throw new NoSuchElementException();
        }
        StAXDocumentParser parser = (StAXDocumentParser)_streamReader;
        parser.nextTag(true);
        return _eventAllocator.allocate(_streamReader);
    }
    
    //XMLEventReader extends Iterator;
    public Object next() {
        try{
            return nextEvent();
        }catch(XMLStreamException streamException){
            return null;
        }
    }
    
    public XMLEvent peek() throws XMLStreamException{
        if (!hasEvent)
             throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.noElement"));
        _currentEvent = events[currentIndex];
        return _currentEvent;
    }

    public void setAllocator(XMLEventAllocator allocator) {
        if (allocator == null)
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.nullXMLEventAllocator"));

        _eventAllocator = allocator;
    }
    
    
}
