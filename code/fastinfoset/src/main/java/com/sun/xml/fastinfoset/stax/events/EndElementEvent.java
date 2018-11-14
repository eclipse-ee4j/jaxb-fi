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

package com.sun.xml.fastinfoset.stax.events ;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;

import com.sun.xml.fastinfoset.stax.events.EmptyIterator;


public class EndElementEvent extends EventBase implements EndElement {
    
    List _namespaces = null;
    QName _qname ;
    
    public void reset() {
        if (_namespaces != null) _namespaces.clear();
    }
    
    public EndElementEvent() {
        setEventType(END_ELEMENT);
    }
    
    public EndElementEvent(String prefix, String namespaceURI, String localpart) {
        _qname = getQName(namespaceURI,localpart,prefix);
        setEventType(END_ELEMENT);
    }

    public EndElementEvent(QName qname) {
        _qname = qname;
        setEventType(END_ELEMENT);
    }
        
  /**
   * Get the name of this event
   * @return the qualified name of this event
   */
    public QName getName() {
        return _qname;
    }
    
    public void setName(QName qname) {
        _qname = qname;
    }
    

    /** Returns an Iterator of namespaces that have gone out
     * of scope.  Returns an empty iterator if no namespaces have gone
     * out of scope.
     * @return an Iterator over Namespace interfaces, or an
     * empty iterator
     */
    public Iterator getNamespaces() {
        if(_namespaces != null)
            return _namespaces.iterator();
        return EmptyIterator.getInstance();
    }
    
    public void addNamespace(Namespace namespace){
        if (_namespaces == null) {
            _namespaces = new ArrayList();
        }
        _namespaces.add(namespace);
    }
    
    public String toString() { 
        StringBuffer sb = new StringBuffer();
        sb.append("</").append(nameAsString());
        Iterator namespaces = getNamespaces();
        while(namespaces.hasNext()) {
            sb.append(" ").append(namespaces.next().toString());
        }
        sb.append(">");
        return sb.toString();
    }

    
    private String nameAsString() {
        if("".equals(_qname.getNamespaceURI()))
            return _qname.getLocalPart();
        if(_qname.getPrefix() != null)
            return "['" + _qname.getNamespaceURI() + "']:" + _qname.getPrefix() + ":" + _qname.getLocalPart();
        else
            return "['" + _qname.getNamespaceURI() + "']:" + _qname.getLocalPart();
    }
    private QName getQName(String uri, String localPart, String prefix){
        QName qn = null;
        if(prefix != null && uri != null)
            qn = new QName(uri, localPart, prefix);
        else if(prefix == null && uri != null)
            qn = new QName(uri, localPart);
        else if(prefix == null && uri == null)
            qn = new QName(localPart);
        return qn;
    }    
}
