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


package samples.common;


import java.io.File;
import samples.transform.*;


/** <p>Converts XML files into FastInfoset documents.</p>
 *  This utility class uses samples.transform.XMLToFastInfosetStAXSerializer to 
 *  transform XML files into FastInfoset documents.
 *  Since only XML files are packaged in the sample package, thie utility is called 
 *  in samples that require FI documents to create FI documents from XML files.
 */

public class XMLToFastInfoset {
    
    /** Create FI documents from XML files
     *
     * @param args XML files
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            displayUsageAndExit();
        }
        try {
            XMLToFastInfosetSAXSerializer docSerializer = new XMLToFastInfosetSAXSerializer();
            for (int i=0; i<args.length; i++) {
                //XML input file, such as ./data/inv100.xml
                File input = new File(args[i]);
                //FastInfoset output file, such as ./data/inv100_sax.finf.
                String finf = getFinfFilename(args[i]);
                File ouput = new File(finf);
                docSerializer.write(input, ouput);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getFinfFilename(String xmlFilename) {
        int ext = xmlFilename.lastIndexOf(".");
        return xmlFilename.substring(0, ext) + ".finf";
    }

    private static void displayUsageAndExit() {
        System.err.println("Usage: XMLToFastInfoset XMLfiles+");
        System.exit(1);        
    }
    
}
