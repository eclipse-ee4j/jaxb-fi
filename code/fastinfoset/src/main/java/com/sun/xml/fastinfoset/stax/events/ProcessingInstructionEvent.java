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

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.ProcessingInstruction;


public class ProcessingInstructionEvent extends EventBase implements ProcessingInstruction {
    
    private String targetName;
    private String _data;
    
    public ProcessingInstructionEvent() {
        init();
    }
    
    public ProcessingInstructionEvent(String targetName, String data) {
        this.targetName = targetName;
        _data = data;
        init();
    }    
    
    protected void init() {
        setEventType(XMLStreamConstants.PROCESSING_INSTRUCTION);
    }
    
    public String getTarget() {
        return targetName;
    }
    
    public void setTarget(String targetName) {
        this.targetName = targetName;
    }
    
    public void setData(String data) {
        _data = data;
    }
    
    public String getData() {
        return _data;
    }
    
    public String toString() {
        if(_data != null && targetName != null)
            return "<?" + targetName + " " + _data + "?>";
        if(targetName != null)
            return "<?" + targetName + "?>";
        if(_data != null)
            return "<?" + _data + "?>";
        else
            return "<??>";
    }
    
}
