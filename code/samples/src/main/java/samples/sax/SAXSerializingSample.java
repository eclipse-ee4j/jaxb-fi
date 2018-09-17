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

import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import org.xml.sax.helpers.AttributesImpl;


/** <p>Writing a FI document directly with SAXDocumentSerializer.</p>
 *  This sample demonstrates the use of SAXDocumentSerializer to write out an FI document
 *  with following content:
 * <ns1:invoice xmlns:ns1="http://www.sun.com/schema/spidermarkexpress/sm-inv">
 *   <Header>
 *     <IssueDateTime>2003-03-13T13:13:32-08:00</IssueDateTime>
 *     <Identifier schemeAgencyName="ISO" schemeName="Invoice">15570720</Identifier>
 *     <POIdentifier schemeName="Generic" schemeAgencyName="ISO">691</POIdentifier>
 *   </Header>
 * </ns1:invoice>
 *
 * You may use tool "fitosaxtoxml" provided in the FastInfoset package to verify the result.
 */

public class SAXSerializingSample {
    
    /** Creates a new instance of FastInfosetSerializer */
    public SAXSerializingSample() {
    }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            displayUsageAndExit();
        }
        
        try {
            File output = new File(args[0]);
            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(output));
            
            //create an instance of FastInfoset SAX Serializer and set output stream
            SAXDocumentSerializer s = new SAXDocumentSerializer();
            s.setOutputStream(fos);
            
            AttributesImpl attributesImpl = new AttributesImpl();
            String temp;
            
            //start FastInfoset document
            s.startDocument();
            
            //<ns1:invoice xmlns:ns1="http://www.sun.com/schema/spidermarkexpress/sm-inv">
            String namespaceURI = "http://www.sun.com/schema/spidermarkexpress/sm-inv";
            String prefix = "ns1";
            String localPart = "invoice";
            
            //namespace must be indexed before calling startElement
            s.startPrefixMapping(prefix, namespaceURI);
            s.startElement(namespaceURI, localPart, "ns1:invoice", attributesImpl);
            
            //  <Header>
            temp = "\n\t";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.startElement("", "header", "header", attributesImpl);
            
            //      <IssueDateTime>2003-03-13T13:13:32-08:00</IssueDateTime>
            temp = "\n\t\t";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.startElement("", "IssueDateTime", "IssueDateTime", attributesImpl);
            temp = "2003-03-13T13:13:32-08:00";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.endElement("", "IssueDateTime", "IssueDateTime");
            
            //      <Identifier schemeAgencyName="ISO" schemeName="Invoice">15570720</Identifier>
            temp = "\n\t\t";
            s.characters(temp.toCharArray(), 0, temp.length());
            attributesImpl.clear();
            attributesImpl.addAttribute("", "schemeAgencyName", "schemeAgencyName", "", "ISO");
            attributesImpl.addAttribute("", "schemeName", "schemeName", "", "Invoice");
            s.startElement("", "Identifier", "Identifier", attributesImpl);
            temp = "15570720";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.endElement("", "Identifier", "Identifier");
            
            //      <POIdentifier schemeName="Generic" schemeAgencyName="ISO">691</POIdentifier>
            temp = "\n\t\t";
            s.characters(temp.toCharArray(), 0, temp.length());
            attributesImpl.clear();
            attributesImpl.addAttribute("", "schemeName", "schemeName", "", "Generic");
            attributesImpl.addAttribute("", "schemeAgencyName", "schemeAgencyName", "", "ISO");
            s.startElement("", "POIdentifier", "POIdentifier", attributesImpl);
            temp = "691";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.endElement("", "POIdentifier", "POIdentifier");
            
            //  </Header>
            temp = "\n\t";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.endElement("", "header", "header");
            
            //</ns1:invoice>
            temp = "\n";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.endElement("", "invoice", "ns1:invoice");
            
            s.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void displayUsageAndExit() {
        System.err.println("Usage: ant FISAXSerializer or samples.sax.FastInfosetSerializer FI_output_file");
        System.exit(1);
    }
}
