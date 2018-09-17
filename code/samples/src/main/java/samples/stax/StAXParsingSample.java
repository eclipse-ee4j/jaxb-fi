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

package samples.stax;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;
import samples.common.Util;


/** <p>This is a sample that demonstrates the use of FI StAX Cursor API.</p>
 *  The sample starts by creating an input stream from the input FI document. The input stream is
 *  then used to instantiating a new instance of FI StAX stream reader "StAXDocumentParser". The
 *  rest of the code simply uses the StreamReader to loop through
 *  the document and displays event types, names and etc.
 */
public class StAXParsingSample {
    
    /** Creates a new instance of Cursor */
    public StAXParsingSample() {
    }
    
    /** Starts the sample. The sample takes a FI document that will
     * be read using StAXDocumentParser in the fastinfoset package.
     *
     *  @param args a FI document.
     */
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 1) {
            displayUsageAndExit();
        }
        
        if (args[0] == null) {
            displayUsageAndExit();
        }

        StAXParsingSample streamReader = new StAXParsingSample();
        streamReader.parse(args[0]);
    }
    
    /** Reads a FI document using StAXDocumentParser and displays event types and contents.
     *
     *  @param filename FI document name.
     */
    @SuppressWarnings("CallToThreadDumpStack")
    public void parse(String filename) {
        InputStream document;
        XMLStreamReader streamReader = null;
        try {
            document = new BufferedInputStream(new FileInputStream(filename));
            streamReader = new StAXDocumentParser(document);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Reading " + new File(filename) + ": \n");
        long starttime = System.currentTimeMillis();
        try {
            int eventType;
            while (streamReader != null && streamReader.hasNext()) {
                eventType = streamReader.next();
                Util.printEventType(eventType);
                Util.printName(streamReader, eventType);
                Util.printText(streamReader);
                if (streamReader.isStartElement()) {
                    Util.printAttributes(streamReader);
                }
                Util.printPIData(streamReader);
                System.out.println("-----------------------------");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long endtime = System.currentTimeMillis();
        System.out.println(" Parsing Time = " + (endtime - starttime));

    }
    
    private static void displayUsageAndExit() {
        System.err.println("Usage: ant StreamReader or samples.stax.StreamReader FI_file");
        System.exit(1);
    }
    
}
