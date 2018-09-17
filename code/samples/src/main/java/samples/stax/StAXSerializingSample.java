/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2011 Oracle and/or its affiliates. All rights reserved.
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

package samples.stax;

import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;


/** <p>Writing a FI document directly with StAXDocumentSerializer.</p>
 *  This sample demonstrates the use of StAXDocumentSerializer to write out an FI document
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

public class StAXSerializingSample {
    
    /** Creates a new instance of FastInfosetSerializer */
    public StAXSerializingSample() {
    }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            displayUsageAndExit();
        }
        
        try {
            File output = new File(args[0]);
            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(output));
            
            //create an instance of FastInfoset StAX Serializer and set output stream
            StAXDocumentSerializer s = new StAXDocumentSerializer();
            s.setOutputStream(fos);
            
            String temp = null;
            
            //start FastInfoset document
            s.writeStartDocument();
            
            //<ns1:invoice xmlns:ns1="http://www.sun.com/schema/spidermarkexpress/sm-inv">
            String namespaceURI = "http://www.sun.com/schema/spidermarkexpress/sm-inv";
            String prefix = "ns1";
            String localPart = "invoice";
            
            //namespace must be indexed before calling startElement
            
            s.writeStartElement(prefix, localPart, namespaceURI);
            s.writeNamespace(prefix, namespaceURI);
            s.setPrefix(prefix, namespaceURI);
            
            //  <Header>
            s.writeCharacters("\n\t");
            s.writeStartElement("header");
            
            //      <IssueDateTime>2003-03-13T13:13:32-08:00</IssueDateTime>
            s.writeCharacters("\n\t\t");
            s.writeStartElement("IssueDateTime");
            s.writeCharacters("2003-03-13T13:13:32-08:00");
            s.writeEndElement();
            
            //      <Identifier schemeAgencyName="ISO" schemeName="Invoice">15570720</Identifier>
            s.writeCharacters("\n\t\t");
            s.writeStartElement("Identifier");
            s.writeAttribute("schemeAgencyName", "ISO");
            s.writeAttribute("schemeName", "Invoice");
            s.writeCharacters("15570720");
            s.writeEndElement();
            
            //      <POIdentifier schemeName="Generic" schemeAgencyName="ISO">691</POIdentifier>
            s.writeCharacters("\n\t\t");
            s.writeStartElement("POIdentifier");
            s.writeAttribute("schemeName", "Generic");
            s.writeAttribute("schemeAgencyName", "ISO");
            s.writeCharacters("691");
            s.writeEndElement();
            
            //  </Header>
            s.writeCharacters("\n\t");
            s.writeEndElement();
            
            //</ns1:invoice>
            s.writeCharacters("\n");
            s.writeEndElement();
            
            s.writeEndDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void displayUsageAndExit() {
        System.err.println("Usage: ant FIStAXSerializer or samples.stax.FastInfosetSerializer FI_output_file");
        System.exit(1);
    }
}
