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

package com.sun.xml.fastinfoset.stax.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.StreamFilter;
import com.sun.xml.fastinfoset.CommonResourceBundle;


public class StAXFilteredParser extends StAXParserWrapper {
    private StreamFilter _filter;
    
    /** Creates a new instance of StAXFilteredParser */
    public StAXFilteredParser() {
    }
    public StAXFilteredParser(XMLStreamReader reader, StreamFilter filter) {
        super(reader);
        _filter = filter;
    }
    
    public void setFilter(StreamFilter filter) {
        _filter = filter;
    }

    public int next() throws XMLStreamException
    {
        if (hasNext())
            return super.next();
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.noMoreItems"));
    }

    public boolean hasNext() throws XMLStreamException
    {
        while (super.hasNext()) {
            if (_filter.accept(getReader())) return true;
            super.next();
        }
        return false;
    }
    
}
