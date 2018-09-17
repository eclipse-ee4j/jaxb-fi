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
package com.sun.xml.fastinfoset.streambuffer.tools;

import com.sun.xml.analysis.types.SchemaProcessor;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import com.sun.xml.fastinfoset.streambuffer.FastInfosetReaderSAXBufferCreator;
import com.sun.xml.fastinfoset.streambuffer.FastInfosetWriterSAXBufferProcessor;
import com.sun.xml.fastinfoset.streambuffer.TypedSAXBufferCreator;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import org.jvnet.fastinfoset.sax.FastInfosetReader;
import org.jvnet.fastinfoset.sax.FastInfosetWriter;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class RoundTrip {
    
    public static void main(String[] args) throws Exception {
        SchemaProcessor sp = new SchemaProcessor(new File(args[0]).toURL());
        sp.process();
        
        // Create buffer from XML document, convert lexical space to
        // value space
        XMLStreamBuffer source = TypedSAXBufferCreator.create(
                sp.getElementToXSDataTypeMap(),
                sp.getAttributeToXSDataTypeMap(),
                new BufferedInputStream(new FileInputStream(args[1])));
        
        // Serialize buffer to fast infoset document
        ByteArrayOutputStream fiDocumentOut = new ByteArrayOutputStream();
        FastInfosetWriter fiWriter = new SAXDocumentSerializer();
        fiWriter.setOutputStream(fiDocumentOut);
        FastInfosetWriterSAXBufferProcessor p = new FastInfosetWriterSAXBufferProcessor(source);
        p.process(fiWriter);
        
        // Parse fast infoset document to buffer
        ByteArrayInputStream fiDocumentIn = new ByteArrayInputStream(fiDocumentOut.toByteArray());
        FastInfosetReader fiReader = new SAXDocumentParser();
        FastInfosetReaderSAXBufferCreator c = new FastInfosetReaderSAXBufferCreator();
        MutableXMLStreamBuffer sink = c.create(fiReader, fiDocumentIn);
        
        assert sink != null;
    }
}
