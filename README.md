[//]: # " Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved. "
[//]: # "  "
[//]: # " This program and the accompanying materials are made available under the "
[//]: # " terms of the Eclipse Distribution License v. 1.0, which is available at "
[//]: # " http://www.eclipse.org/org/documents/edl-v10.php. "
[//]: # "  "
[//]: # " SPDX-License-Identifier: BSD-3-Clause "

# Fast Infoset

[![Build Status](https://github.com/eclipse-ee4j/jaxb-fi/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/eclipse-ee4j/jaxb-fi/actions/workflows/maven.yml?branch=master)
[![Jakarta Staging (Snapshots)](https://img.shields.io/nexus/s/https/jakarta.oss.sonatype.org/com.sun.xml.fastinfoset/fastinfoset-project.svg)](https://jakarta.oss.sonatype.org/content/repositories/staging/com/sun/xml/fastinfoset/)

Fast Infoset Project, an Open Source implementation of the Fast Infoset Standard for Binary XML.

The Fast Infoset specification ([ITU-T Rec. X.891](https://www.itu.int/rec/T-REC-X.891/en)
| [ISO/IEC 24824-1](https://www.iso.org/standard/41327.html)) describes an open,
standards-based "binary XML" format that is based on the [XML Information Set](https://www.w3.org/TR/xml-infoset/).

For background, see the Fast Infoset article at Oracle Tech-Network [Fast Infoset](http://www.oracle.com/technetwork/articles/javase/fastinfoset-139262.html)


This project is part of [Eclipse Implementation of JAXB](https://projects.eclipse.org/projects/ee4j.jaxb-impl).

### Limitations

#### Base64 Optimization

The Base64 optimization only applies to schema-defined (or WSDL-defined) elements.
It does *not* apply to attribute values or element values, which are not defined in the schema
(for example, those generated by the XWSS library). Moreover, this optimization is turned off when
handlers are defined.

#### XWSS Library Optimizations

Certain optimizations in the XWSS library may not be available when Fast Infoset
is turned on.


## License

Fast Infoset is licensed under a license - [Apache License, 2.0](LICENSE).


## Contributing

We use [contribution policy](CONTRIBUTING.md), which means we can only accept contributions under
the terms of [Eclipse Contributor Agreement](http://www.eclipse.org/legal/ECA.php).


## Links

* [Javadoc](https://javadoc.io/doc/com.sun.xml.fastinfoset/FastInfoset/latest/com.sun.xml.fastinfoset/module-summary.html)
* [Mailing list](https://accounts.eclipse.org/mailing-list/jaxb-impl-dev)
* [Nightly build job](https://ci.eclipse.org/jaxb-impl/job/jaxb-fi-master-build/)
