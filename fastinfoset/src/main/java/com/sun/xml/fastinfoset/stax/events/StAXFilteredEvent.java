/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2005, 2023 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.fastinfoset.stax.events;

import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.EventFilter;
import javax.xml.stream.events.Characters;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import com.sun.xml.fastinfoset.CommonResourceBundle;

public class StAXFilteredEvent implements XMLEventReader {
    private XMLEventReader eventReader;
    private EventFilter _filter;
    
    /** Creates a new instance of StAXFilteredEvent */
    public StAXFilteredEvent() {
    }
    
    public StAXFilteredEvent(XMLEventReader reader, EventFilter filter) throws XMLStreamException
    {
        eventReader = reader;
        _filter = filter;
    }

    public void setEventReader(XMLEventReader reader) {
        eventReader = reader;
    }

    public void setFilter(EventFilter filter) {
        _filter = filter;
    }

    @Override
    public Object next() {
        try {
            return nextEvent();
        } catch (XMLStreamException e) {
            return null;
        }
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException
    {
        if (hasNext())
            return eventReader.nextEvent();
        return null;
    }

    @Override
    public String getElementText() throws XMLStreamException
    {
        StringBuilder buffer = new StringBuilder();
        XMLEvent e = nextEvent();
        if (!e.isStartElement())
            throw new XMLStreamException(
            CommonResourceBundle.getInstance().getString("message.mustBeOnSTART_ELEMENT"));            

        while(hasNext()) {
            e = nextEvent();
            if(e.isStartElement())
                throw new XMLStreamException(
                CommonResourceBundle.getInstance().getString("message.getElementTextExpectTextOnly"));
            if(e.isCharacters())
                buffer.append(((Characters) e).getData());
            if(e.isEndElement())
                return buffer.toString();
        } 
        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.END_ELEMENTnotFound"));
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        while(hasNext()) {
            XMLEvent e = nextEvent();
            if (e.isStartElement() || e.isEndElement())
                return e;
        }
        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.startOrEndNotFound"));
    }


    @Override
    public boolean hasNext()
    {
        try { 
            while(eventReader.hasNext()) {
                if (_filter.accept(eventReader.peek())) return true;
                eventReader.nextEvent();
            }
            return false;
        } catch (XMLStreamException e) {
            return false;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public XMLEvent peek() throws XMLStreamException
    {
        if (hasNext())
            return eventReader.peek();
        return null;
    }

    @Override
    public void close() throws XMLStreamException
    {
        eventReader.close();
    }

    @Override
    public Object getProperty(String name) {
        return eventReader.getProperty(name);
    }

}
