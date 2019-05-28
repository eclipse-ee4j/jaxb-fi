/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

module com.sun.xml.fastinfoset.utilities {
    requires com.sun.xml.fastinfoset;
    requires com.sun.xml.xsom;
    requires com.sun.tools.rngdatatype;
    requires com.sun.xml.streambuffer;
    requires org.jvnet.staxex;
    requires jakarta.activation;

    exports com.sun.xml.analysis.frequency;
    exports com.sun.xml.analysis.frequency.tools;
    exports com.sun.xml.analysis.types;
    exports com.sun.xml.fastinfoset.streambuffer;
    exports com.sun.xml.fastinfoset.streambuffer.tools;
    exports com.sun.xml.fastinfoset.types;
    exports com.sun.xml.fastinfoset.utilities.tools;
    exports com.sun.xml.fastinfoset.vocab.frequency;
}
