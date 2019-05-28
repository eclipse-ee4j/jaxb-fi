/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2019 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.fastinfoset.stax.events ;

import javax.xml.stream.Location;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.QName;
import java.io.Writer;
import com.sun.xml.fastinfoset.CommonResourceBundle;


public abstract class EventBase implements XMLEvent {

    /* Event type this event corresponds to */
    protected int _eventType;
    protected Location _location = null;

    public EventBase() {

    }

    public EventBase(int eventType) {
        _eventType = eventType;
    }

    /**
    * Returns an integer code for this event.
    */
    public int getEventType() {
        return _eventType;
    }

    protected void setEventType(int eventType){
        _eventType = eventType;
    }


    public boolean isStartElement() {
        return _eventType == START_ELEMENT;
    }

    public boolean isEndElement() {
        return _eventType == END_ELEMENT;
    }

    public boolean isEntityReference() {
        return _eventType == ENTITY_REFERENCE;
    }

    public boolean isProcessingInstruction() {
        return _eventType == PROCESSING_INSTRUCTION;
    }

    public boolean isStartDocument() {
        return _eventType == START_DOCUMENT;
    }

    public boolean isEndDocument() {
        return _eventType == END_DOCUMENT;
    }

  /**
   * Return the location of this event.  The Location
   * returned from this method is non-volatile and
   * will retain its information.
   * @see javax.xml.stream.Location
   */
    public Location getLocation(){
        return _location;
    }

    public void setLocation(Location loc){
        _location = loc;
    }
    public String getSystemId() {
        if(_location == null )
            return "";
        else
            return _location.getSystemId();
    }

    /** Returns this event as Characters, may result in
     * a class cast exception if this event is not Characters.
     */
    public Characters asCharacters() {
        if (isCharacters()) {
            return (Characters)this;
        } else
            throw new ClassCastException(CommonResourceBundle.getInstance().getString("message.charactersCast", new Object[]{getEventTypeString()}));
    }

    /** Returns this event as an end  element event, may result in
     * a class cast exception if this event is not a end element.
     */
    public EndElement asEndElement() {
        if (isEndElement()) {
            return (EndElement)this;
        } else
            throw new ClassCastException(CommonResourceBundle.getInstance().getString("message.endElementCase", new Object[]{getEventTypeString()}));
    }

  /**
   * Returns this event as a start element event, may result in
   * a class cast exception if this event is not a start element.
   */
    public StartElement asStartElement() {
        if (isStartElement()) {
            return (StartElement)this;
        } else
            throw new ClassCastException(CommonResourceBundle.getInstance().getString("message.startElementCase", new Object[]{getEventTypeString()}));
    }

    /**
    * This method is provided for implementations to provide
    * optional type information about the associated event.
    * It is optional and will return null if no information
    * is available.
    */
    public QName getSchemaType() {
        return null;
    }

    /** A utility function to check if this event is an Attribute.
     * @see javax.xml.stream.events.Attribute
     */
    public boolean isAttribute() {
        return _eventType == ATTRIBUTE;
    }

    /** A utility function to check if this event is Characters.
     * @see javax.xml.stream.events.Characters
     */
    public boolean isCharacters() {
        return _eventType == CHARACTERS;
    }

    /** A utility function to check if this event is a Namespace.
     * @see javax.xml.stream.events.Namespace
     */
    public boolean isNamespace() {
        return _eventType == NAMESPACE;
    }


    /**
    * This method will write the XMLEvent as per the XML 1.0 specification as Unicode characters.
    * No indentation or whitespace should be outputted.
    *
    * Any user defined event type SHALL have this method
    * called when being written to on an output stream.
    * Built in Event types MUST implement this method,
    * but implementations MAY choose not call these methods
    * for optimizations reasons when writing out built in
    * Events to an output stream.
    * The output generated MUST be equivalent in terms of the
    * infoset expressed.
    *
    * @param writer The writer that will output the data
    * @throws XMLStreamException if there is a fatal error writing the event
    */
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
    }

    private String getEventTypeString() {
        switch (_eventType){
            case START_ELEMENT:
                return "StartElementEvent";
            case END_ELEMENT:
                return "EndElementEvent";
            case PROCESSING_INSTRUCTION:
                return "ProcessingInstructionEvent";
            case CHARACTERS:
                return "CharacterEvent";
            case COMMENT:
                return "CommentEvent";
            case START_DOCUMENT:
                return "StartDocumentEvent";
            case END_DOCUMENT:
                return "EndDocumentEvent";
            case ENTITY_REFERENCE:
                return "EntityReferenceEvent";
            case ATTRIBUTE:
                return "AttributeBase";
            case DTD:
                return "DTDEvent";
            case CDATA:
                return "CDATA";
        }
        return "UNKNOWN_EVENT_TYPE";
    }

}
