/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2012 Oracle and/or its affiliates. All rights reserved.
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
package com.sun.xml.analysis.types;

import com.sun.xml.fastinfoset.types.XSDataType;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSListSimpleType;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSUnionSimpleType;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.visitor.XSSimpleTypeVisitor;
import com.sun.xml.xsom.visitor.XSVisitor;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A Schema processor that collects the XSD simple types of elements and 
 * attributes declarations.
 * <p>
 * Maps of element/attribute local name to a set of XSDataType are created
 * when a schema is processed. 
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class SchemaProcessor {
        
    private class InternalSchemaProcessor implements XSVisitor, XSSimpleTypeVisitor {
            
        public void annotation(XSAnnotation xSAnnotation) {
        }

        public void attGroupDecl(XSAttGroupDecl decl) {
            for (XSAttGroupDecl groupDecl : decl.getAttGroups()) {
                groupDecl.visit(this);
            }

            for (XSAttributeUse use : decl.getAttributeUses()) {
                use.visit(this);
            }        
        }

        public void attributeDecl(XSAttributeDecl xSAttributeDecl) {
            _attribute = qname(xSAttributeDecl);
            _element = null;

            XSSimpleType s = xSAttributeDecl.getType();
            s.visit((XSSimpleTypeVisitor)this);
        }

        public void attributeUse(XSAttributeUse use) {
            XSAttributeDecl decl = use.getDecl();        
            decl.visit(this);
        }

        public void complexType(XSComplexType type) {

            if (type.getContentType().asSimpleType() != null) {
                XSType baseType = type.getBaseType();

                if (type.getDerivationMethod() != XSType.RESTRICTION) {
                    // check if have redefine tag
                    if (type.getName() != null &&
                            (type.getTargetNamespace().compareTo(
                            baseType.getTargetNamespace()) ==
                            0)
                            && (type.getName().compareTo(baseType.getName()) == 0)) {
                        baseType.visit(this);
                    }
                }

                XSSimpleType s = type.getContentType().asSimpleType();
                // processSimpleType(s);
                s.visit((XSSimpleTypeVisitor)this);
            } else {                
                XSComplexType baseType = type.getBaseType().asComplexType();

                if (type.getDerivationMethod() != XSType.RESTRICTION) {
                    // check if have redefine tag
                    if (type.getName() != null && 
                            type.getTargetNamespace().compareTo(
                                baseType.getTargetNamespace()) == 0 && 
                            type.getName().compareTo(baseType.getName()) == 0) {
                        baseType.visit(this);
                    }
                    type.getExplicitContent().visit(this);
                }
                type.getContentType().visit(this);
            }

            for (XSAttGroupDecl a : type.getAttGroups()) {
                a.visit(this);
            }

            for (XSAttributeUse a : type.getDeclaredAttributeUses()) {
                a.visit(this);
            }
        }

        public void schema(XSSchema s) {
            for (XSAttGroupDecl groupDecl : s.getAttGroupDecls().values()) {
                attGroupDecl(groupDecl);
            }

            for (XSAttributeDecl attrDecl : s.getAttributeDecls().values()) {
                attributeDecl(attrDecl);
            }

            for (XSComplexType complexType : s.getComplexTypes().values()) {
                complexType(complexType);
            }

            for (XSElementDecl elementDecl : s.getElementDecls().values()) {
                elementDecl(elementDecl);
            }

            for (XSModelGroupDecl modelGroupDecl : s.getModelGroupDecls().values()) {
                modelGroupDecl(modelGroupDecl);
            }
        }

        public void facet(XSFacet facet) {
        }

        public void notation(XSNotation xSNotation) {
        }

        public void identityConstraint(XSIdentityConstraint xSIdentityConstraint) {
        }

        public void xpath(XSXPath xSXPath) {
        }

        public void wildcard(XSWildcard xSWildcard) {
        }

        public void modelGroupDecl(XSModelGroupDecl decl) {
           modelGroup(decl.getModelGroup());
        }

        public void modelGroup(XSModelGroup group) {
            final int len = group.getSize();
            for (int i = 0; i < len; i++) {
                particle(group.getChild(i));
            }
        }

        private Set<XSElementDecl> seen = new HashSet<XSElementDecl>();

        public void elementDecl(XSElementDecl type) {
            if (seen.contains(type)) return;
            seen.add(type);

            _element = qname(type);
            type.getType().visit(this);
        }

        public void particle(XSParticle part) {
            part.getTerm().visit(this);
        }

        public void empty(XSContentType xSContentType) {
        }    



        public void simpleType(XSSimpleType type) {
            type.visit((XSSimpleTypeVisitor)this);
        }

        public void listSimpleType(XSListSimpleType xSListSimpleType) {
            if (_isListSimpleType) return;

            _isListSimpleType = true;
            simpleType(xSListSimpleType.getItemType());
        }

        public void unionSimpleType(XSUnionSimpleType type) {
        }

        public void restrictionSimpleType(XSRestrictionSimpleType type) {
            if (type.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
                if (_element != null) {
                    addToMap(_elementMap, _element.getLocalPart(), type.getName());
                } else if (_attribute != null) {
                    addToMap(_attributeMap, _attribute.getLocalPart(), type.getName());
                }
            } else {
                XSSimpleType baseType = type.getSimpleBaseType();

                if (baseType == null) return;
                if (baseType.isLocal()) simpleType(baseType);
            }

            reset();
        }
    
    }
    
    private static class ErrorHandlerImpl implements ErrorHandler {
        public void warning(SAXParseException e) throws SAXException {
            System.out.println("WARNING");
            e.printStackTrace();
        }

        public void error(SAXParseException e) throws SAXException {
            System.out.println("ERROR");
            e.printStackTrace();
        }

        public void fatalError(SAXParseException e) throws SAXException {
            System.out.println("FATAL ERROR");
            e.printStackTrace();
        }
    }

    // The list of URLs to schema
    private List<URL> _schema;
    
    private Set<XSDataType> _filter;
    
    private Map<String, Set<XSDataType>> _elementMap = 
            new HashMap<String, Set<XSDataType>>();
    
    private Map<String, Set<XSDataType>> _attributeMap = 
            new HashMap<String, Set<XSDataType>>();
    
    private QName _element;
    
    private QName _attribute;
    
    private boolean _isListSimpleType;
    

    /*
     * Construct a schema processor given a URL to a schema.
     * 
     * @param schema the URL to a schema.
     */
    public SchemaProcessor(URL schema) {
        _schema = new ArrayList();
        _schema.add(schema);
    }
    
    /*
     * Construct a schema processor given a list URLs to schema.
     * 
     * @param schema the list URLs to schema.
     */
    public SchemaProcessor(List<URL> schema) {
        _schema = schema;
    }
    
    /*
     * Get the element to XS data type map.
     * 
     * @return the element to XS data type map.
     */
    public Map<String, Set<XSDataType>> getElementToXSDataTypeMap() {
        return new HashMap<String, Set<XSDataType>>(_elementMap);
    }
    
    /*
     * Get the attribute to XS data type map.
     * 
     * @return the attribute to XS data type map.
     */
    public Map<String, Set<XSDataType>> getAttributeToXSDataTypeMap() {
        return new HashMap<String, Set<XSDataType>>(_attributeMap);
    }
    
    /**
     * Process the schema.
     */
    public void process() throws Exception {
        process(null);
    }
    
    /**
     * Process the schema.
     * <p>
     * @param filter if not null only include elements/attributes with simple
     * types if it is present in the Set of XS data type. Otherwise all 
     * elements/attributes with simple types are included.
     */
    public void process(Set<XSDataType> filter) throws Exception {
        _filter = filter;
        
        XSOMParser parser = new XSOMParser();
        parser.setErrorHandler(new ErrorHandlerImpl());
        
        for (URL u : _schema) {
            InputSource s = new InputSource(u.openStream());
            s.setSystemId(u.toString());
            parser.parse(s);
        }
        
        XSSchemaSet sset = parser.getResult();
        Iterator<XSSchema> is = sset.iterateSchema();
        while (is.hasNext()) {
            XSSchema s = is.next();
            s.visit(new InternalSchemaProcessor());
        }
    }
    
    private void addToMap(Map<String, Set<XSDataType>> map, String localName, String typeName) {
        XSDataType type = XSDataType.create(typeName);
        if (_filter != null && !_filter.contains(type)) return;
        
        Set<XSDataType> types = map.get(localName);
        
        if (types == null) {
            map.put(localName,
                    Collections.singleton(type));
        } else if (!types.contains(type)) {
            if (types.size() == 1) {
                types = new HashSet<XSDataType>(types);
                map.put(localName, types);
            }
            
            types.add(type);
        }
    }

    private void reset() {
        _element = _attribute = null;
        _isListSimpleType = false;
    }
    
    private QName qname(XSDeclaration d) {
        return new QName(d.getTargetNamespace(), d.getName());
    }
    
    private void print() {
        System.out.println("ELEMENTS");
        for (Map.Entry<String, Set<XSDataType>> e : _elementMap.entrySet()) {
            System.out.println("  " + e.getKey() + ": " + e.getValue());
        }
        System.out.println("ATTRIBUTES");
        for (Map.Entry<String, Set<XSDataType>> e : _attributeMap.entrySet()) {
            System.out.println("  " + e.getKey() + ": " + e.getValue());
        }
    }
    
    public static void main(String[] args) throws Exception {
        SchemaProcessor v = new SchemaProcessor(new File(args[0]).toURL());
        v.process();
        v.print();
    }
}
