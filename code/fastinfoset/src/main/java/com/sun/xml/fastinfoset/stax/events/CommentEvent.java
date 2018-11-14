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


import javax.xml.stream.events.Comment;

public class CommentEvent extends EventBase implements Comment {
    
    /* String data for this event */
    private String _text;
    
    public CommentEvent() {
        super(COMMENT);
    }
    
    public CommentEvent(String text) {
        this();
        _text = text;
    }
    
    
    /**
     * @return String String representation of this event
     */
    public String toString() {
        return "<!--" + _text + "-->";
    }
    
  /**
   * Return the string data of the comment, returns empty string if it
   * does not exist
   */
    public String getText() {
        return _text ;
    }
    
    public void setText(String text) {
        _text = text;
    }
}
