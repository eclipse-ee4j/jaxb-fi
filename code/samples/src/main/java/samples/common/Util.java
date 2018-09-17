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

package samples.common;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.namespace.QName;

/** <p>Sample utility.</p>
 *  This is an utility class used for samples in this package.
 */
public class Util {
    
    /** Creates a new instance of Util */
    public Util() {
    }
    
    /** Get event type in string format 
     *
     * @param eventType event type
     * @return String
     */    
    public final static String getEventTypeString(int eventType) {
        switch (eventType){
            case XMLStreamConstants.START_ELEMENT:
                return "START_ELEMENT";
            case XMLStreamConstants.END_ELEMENT:
                return "END_ELEMENT";
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                return "PROCESSING_INSTRUCTION";
            case XMLStreamConstants.CHARACTERS:
                return "CHARACTERS";
            case XMLStreamConstants.COMMENT:
                return "COMMENT";
            case XMLStreamConstants.START_DOCUMENT:
                return "START_DOCUMENT";
            case XMLStreamConstants.END_DOCUMENT:
                return "END_DOCUMENT";
            case XMLStreamConstants.ENTITY_REFERENCE:
                return "ENTITY_REFERENCE";
            case XMLStreamConstants.ATTRIBUTE:
                return "ATTRIBUTE";
            case XMLStreamConstants.DTD:
                return "DTD";
            case XMLStreamConstants.CDATA:
                return "CDATA";
        }
        return "UNKNOWN_EVENT_TYPE";
    }
    
    /** Print out event type
     *
     * @param eventType event type
     */    
    public static void printEventType(int eventType) {
        System.out.print("EVENT TYPE("+eventType+"):");
        System.out.println(getEventTypeString(eventType));
    }
    
    /** Print out element name
     *
     * @param xmlr Stream reader
     * @param eventType event type
     */    
    public static void printName(XMLStreamReader xmlr,int eventType){
        if(xmlr.hasName()){
            System.out.println("HAS NAME: " + xmlr.getLocalName());
        } else {
            System.out.println("HAS NO NAME");
        }
    }
    
    /** Print out text
     *
     * @param xmlr Stream reader
     */    
    public static void printText(XMLStreamReader xmlr){
        if(xmlr.hasText()){
            System.out.println("HAS TEXT: " + xmlr.getText());
        } else {
            System.out.println("HAS NO TEXT");
        }
    }
    
    /** Print out processing instructions
     *
     * @param xmlr Stream reader
     */    
    public static void printPIData(XMLStreamReader xmlr){
        if (xmlr.getEventType() == XMLStreamConstants.PROCESSING_INSTRUCTION){
            System.out.println(" PI target = " + xmlr.getPITarget() ) ;
            System.out.println(" PI Data = " + xmlr.getPIData() ) ;
        }
    }
    
    /** Print out element attributes
     *
     * @param xmlr Stream reader
     */    
    public static void printAttributes(XMLStreamReader xmlr){
        if(xmlr.getAttributeCount() > 0){
            System.out.println("\nHAS ATTRIBUTES: ");
            int count = xmlr.getAttributeCount() ;
            for(int i = 0 ; i < count ; i++) {
                
                QName name = xmlr.getAttributeName(i) ;
                String namespace = xmlr.getAttributeNamespace(i) ;
                String  type = xmlr.getAttributeType(i) ;
                String prefix = xmlr.getAttributePrefix(i) ;
                String value = xmlr.getAttributeValue(i) ;
                
                System.out.println("ATTRIBUTE-PREFIX: " + prefix );
                System.out.println("ATTRIBUTE-NAMESP: " + namespace );
                System.out.println("ATTRIBUTE-NAME:   " + name.toString() );
                System.out.println("ATTRIBUTE-VALUE:  " + value );
                System.out.println("ATTRIBUTE-TYPE:  " + type );
                System.out.println();
                
            }
            
        } else {
            System.out.println("HAS NO ATTRIBUTES");
        }
    }
        
}
