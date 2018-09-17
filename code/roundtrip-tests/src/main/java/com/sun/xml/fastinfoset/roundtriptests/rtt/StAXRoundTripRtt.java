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

import com.sun.xml.fastinfoset.tools.FI_SAX_Or_XML_SAX_SAXEvent;
import com.sun.xml.fastinfoset.tools.FI_StAX_SAX_Or_XML_SAX_SAXEvent;
import com.sun.xml.fastinfoset.tools.XML_SAX_StAX_FI;
import java.io.File;

/**
 * @author Alexey Stashok
 */
public class StAXRoundTripRtt extends RoundTripRtt {
    
    public boolean process(File file) throws Exception {
        String absolutePath = file.getAbsolutePath();
        
        String fiOutputFileName = absolutePath + ".stax.finf";
        String saxOutputFileName = absolutePath + ".sax-event";
        String fiStaxOutputFileName = absolutePath + ".stax.finf.sax-event";
        String diffOutputFileName = absolutePath + ".stax.sax-event.diff";

        transform(file.getAbsolutePath(), fiOutputFileName, file.getParent(), new XML_SAX_StAX_FI());
        transform(file.getAbsolutePath(), saxOutputFileName, file.getParent(), new FI_SAX_Or_XML_SAX_SAXEvent());
        transform(fiOutputFileName, fiStaxOutputFileName, new FI_StAX_SAX_Or_XML_SAX_SAXEvent());
        
        return diffText(saxOutputFileName, fiStaxOutputFileName, diffOutputFileName);
    }
    
    public String getName() {
        return "staxroundtrip";
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Source file or directory wasn't specified!");
            System.exit(0);
        }
        
        File file = new File(args[0]);
        StAXRoundTripRtt rtt = new StAXRoundTripRtt();
        rtt.process(file);
    }
}
