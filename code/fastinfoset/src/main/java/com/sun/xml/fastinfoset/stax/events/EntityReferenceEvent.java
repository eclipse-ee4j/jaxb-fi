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

import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;


public class EntityReferenceEvent extends EventBase implements EntityReference {
    private EntityDeclaration _entityDeclaration ;
    private String _entityName;
    
    public EntityReferenceEvent() {
        init();
    }
    
    public EntityReferenceEvent(String entityName , EntityDeclaration entityDeclaration) {
        init();
        _entityName = entityName;
        _entityDeclaration = entityDeclaration;
    }
    
  /**
   * The name of the entity
   * @return the entity's name, may not be null
   */
    public String getName() {
        return _entityName;
    }
    
  /**
   * Return the declaration of this entity.
   */
    public EntityDeclaration getDeclaration(){
        return _entityDeclaration ;
    }

    public void setName(String name){
        _entityName = name;
    }
    
    public void setDeclaration(EntityDeclaration declaration) {
        _entityDeclaration = declaration ;
    }
    
    public String toString() {
        String text = _entityDeclaration.getReplacementText();
        if(text == null)
            text = "";
        return "&" + getName() + ";='" + text + "'";
    }
        
    protected void init() {
        setEventType(ENTITY_REFERENCE);
    }
    
    
}
