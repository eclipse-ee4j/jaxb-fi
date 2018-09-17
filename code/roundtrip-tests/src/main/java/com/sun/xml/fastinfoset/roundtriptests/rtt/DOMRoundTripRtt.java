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

import com.sun.xml.fastinfoset.tools.FI_DOM_Or_XML_DOM_SAX_SAXEvent;
import com.sun.xml.fastinfoset.tools.XML_DOM_FI;
import java.io.File;

/**
 * @author Alexey Stashok
 */
public class DOMRoundTripRtt extends RoundTripRtt {
    
    public boolean process(File testFile) throws Exception {
        String absolutePath = testFile.getAbsolutePath();
        
        String fiOutputFileName = absolutePath + ".dom.finf";
        String saxOutputFileName = absolutePath + ".sax-event";
        String fiSaxOutputFileName = absolutePath + ".dom.finf.sax-event";
        String diffOutputFileName = absolutePath + ".dom.sax-event.diff";
        
        transform(testFile.getAbsolutePath(), fiOutputFileName, testFile.getParent(), new XML_DOM_FI());
        transform(testFile.getAbsolutePath(), saxOutputFileName, testFile.getParent(), new FI_DOM_Or_XML_DOM_SAX_SAXEvent());
        transform(fiOutputFileName, fiSaxOutputFileName, new FI_DOM_Or_XML_DOM_SAX_SAXEvent());
        
        return diffText(saxOutputFileName, fiSaxOutputFileName, diffOutputFileName);
    }
    
    public String getName() {
        return "domroundtrip";
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Source file or directory wasn't specified!");
            System.exit(0);
        }
        
        File file = new File(args[0]);
        DOMRoundTripRtt rtt = new DOMRoundTripRtt();
        rtt.process(file);
    }
}
