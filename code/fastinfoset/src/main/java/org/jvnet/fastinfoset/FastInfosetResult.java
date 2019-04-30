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

package org.jvnet.fastinfoset;

import java.io.OutputStream;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import javax.xml.transform.sax.SAXResult;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;

/**
 *  A JAXP Result implementation that supports the serialization to fast
 *  infoset documents for use by applications that expect a Result.
 *
 *  <P>The derivation of FIResult from SAXResult is an implementation
 *  detail.<P>
 *
 *  <P>This implementation is designed for interoperation with JAXP and is not
 *  not designed with performance in mind. It is recommended that for performant
 *  interoperation alternative serializer specific solutions be used.<P>
 *
 *  <P>General applications shall not call the following methods:
 *   <UL>
 *     <LI>setHandler</LI>
 *     <LI>setLexicalHandler</LI>
 *     <LI>setSystemId</LI>
 *   </UL>
 */
public class FastInfosetResult extends SAXResult {

    OutputStream _outputStream;

    public FastInfosetResult(OutputStream outputStream) {
        _outputStream = outputStream;
    }

    public ContentHandler getHandler() {
        ContentHandler handler = super.getHandler();
        if (handler == null) {
            handler = new SAXDocumentSerializer();
            setHandler(handler);
        }
        ((SAXDocumentSerializer) handler).setOutputStream(_outputStream);
        return handler;
    }

    public LexicalHandler getLexicalHandler() {
        return (LexicalHandler) getHandler();
    }

    public OutputStream getOutputStream() {
        return _outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        _outputStream = outputStream;
    }
}
