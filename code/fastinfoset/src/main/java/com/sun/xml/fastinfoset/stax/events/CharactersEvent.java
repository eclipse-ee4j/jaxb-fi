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

import com.sun.xml.fastinfoset.org.apache.xerces.util.XMLChar;
import javax.xml.stream.events.Characters;


public class CharactersEvent extends EventBase implements Characters {
    private String _text;
    private boolean isCData=false;
    private boolean isSpace=false;
    private boolean isIgnorable=false;
    private boolean needtoCheck = true;
    
    public CharactersEvent() {
        super(CHARACTERS);
    }    
    /**
     *
     * @param data Character Data.
     */
    public CharactersEvent(String data) {
        super(CHARACTERS);
        _text = data;
    }
    
    /**
     *
     * @param data Character Data.
     * @param isCData true if is CData
     */
    public CharactersEvent(String data, boolean isCData) {
        super(CHARACTERS);
        _text = data;
        this.isCData = isCData;
    }
    
  /**
   * Get the character data of this event
   */
   public String getData() {
        return _text;
    }
    
    public void setData(String data){
        _text = data;
    }
    
    /**
     *
     * @return boolean returns true if the data is CData
     */
    public boolean isCData() {
        return isCData;
    }
    
    /**
     *
     * @return String return the String representation of this event.
     */
    public String toString() {
        if(isCData)
            return "<![CDATA[" + _text + "]]>";
        else
            return _text;
    }
    
    /**
     * Return true if this is ignorableWhiteSpace.  If
     * this event is ignorableWhiteSpace its event type will
     * be SPACE.
     * @return boolean true if this is ignorableWhiteSpace.
     */
    public boolean isIgnorableWhiteSpace() {
        return isIgnorable;
    }
    
    /**
     * Returns true if this set of Characters are all whitespace.  Whitspace inside a document
     * is reported as CHARACTERS.  This method allows checking of CHARACTERS events to see 
     * if they are composed of only whitespace characters
     * @return boolean true if this set of Characters are all whitespace
     */
    public boolean isWhiteSpace() {
        //no synchronization checks made.
        if(needtoCheck){
            checkWhiteSpace();
            needtoCheck = false;
        }
        return isSpace;
    }
    
    public void setSpace(boolean isSpace) {
        this.isSpace = isSpace;
        needtoCheck = false;
    }
    public void setIgnorable(boolean isIgnorable){
        this.isIgnorable = isIgnorable;
        setEventType(SPACE);
    }
    private void checkWhiteSpace(){
        //refer to xerces XMLChar
        if(!Util.isEmptyString(_text)){
            isSpace = true;
            for(int i=0;i<_text.length();i++){
                if(!XMLChar.isSpace(_text.charAt(i))){
                    isSpace = false;
                    break;
                }
            }
        }
    }
}
