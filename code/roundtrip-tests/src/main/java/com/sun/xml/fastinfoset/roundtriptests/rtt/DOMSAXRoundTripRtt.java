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

package com.sun.xml.fastinfoset.roundtriptests.rtt;

import com.sun.xml.fastinfoset.tools.FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent;
import com.sun.xml.fastinfoset.tools.XML_DOM_SAX_FI;
import java.io.File;

/**
 * @author Alexey Stashok
 */
public class DOMSAXRoundTripRtt extends RoundTripRtt {
    
    public boolean process(File file) throws Exception {
        String absolutePath = file.getAbsolutePath();
        
        String fiOutputFileName = absolutePath + ".dom.sax.finf";
        String domSaxOutputFileName = absolutePath + ".dom.sax-event";
        String fiDomSaxOutputFileName = absolutePath + ".dom.sax.finf.sax-event";
        String diffOutputFileName = absolutePath + ".dom.sax.sax-event.diff";

        transform(file.getAbsolutePath(), fiOutputFileName, file.getParent(), new XML_DOM_SAX_FI());
        transform(file.getAbsolutePath(), domSaxOutputFileName, file.getParent(), new FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent());
        transform(fiOutputFileName, fiDomSaxOutputFileName, new FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent());
        
        return diffText(domSaxOutputFileName, fiDomSaxOutputFileName, diffOutputFileName);
    }
    
    public String getName() {
        return "domsaxroundtrip";
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Source file or directory wasn't specified!");
            System.exit(0);
        }
        
        File file = new File(args[0]);
        DOMSAXRoundTripRtt rtt = new DOMSAXRoundTripRtt();
        rtt.process(file);
    }
}
