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

package org.jvnet.fastinfoset.stax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Fast Infoset Stream Reader.
 * <p>
 * This interface provides additional optimized methods to that of
 * {@link javax.xml.stream.XMLStreamReader}.
 */
public interface FastInfosetStreamReader {
    /**
     * Peek at the next event.
     *
     * @return the event, which will be the same as that returned from
     *         {@link XMLStreamReader#next}.
     */
    public int peekNext() throws XMLStreamException;

    // Faster access methods without checks

    public int accessNamespaceCount();

    public String accessLocalName();

    public String accessNamespaceURI();

    public String accessPrefix();

    /**
     * Returns a cloned char[] representation of the internal char[] buffer.
     * So be careful, when using this method due to possible performance and
     * memory inefficiency.
     *
     * @return a cloned char[] representation of the internal char[] buffer.
     */
    public char[] accessTextCharacters();

    public int accessTextStart();

    public int accessTextLength();
}
