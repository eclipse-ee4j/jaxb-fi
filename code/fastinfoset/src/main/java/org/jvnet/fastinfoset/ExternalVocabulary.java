/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.fastinfoset;

/**
 * An external vocabulary.
 * <p>
 * An external vocabulary has a URI that refers to a vocabulary.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class ExternalVocabulary {
  
    /**
     * A URI that refers to the external vocabulary.
     */
    public final String URI;
    
    /**
     * The vocabulary that is refered to by the URI.
     */
    public final Vocabulary vocabulary;
    
    public ExternalVocabulary(String URI, Vocabulary vocabulary) {
        if (URI == null || vocabulary == null) {
            throw new IllegalArgumentException();
        }
        
        this.URI = URI;
        this.vocabulary = vocabulary;
    }
}
