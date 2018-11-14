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

package samples.transform;

import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.w3c.dom.Document;


/** <p>Serializes an XML input stream into FI document using
 *  SAXDocumentSerializer defined in the fastinfoset.sax package.</p>
 *  The sample demonstrates how to use SAXDocumentSerializer as a SAX handler and JAXP
 *  transformer to convert an XML file into a FI document. As shown in the sample,
 *  transforming a DOM source to SAX Result involves very little coding. However, the process
 *  may not be efficient due to the construction of DOM source.
 *
 *  In the sample, a DOMSource is constructed out of an XML file input (see method getDOMSource)
 *  and a SAXResult instantiated using an instance of SAXDocumentSerializer as the
 *  handler which takes a FI document as OutputStream (see method getSAXResult).
 *  The sample then calls transformer's tranform method to convert the XML file into the FI
 *  document.
 */
@SuppressWarnings("CallToThreadDumpStack")
public class XMLToFastInfosetSAXSerializer {
    Transformer _transformer;
    DocumentBuilder _docBuilder;
    DOMSource _source = null;
    SAXResult _result = null;
    
    /** Creates a new instance of FISerializer */
    public XMLToFastInfosetSAXSerializer() {
        try {
            // get a transformer and document builder
            _transformer = TransformerFactory.newInstance().newTransformer();
            _docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /** Construct a DOMSource with a file.
     *
     *  @param input the XML file input
     */
    void getDOMSource(File input) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(input);
            Document document = _docBuilder.parse(fis);
            fis.close();
            _source = new DOMSource(document);
        } catch (Exception e) {
            if (fis != null) {
                fis.close();
            }
            e.printStackTrace();
        }
        
    }
    
    /** Initialize a SAXResult and set its handers.
     *
     *  @param output FI document output
     */
    void getSAXResult(File output) {
        try {
            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(output));
            SAXDocumentSerializer serializer = new SAXDocumentSerializer();
            serializer.setOutputStream(fos);
            
            _result = new SAXResult();
            _result.setHandler(serializer);
            _result.setLexicalHandler(serializer);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Transform an XML file into a FI document.
     *
     *  @param input an XML file input
     *  @param output the FI document output
     */
    public void write(File input, File output) throws IOException {
        // construct a DOMSource from the input file
        getDOMSource(input);
        // Initialize a SAXResult object
        getSAXResult(output);
        
        if (_source != null && _result != null) {
            try {
                System.out.println("Transforming "+input.getName()+ " into " + output.getName());
                // Transform the XML input file into a FI document
                _transformer.transform(_source, _result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("\ndone.");
        } else {
            System.out.println("Source or Result could not be null.");
        }
    }
    
    /** Starts the sample
     * @param args XML input file name and FI output document name
     */
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 4) {
            displayUsageAndExit();
        }
        
        try {
            //XML input file, such as ./data/inv100.xml
            File input = new File(args[0]);
            //FastInfoset output file, such as ./data/inv100_sax.finf.
            File ouput = new File(args[1]);
            XMLToFastInfosetSAXSerializer docSerializer = new XMLToFastInfosetSAXSerializer();
            docSerializer.write(input, ouput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void displayUsageAndExit() {
        System.err.println("Usage: ant FISAXSerialixer or samples.sax.FISerializer XML_input_file FI_output_file");
        System.exit(1);
    }
    
}
