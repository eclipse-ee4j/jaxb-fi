/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jvnet.fastinfoset.stax;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Low level Fast Infoset stream writer.
 * <p>
 * This interface provides additional stream-based serialization methods for the
 * case where an application is in specific control of the serialization
 * process and has the knowledge to call the LowLevel methods in the required
 * order.
 * <p>
 * For example, the application may be able to perform efficient information
 * to indexing mapping and to provide certain information in UTF-8 encoded form.
 * <p>
 * These methods may be used in conjunction with {@link javax.xml.stream.XMLStreamWriter}
 * as long as an element fragment written using the efficient streaming methods
 * are self-contained and no sub-fragment is written using methods from
 * {@link javax.xml.stream.XMLStreamWriter}.
 * <p>
 * The required call sequence is as follows:
 * <pre>
 * CALLSEQUENCE    := {@link DefaultHandler#startDocument startDocument}
 *                    initiateLowLevelWriting ELEMENT
 *                    {@link DefaultHandler#endDocument endDocument}
 *                 |  initiateLowLevelWriting ELEMENT   // for fragment
 *
 * ELEMENT         := writeLowLevelTerminationAndMark
 *                    NAMESPACES?
 *                    ELEMENT_NAME
 *                    ATTRIBUTES?
 *                    writeLowLevelEndStartElement
 *                    CONTENTS
 *                    writeLowLevelEndElement
 *
 * NAMESPACES      := writeLowLevelStartNamespaces
 *                    writeLowLevelNamespace*
 *                    writeLowLevelEndNamespaces
 *
 * ELEMENT_NAME    := writeLowLevelStartElementIndexed
 *                 |  writeLowLevelStartNameLiteral
 *                 |  writeLowLevelStartElement
 *
 * ATTRUBUTES      := writeLowLevelStartAttributes
 *                   (ATTRIBUTE_NAME writeLowLevelAttributeValue)*
 *
 * ATTRIBUTE_NAME  := writeLowLevelAttributeIndexed
 *                 |  writeLowLevelStartNameLiteral
 *                 |  writeLowLevelAttribute
 *
 *
 * CONTENTS      := (ELEMENT | writeLowLevelText writeLowLevelOctets)*
 * </pre>
 * <p>
 * Some methods defer to the application for the mapping of information
 * to indexes.
 */
public interface LowLevelFastInfosetStreamWriter {
    /**
     * Initiate low level writing of an element fragment.
     * <p>
     * This method must be invoked before other low level method.
     */
    void initiateLowLevelWriting()
    throws XMLStreamException;

    /**
     * Get the next index to apply to an Element Information Item.
     * <p>
     * This will increment the next obtained index such that:
     * <pre>
     * i = w.getNextElementIndex();
     * j = w.getNextElementIndex();
     * i == j + 1;
     * </pre>
     * @return the index.
     */
    int getNextElementIndex();

    /**
     * Get the next index to apply to an Attribute Information Item.
     * This will increment the next obtained index such that:
     * <pre>
     * i = w.getNextAttributeIndex();
     * j = w.getNextAttributeIndex();
     * i == j + 1;
     * </pre>
     * @return the index.
     */
    int getNextAttributeIndex();

    /**
     * Get the current index that was applied to an [local name] of an
     * Element or Attribute Information Item.
     *
     * @return the index.
     */
    int getLocalNameIndex();

    /**
     * Get the next index to apply to an [local name] of an Element or Attribute
     * Information Item.
     * This will increment the next obtained index such that:
     * <pre>
     * i = w.getNextLocalNameIndex();
     * j = w.getNextLocalNameIndex();
     * i == j + 1;
     * </pre>
     * @return the index.
     */
    int getNextLocalNameIndex();

    void writeLowLevelTerminationAndMark()
    throws IOException;

    void writeLowLevelStartElementIndexed(int type, int index)
    throws IOException;

    /**
     * Write the start of an element.
     *
     * @return true if element is indexed, otherwise false.
     */
    boolean writeLowLevelStartElement(int type,
                                      String prefix, String localName, String namespaceURI)
            throws IOException;

    void writeLowLevelStartNamespaces()
    throws IOException;

    void writeLowLevelNamespace(String prefix, String namespaceName)
        throws IOException;

    void writeLowLevelEndNamespaces()
    throws IOException;

    void writeLowLevelStartAttributes()
    throws IOException;

    void writeLowLevelAttributeIndexed(int index)
    throws IOException;

    /**
     * Write an attribute.
     *
     * @return true if attribute is indexed, otherwise false.
     */
    boolean writeLowLevelAttribute(
            String prefix, String namespaceURI, String localName)
            throws IOException;

    void writeLowLevelAttributeValue(String value)
    throws IOException;

    void writeLowLevelStartNameLiteral(int type,
                                       String prefix, byte[] utf8LocalName, String namespaceURI)
            throws IOException;

    void writeLowLevelStartNameLiteral(int type,
                                       String prefix, int localNameIndex, String namespaceURI)
            throws IOException;

    void writeLowLevelEndStartElement()
    throws IOException;

    void writeLowLevelEndElement()
    throws IOException;

    void writeLowLevelText(char[] text, int length)
    throws IOException;

    void writeLowLevelText(String text)
    throws IOException;

    void writeLowLevelOctets(byte[] octets, int length)
    throws IOException;
}
