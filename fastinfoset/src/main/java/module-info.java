/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017, 2021 Oracle and/or its affiliates. All rights reserved.
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

module com.sun.xml.fastinfoset {
    requires java.logging;
    requires transitive java.xml;

    exports com.sun.xml.fastinfoset;
    exports com.sun.xml.fastinfoset.algorithm;
    exports com.sun.xml.fastinfoset.alphabet;
    exports com.sun.xml.fastinfoset.dom;
    exports com.sun.xml.fastinfoset.sax;
    exports com.sun.xml.fastinfoset.stax;
    exports com.sun.xml.fastinfoset.stax.events;
    exports com.sun.xml.fastinfoset.stax.factory;
    exports com.sun.xml.fastinfoset.stax.util;
    exports com.sun.xml.fastinfoset.tools;
    exports com.sun.xml.fastinfoset.util;
    exports com.sun.xml.fastinfoset.vocab;
    exports org.jvnet.fastinfoset;
    exports org.jvnet.fastinfoset.sax;
    exports org.jvnet.fastinfoset.sax.helpers;
    exports org.jvnet.fastinfoset.stax;
}
