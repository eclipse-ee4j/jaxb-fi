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

import java.io.File;
import java.io.FileFilter;

/**
 * @author Alexey Stashok
 */
class FileUtils {
    
    final public static void processFileOrDirectoryRecursivelly(File srcFile, FileFilter filter, FileProcessorHandler handler) {
        if (srcFile.isFile()) {
            String filename = srcFile.getName();
            int delim = filename.lastIndexOf('.');
            if (delim != -1) {
                String ext = filename.substring(delim, filename.length());
                if (ext.equals(".xml")) {
                    handler.handle(srcFile);
                }
            }
        } else if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles(filter);
            
            for(int i=0; i<files.length; i++) {
                processFileOrDirectoryRecursivelly(files[i], filter, handler);
            }
        }
    }
    
    final public static void removeFileRecursivelly(File testSrcFile, FileFilter filter) {
        File[] files = testSrcFile.listFiles(filter);
        
        for(int i=0; i<files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                final boolean result = file.delete();
                assert result;
            } else {
                removeFileRecursivelly(file, filter);
            }
        }
    }
    
    interface FileProcessorHandler {
        void handle(File file);
    }
}
