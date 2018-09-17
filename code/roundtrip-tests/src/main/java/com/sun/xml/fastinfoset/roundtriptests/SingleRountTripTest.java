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

package com.sun.xml.fastinfoset.roundtriptests;

import com.sun.xml.fastinfoset.roundtriptests.rtt.RoundTripRtt;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * @author Alexey Stashok
 */
public class SingleRountTripTest {
    
    private RoundTripReport report;
    private RoundTripRtt test;
    
    public SingleRountTripTest(RoundTripRtt test, RoundTripReport report) {
        this.test = test;
        this.report = report;
    }
    
    public void processRtt(File srcFile) {
        boolean passed;
        try {
            System.out.println(test.getName() + ": " + srcFile.getAbsolutePath());
            passed = test.process(srcFile);
        } catch (Exception ex) {
            passed = false;
            System.err.println("Exception occured when processing file: " + srcFile.getAbsolutePath() + " test: " + test.getClass().getName());
            ex.printStackTrace();
        }
        
        String srcFolder = srcFile.getParent() != null ? srcFile.getParent() : ".";
        report.addResult(test.getName(), passed, srcFolder, srcFile.getName());
        
        String passedStr = passed ? "passed" : "failed";
        System.out.println(passedStr.toUpperCase(Locale.US));        
    }
    
    public void processFileOrFolder(File srcFile) {
        FileUtils.processFileOrDirectoryRecursivelly(srcFile, new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return !file.getName().equals(".") && !file.getName().equals("..");
                } else if (file.isFile()) {
                    return file.getName().endsWith(".xml");
                }
                return false;
            }}, new FileUtils.FileProcessorHandler() {
                public void handle(final File file) {
                    processRtt(file);
                }
            });
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: SingleRountTripTest <RoundTripRtt classname> <src file or directory> <report_filename>");
            System.exit(0);
        }
        
        RoundTripReport report = new RoundTripReport();
        new SingleRountTripTest((RoundTripRtt) Class.forName(args[0]).newInstance(), report).processFileOrFolder(new File(args[1]));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[2])));
        try {
            writer.print(report.generateReport());
        } finally {
            writer.close();
        }
    }
}
