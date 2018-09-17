/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

module com.sun.xml.fastinfoset {
    requires java.logging;
    requires transitive java.xml;

    exports com.sun.xml.fastinfoset;
    exports com.sun.xml.fastinfoset.stax;
    exports com.sun.xml.fastinfoset.vocab;
    exports org.jvnet.fastinfoset;
}
