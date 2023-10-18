/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004, 2023 Oracle and/or its affiliates. All rights reserved.
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

import javax.xml.namespace.QName;
import javax.xml.stream.events.Namespace;
import java.util.Objects;


public class NamespaceBase extends AttributeBase implements Namespace{
    //J2SE1.5.0 javax.xml.XMLConstants
    static final String DEFAULT_NS_PREFIX = "";
    static final String XML_NS_URI = "http://www.w3.org/XML/1998/namespace";
    static final String XML_NS_PREFIX = "xml";
    static final String XMLNS_ATTRIBUTE_NS_URI = "http://www.w3.org/2000/xmlns/";
    static final String XMLNS_ATTRIBUTE = "xmlns";
    static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";
    static final String W3C_XML_SCHEMA_INSTANCE_NS_URI = "http://www.w3.org/2001/XMLSchema-instance";
    
    //is this namespace default declaration?
    private boolean defaultDeclaration = false;
    
    /** a namespace attribute has a form: xmlns:NCName="URI reference" */
    public NamespaceBase(String namespaceURI) {
        this("", namespaceURI);
    }

  /**
   * Create a new Namespace
   * @param prefix prefix of a namespace is the local name for an attribute
   * @param namespaceURI the uri reference of a namespace is the value for an attribute
   */
    @SuppressWarnings({"this-escape"})
    public NamespaceBase(String prefix, String namespaceURI){
        super(XMLNS_ATTRIBUTE, prefix, namespaceURI);
        setEventType(NAMESPACE);
        if (Util.isEmptyString(prefix)) {
            defaultDeclaration=true;
        }
    }
    
    void setPrefix(String prefix){
        // new QName(uri, localpart, prefix)
        setName(new QName(XMLNS_ATTRIBUTE_NS_URI, Objects.requireNonNullElse(prefix, DEFAULT_NS_PREFIX), XMLNS_ATTRIBUTE));
    }
    
    @Override
    public String getPrefix() {
        if (defaultDeclaration) return "";
        return super.getLocalName();
    }
    
    
  /**
   * set Namespace URI reference (xmlns:prefix = "uri")
   * @param uri the uri reference of a namespace is the value for an attribute
   */
    void setNamespaceURI(String uri) {
        setValue(uri);
    }
    @Override
    public String getNamespaceURI() {
        return getValue();
    }
    

    @Override
    public boolean isNamespace(){
        return true;
    }
    
    @Override
    public boolean isDefaultNamespaceDeclaration() {
        return defaultDeclaration;
    }
    

}
