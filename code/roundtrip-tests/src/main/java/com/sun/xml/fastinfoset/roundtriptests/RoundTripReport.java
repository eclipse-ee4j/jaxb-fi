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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoundTripReport {
    static final int INDEX_HOME = 0;    //e.g. /projects/RountTripTests/data
    static final int INDEX_REPORT = 1;    //e.g.   report/report.html
    static final int INDEX_TESTCASE = 2;    //e.g 011.xml
    static final int INDEX_TESTCASEPATH = 3;  //e.g. /projects/RountTripTests/data/xmlconf/xmltest
    static final int INDEX_TESTNAME = 4;  //e.g. saxroundtrip
    static final int INDEX_RESULT = 5;
    static final String COUNT_DEFAULT = "N/A";
    static final String COUNT_SAXPASSED = "<!--saxroundtrip_passed-->";
    static final String COUNT_SAXFAILED = "<!--saxroundtrip_failed-->";
    static final String COUNT_STAXPASSED = "<!--staxroundtrip_passed-->";
    static final String COUNT_STAXFAILED = "<!--staxroundtrip_failed-->";
    static final String COUNT_DOMPASSED = "<!--domroundtrip_passed-->";
    static final String COUNT_DOMFAILED = "<!--domroundtrip_failed-->";
    static final String COUNT_DOMSAXPASSED = "<!--domsaxroundtrip_passed-->";
    static final String COUNT_DOMSAXFAILED = "<!--domsaxroundtrip_failed-->";
    static final String COUNT_SAXSTAXPASSED = "<!--saxstaxdiff_passed-->";
    static final String COUNT_SAXSTAXFAILED = "<!--saxstaxdiff_failed-->";
    static final String RESULT_PASSED = "passed";
    static final String RESULT_FAILED = "failed";
    static final String REPORTCOUNT_TOTAL = "<!--{TOTAL}-->";
    static final String REPORT_NEWROW = "<!--{new row}-->";
    static final String TEST_SAX = "saxroundtrip";
    static final String TEST_STAX = "staxroundtrip";
    static final String TEST_DOM = "domroundtrip";
    static final String TEST_DOMSAX = "domsaxroundtrip";
    static final String TEST_SAXSTAX = "saxstaxdiff";
    
    static final List<String> RTT_NAMES = new ArrayList(Arrays.<String>asList(new String[] {TEST_SAX, TEST_STAX, TEST_DOM, TEST_DOMSAX, TEST_SAXSTAX}));
    static final int SAX_RTT = 0;
    static final int STAX_RTT = 1;
    static final int DOM_RTT = 2;
    static final int DOM_SAX_RTT = 3;
    static final int SAX_STAX_RTT = 4;
    
    private int[] passed = new int[RTT_NAMES.size()];
    private int[] failed = new int[RTT_NAMES.size()];
    
    private Map<String, FailedTestRecord> failedTests = new HashMap<String, FailedTestRecord>();
    
    /** Creates a new instance of RoundTripReport */
    public RoundTripReport() {
    }
    
    public void addResult(String rttName, boolean passed, String testFolder, String testFile) {
        int rttIndex = RTT_NAMES.indexOf(rttName);
        if (rttIndex == -1) {
            // Unknown RTT
            return;
        }
        
        if (passed) {
            incPassed(rttIndex);
        } else {
            incFailed(rttIndex);
            addFailedRecord(testFolder, testFile, rttIndex);
        }
    }
    
    public String generateReport() {
        return generateReport(passed, failed, failedTests);
    }

    private void incPassed(int rtt) {
        passed[rtt]++;
    }

    private void incFailed(int rtt) {
        failed[rtt]++;
    }
    
    private void addFailedRecord(String testPath, String testName, int rtt) {
        String testFullName = testPath + "/" + testName;
        
        FailedTestRecord failTestRecord = failedTests.get(testFullName);
        
        if (failTestRecord == null) {
            failTestRecord = new FailedTestRecord(testName);
            failedTests.put(testFullName, failTestRecord);
        }
        
        failTestRecord.setFailedFor(rtt);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length != 6) {
            displayUsageAndExit(args);
        }
        new RoundTripReport().report(args);
        
        
    }
    public void report(String[] args) {
        OutputStreamWriter osr = null;
        try {
            //String filename = args[INDEX_REPORT];
            String filename = args[INDEX_HOME]+"/"+args[INDEX_REPORT];
            String content = reportContent(filename, args);
            osr = new OutputStreamWriter(
                    new FileOutputStream(
                    new File(filename)));
            osr.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (osr != null) {
                try {
                    osr.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
    public String reportContent(String filename, String[] args) {
        File file = new File(filename);
        StringBuffer content = new StringBuffer();
        if (file.exists()) {
            content.append(readFromFile(file));
        } else {
            content.append(getTemplate());
        }
        
        //count test result
        countIncrement("<!--"+args[INDEX_TESTNAME]+"_"+args[INDEX_RESULT]+"-->", content);
        int testcaseStart = content.indexOf(args[INDEX_TESTCASE]);
//        if (testcaseStart < 0) {
//            countIncrement(REPORTCOUNT_TOTAL, content);
//        }
        //
        int start = 0;
        int end = 0;
        String newrow = null;
        
        if (args[INDEX_RESULT].equals(RESULT_FAILED)) {
            if (testcaseStart < 0) {
                newrow = "<tr><td><a href="+args[INDEX_TESTCASEPATH].substring(args[INDEX_HOME].length()+1)+"/"+args[INDEX_TESTCASE]+">"+
                        args[INDEX_TESTCASE]+"</a></td>\n"+
                        "<td><!--"+TEST_SAX+"--></td>\n"+
                        "<td><!--"+TEST_STAX+"--></td>\n"+
                        "<td><!--"+TEST_DOM+"--></td>\n"+
                        "<td><!--"+TEST_DOMSAX+"--></td>\n"+
                        "<td><!--"+TEST_SAXSTAX+"--></td></tr>\n"+
                        REPORT_NEWROW+"\n";
                start = content.indexOf(REPORT_NEWROW);
                end = start + REPORT_NEWROW.length();
                content.replace(start, end, newrow);
                testcaseStart = start;
            }
            
            String testname = "<!--"+args[INDEX_TESTNAME]+"-->";
            start = content.indexOf(testname, testcaseStart);
            if (start>0) {
                end = start + testname.length();
                content.replace(start, end, args[INDEX_RESULT]);
            }
        }
        return content.toString();
    }
    
    private String readFromFile(File file) {
        BufferedReader in = null;
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fstream = new FileInputStream(file);
            
            // Convert our input stream to a
            // DataInputStream
            in = new BufferedReader(new InputStreamReader(fstream));
            
            String s;
            while ((s = in.readLine()) != null) {
                sb.append(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
        return sb.toString();
    }
    private boolean countIncrement(String tag, StringBuffer content) {
        try {
            int start = content.indexOf(tag);
            int end = content.indexOf(tag, start+1);
            
            String temp = content.substring(start+tag.length(), end);
            int count = 1;
            if (!temp.equals(COUNT_DEFAULT)) {
                count = Integer.parseInt(temp) + 1;
            }
            temp = tag + count + tag;
            content.replace(start, end+tag.length(), temp);
            return true;
        } catch (Exception e) {
            return false;
        }
        
    }
    private String getTemplate() {
        return generateReport(new int[5], new int[5], Collections.<String, FailedTestRecord>emptyMap());
    }
    
    private static String generateReport(int[] passed, int[] failed, Map<String, FailedTestRecord> failedTests) {
        StringBuilder report = new StringBuilder();
        report.append("<html>\n<body>\n<b>Roundtrip Tests</b><br><br>");
        report.append("<b>Summary</b>");
        report.append("<table width=\"80%\" border=1>\n");
        report.append("<tr><th></th><th>SAX</th><th>StAX</th><th>DOM</th><th>DOM-SAX</th><th>SAX-StAX</th></tr>\n");
        report.append("<tr><td>Passed</td>\n")
                .append("<td>").append(COUNT_SAXPASSED).append(getReportValue(passed[SAX_RTT], failed[SAX_RTT])).append(COUNT_SAXPASSED).append("</td>\n")
                .append("<td>").append(COUNT_STAXPASSED).append(getReportValue(passed[STAX_RTT], failed[STAX_RTT])).append(COUNT_STAXPASSED).append("</td>\n")
                .append("<td>").append(COUNT_DOMPASSED).append(getReportValue(passed[DOM_RTT], failed[DOM_RTT])).append(COUNT_DOMPASSED).append("</td>\n")
                .append("<td>").append(COUNT_DOMSAXPASSED).append(getReportValue(passed[DOM_SAX_RTT], failed[DOM_SAX_RTT])).append(COUNT_DOMSAXPASSED).append("</td>\n")
                .append("<td>").append(COUNT_SAXSTAXPASSED).append(getReportValue(passed[SAX_STAX_RTT], failed[SAX_STAX_RTT])).append(COUNT_SAXSTAXPASSED).append("</td></tr>\n");
        report.append("<tr><td>Failed</td>\n")
                .append("<td>").append(COUNT_SAXFAILED).append(getReportValue(failed[SAX_RTT], passed[SAX_RTT])).append(COUNT_SAXFAILED).append("</td>\n")
                .append("<td>").append(COUNT_STAXFAILED).append(getReportValue(failed[STAX_RTT], passed[STAX_RTT])).append(COUNT_STAXFAILED).append("</td>\n")
                .append("<td>").append(COUNT_DOMFAILED).append(getReportValue(failed[DOM_RTT], passed[DOM_RTT])).append(COUNT_DOMFAILED).append("</td>\n")
                .append("<td>").append(COUNT_DOMSAXFAILED).append(getReportValue(failed[DOM_SAX_RTT], passed[DOM_SAX_RTT])).append(COUNT_DOMSAXFAILED).append("</td>\n")
                .append("<td>").append(COUNT_SAXSTAXFAILED).append(getReportValue(failed[SAX_STAX_RTT], passed[SAX_STAX_RTT])).append(COUNT_SAXSTAXFAILED).append("</td></tr>\n");
        report.append("<tr><td>Total</td>\n")
                .append("<td>").append(getReportValue(failed[SAX_RTT] + passed[SAX_RTT])).append("</td>\n")
                .append("<td>").append(getReportValue(failed[STAX_RTT] + passed[STAX_RTT])).append("</td>\n")
                .append("<td>").append(getReportValue(failed[DOM_RTT] + passed[DOM_RTT])).append("</td>\n")
                .append("<td>").append(getReportValue(failed[DOM_SAX_RTT] + passed[DOM_SAX_RTT])).append("</td>\n")
                .append("<td>").append(getReportValue(failed[SAX_STAX_RTT] + passed[SAX_STAX_RTT])).append("</td></tr>\n");
        report.append(REPORTCOUNT_TOTAL);
        report.append("</table>\n");
        report.append("<br><b>Failed List</b><br><table width=\"80%\" border=1>\n");
        report.append("<tr><th>Name of Testcase</th><th>SAX</th><th>StAX</th><th>DOM</th><th>DOM-SAX</th><th>SAX-StAX</th></tr>\n");
        for(Map.Entry<String, FailedTestRecord> entry : failedTests.entrySet()) {
            final String testFullPath = entry.getKey();
            FailedTestRecord failedRecord = entry.getValue();
            String newrow = "<tr><td><a href="+testFullPath+">"+
                    failedRecord.testName+"</a></td>\n"+
                    "<td><!--"+TEST_SAX+"-->" + getResultAsString(!failedRecord.isFailedFor(SAX_RTT))+"</td>\n"+
                    "<td><!--"+TEST_STAX+"-->" +getResultAsString(!failedRecord.isFailedFor(STAX_RTT))+"</td>\n"+
                    "<td><!--"+TEST_DOM+"-->" +getResultAsString(!failedRecord.isFailedFor(DOM_RTT))+"</td>\n"+
                    "<td><!--"+TEST_DOMSAX+"-->" +getResultAsString(!failedRecord.isFailedFor(DOM_SAX_RTT))+"</td>\n"+
                    "<td><!--"+TEST_SAXSTAX+"-->" +getResultAsString(!failedRecord.isFailedFor(SAX_STAX_RTT))+"</td></tr>\n";
            report.append(newrow);
        }
        report.append(REPORT_NEWROW+"\n");
        report.append("</table>\n");
        report.append("</body>\n</html>");
        return report.toString();
    }
    
    private static String getResultAsString(boolean result) {
        return result ? "" : "failed";
    }
    private static String getReportValue(int value) {
        return getReportValue(value, value);
    }
    
    private static String getReportValue(int value, int oppositeResultValue) {
        return (value == 0 && oppositeResultValue == 0) ? COUNT_DEFAULT : String.valueOf(value);
    }
    
    private static void displayUsageAndExit(String[] args) {
        System.err.println("Usage: RoundTripReport TS_HOME reportPath testcase_filename testcase_path testname testresult");
        System.err.println("Your input:");
        System.err.println("Number of arguments: "+args.length);
        for(int i=0; i<args.length; i++) {
            System.err.println("args["+i+"]="+args[i]);
        }
        System.err.println("Example: RoundTripReport /projects/fi/RoundTripTests/data xmlts_report.html 011.xml /projects/fi/RoundTripTests/xmltest/valid saxroundtrip failed");
        System.exit(1);
    }

    
    private static class FailedTestRecord {
        private int rttMap;
        private String testName;
        public FailedTestRecord(String testName) {
            this.testName = testName;
        }
        
        public boolean isFailedFor(int rtt) {
            return (rttMap & (1 << rtt)) != 0;
        }
        
        public void setFailedFor(int rtt) {
            rttMap |= (1 << rtt);
        }
    }
    
}
