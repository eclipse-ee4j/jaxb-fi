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

package samples.sax;

import org.xml.sax.helpers.DefaultHandler;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** <p>Parses a FI document using SAXDocumentParser.</p>
 *  The sample parses data/inv1a.finf as specified in the build.xml and handles SAX events
 *  to display corresponding content in XML format. The sample class entends DefaultHandler
 *  interface making itself a ContentHandler. Since the FastInfoset SAX package does not
 *  implement factory, a document parser is instantiated directly. The sample class implements
 *  some major event-handling methods as defined in the ContentHandler,  such as startDocument,
 *  endDocument, startElement,
 *  endElement, and characters. In this sample, these methods simply write out the content
 *  to System.out.
 */
public class SAXParsingSample extends DefaultHandler {
    static private Writer  out;
    
    StringBuffer textBuffer;
    
    /** Starts the sample. The sample takes a FI document and parses it using SAXDocumentParser in
     * the fastinfoset package
     *
     * @param argv FI document filename
     */
    @SuppressWarnings("CallToThreadDumpStack")
    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Usage: FastInfosetParser filename");
            System.exit(1);
        }
        
        // instantiate event handler
        DefaultHandler handler = new SAXParsingSample();
        
        try {
            // Set up output stream
            out = new OutputStreamWriter(System.out, "UTF8");
            
            // Create an inputstream
            InputStream in = new BufferedInputStream(new FileInputStream(argv[0]));
            
            // Instantiate a FI parser
            SAXDocumentParser parser = new SAXDocumentParser();
            parser.setContentHandler(handler);
            // Start parsing
            parser.parse(in);
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(0);
    }
    
    /** Creates a new instance of FIParser */
    public SAXParsingSample() {
    }
    
    //===========================================================
    // SAX DocumentHandler methods
    //===========================================================
    /** Handles startDocument event and prints out file header.
     *
     */
    @Override
    public void startDocument() throws SAXException {
        display("<?xml version='1.0' encoding='UTF-8'?>");
        displayNewLine();
    }
    
    /** Handles endDocument event
     *
     */
    @Override
    public void endDocument() throws SAXException {
        try {
            displayNewLine();
            out.flush();
        } catch (IOException e) {
            throw new SAXException("I/O error", e);
        }
    }
    
    /** Handles startElement event. Prints out any text accumulated
     *  from the characters event before the element, the element name, and
     *  attributes if any.
     *
     *  @param namespaceURI namespace URI
     *  @param localName The local name (without prefix), or the empty string if Namespace processing is not being performed.
     *  @param qName The qualified name (with prefix), or the empty string if qualified names are not available.
     *  @param attributes The specified or defaulted attributes.
     */
    @Override
    public void startElement(String namespaceURI, String localName,
            String qName, Attributes attributes) throws SAXException {
        
        // Print out text accumulated from the characters event
        flushText();
        
        String name;
        
        if (localName.equals("")) {
            name = qName;
        } else {
            name = localName;
        }
        
        // Print out element name and attributes if any
        display("<" + name);
        
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                String aName = attributes.getLocalName(i);
                
                if ("".equals(aName)) {
                    aName = attributes.getQName(i);
                }
                
                display(" " + aName + "=\"" + attributes.getValue(i) + "\"");
            }
        }
        
        display(">");
    }
    
    /** Handles endElement event. Prints out any text accumulated
     *  from the characters event within the element, and then the end element name.
     *
     *  @param namespaceURI namespace URI
     *  @param localName The local name (without prefix), or the empty string if Namespace processing is not being performed.
     *  @param qName The qualified name (with prefix), or the empty string if qualified names are not available.
     */
    @Override
    public void endElement(String namespaceURI, String localName, String qName )
    throws SAXException {
        
        // Print out text accumulated from the characters event
        flushText();
        
        String name = localName;
        
        if ("".equals(name)) {
            name = qName;
        }
        
        // Print out end element
        display("</" + name + ">");
    }
    
    /** Handles characters event. Save the text in a buffer for print-out in start/end element.
     *
     *  @param ch The characters
     *  @param start The start position in the character array.
     *  @param length The number of characters to use from the character array.
     */
    @Override
    public void characters(char[] ch, int start, int length)
    throws SAXException {
        String s = new String(ch, start, length);
        
        if (textBuffer == null) {
            textBuffer = new StringBuffer(s);
        } else {
            textBuffer.append(s);
        }
    }
    
    //===========================================================
    // Utility Methods ...
    //===========================================================
    // Display text accumulated in the character buffer
    private void flushText() throws SAXException {
        if (textBuffer == null) {
            return;
        }
        
        display(textBuffer.toString());
        textBuffer = null;
    }
    
    // Wrap I/O exceptions in SAX exceptions, to
    // suit handler signature requirements
    private void display(String s) throws SAXException {
        try {
            out.write(s);
            out.flush();
        } catch (IOException e) {
            throw new SAXException("I/O error", e);
        }
    }
    
    // Displays a new line
    private void displayNewLine() throws SAXException {
        try {
            out.write(System.getProperty("line.separator"));
        } catch (IOException e) {
            throw new SAXException("I/O error", e);
        }
    }
}
