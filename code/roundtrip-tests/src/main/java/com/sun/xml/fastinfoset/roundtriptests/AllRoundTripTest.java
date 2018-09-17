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

package com.sun.xml.fastinfoset.roundtriptests;

import com.sun.xml.fastinfoset.roundtriptests.rtt.DOMRoundTripRtt;
import com.sun.xml.fastinfoset.roundtriptests.rtt.DOMSAXRoundTripRtt;
import com.sun.xml.fastinfoset.roundtriptests.rtt.RoundTripRtt;
import com.sun.xml.fastinfoset.roundtriptests.rtt.SAXRoundTripRtt;
import com.sun.xml.fastinfoset.roundtriptests.rtt.SAXStAXDiffRtt;
import com.sun.xml.fastinfoset.roundtriptests.rtt.StAXRoundTripRtt;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * @author Alexey Stashok
 */
public class AllRoundTripTest {
    
    private RoundTripRtt[] roundTripTests = new RoundTripRtt[] {
        new SAXRoundTripRtt(), new StAXRoundTripRtt(), new DOMRoundTripRtt(),
        new DOMSAXRoundTripRtt(), new SAXStAXDiffRtt()};
    
    public void processAllRttTests(String testSrc, String report) {
        File testSrcFile = new File(testSrc);
        RoundTripReport reporter = new RoundTripReport();
        for (int i=0; i<roundTripTests.length; i++) {
            SingleRountTripTest roundTripTest = new SingleRountTripTest(roundTripTests[i], reporter);
            roundTripTest.processFileOrFolder(testSrcFile);
        }
        
        cleanupDiffs(testSrcFile);
        
        try {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(report)));
            try {
                writer.print(reporter.generateReport());
            } finally {
                writer.close();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Error generating report!");
        }
    }
    
    private void cleanupDiffs(File testSrcFile) {
        if (testSrcFile.isFile()) {
            testSrcFile = testSrcFile.getParentFile();
        }
        
        FileUtils.removeFileRecursivelly(testSrcFile, new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return !file.getName().equals(".") && !file.getName().equals("..");
                } else if (file.isFile()) {
                    String filename = file.getName();
                    return filename.endsWith(".finf") || filename.endsWith(".sax-event") ||
                            filename.indexOf(".diff") != -1;
                }
                return false;
            }
        });
    }
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Not enough parameters!!!");
            System.out.println("Use AllRoundTripTest <test_src> <report_filename>");
            System.out.println("Where <test_src> - testing file or folder");
            System.out.println("<report_filename> - file for generating report");
            System.exit(0);
        }
        
        new AllRoundTripTest().processAllRttTests(args[0], args[1]);
    }
}
