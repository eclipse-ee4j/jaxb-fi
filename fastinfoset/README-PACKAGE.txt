FastInfoset.jar
---------------

The FastInfoset.jar is in the dist directory. Dependent jars are in
the lib directory.


Scripts
-------

A number of UNIX scripts are present in the 'bin' directory. 

The scripts require that the FI_HOME environment variable be set to the
top-level directory of this distribution.


xmltosaxtofi <input> <output>
-----------------------------
Parses an XML document and transforms it into an equivalent fast infoset
document using the FI SAX serializer.
	The 'input' shall be a XML document file.
	If the input is ommited then the standard input is used.
	The 'output' is the resulting fast infoset document file.
	If the output is ommited then the standard output is used.

fitosaxtoxml <input> <output>
-----------------------------
Parses a fast infoset document and transforms it into an equivalent XML
document using the FI SAX parser.
	The 'input' shall be a fast infoset document file.
	If the input is ommited then the standard input is used.
	The 'output' is the resulting XML document file.
	If the output is ommited then the standard output is used.

saxtosaxevent <input> <output>
------------------------------
Parses an XML document or a fast infoset document and outputs a SAX event
XML document containing all the SAX events resulting from the parse.
	The 'input' shall be an XML document file or a fast infoset document
	file.  If the input is ommited then the standard input is used.
	The 'output' is the resulting SAX event XML document file.
	If the output is ommited then the standard output is used.

saxroundtrip <input>
--------------------
Tests to ascertain if an XML document can roundtrip to a fast infoset document
and back to an XML document without loss of infoset information items when
using the XML and FI SAX parsers and serializers..
If roundtripping passes then 'PASSED' will be output. If round tripping fails 
then 'FAILED' will be output, in addition the diff file location will be 
printed, which can be looked at for further evaluation.
	The 'input' shall be an XML document file. After exceution the
        following files will be created 'input'.sax-event, 'intput'.finf,
        'input'.finf.sax-event, 'input'.sax-event.diff'

saxroundtripdir <dir>
---------------------
Tests the contents (and sub-contents) of a directory containing XML files for
fast infoset roundtrip consistency. For each file 'saxroundtrip <file>' will
be executed.


xmltosaxtostaxtofi <input> <output>
-----------------------------------
Parses an XML document and transforms it into an equivalent fast infoset
document using the SAX to StAX adapter and the FI StAX serializer.
	The 'input' shall be a XML document file.
	If the input is ommited then the standard input is used.
	The 'output' is the resulting fast infoset document file.
	If the output is ommited then the standard output is used.

staxtosaxtosaxevent <input> <output>
------------------------------------
Parses an XML document or a fast infoset document and outputs a SAX event
XML document containing all the SAX events resulting from the parse. When 
parsing a fast infoset document the SAX events are adapter from the FI StAX 
parser.
	The 'input' shall be an XML document file or a fast infoset document
	file.  If the input is ommited then the standard input is used.
	The 'output' is the resulting SAX event XML document file.
	If the output is ommited then the standard output is used.

staxroundtrip <input>
---------------------
Tests to ascertain if an XML document can roundtrip to a fast infoset document
and back to an XML document without loss of infoset information items when
using the XML and FI StAX parsers and serializers..
If roundtripping passes then 'PASSED' will be output. If round tripping fails
then 'FAILED' will be output, in addition the diff file location will be 
printed, which can be looked at for further evaluation.
	The 'input' shall be an XML document file. After exceution the
        following files will be created 'input'.sax-event, 'intput'.finf,
        'input'.finf.sax-event, 'input'.sax-event.diff'

staxroundtripdir <dir>
----------------------
Tests the contents (and sub-contents) of a directory containing XML files for
fast infoset roundtrip consistency. For each file 'staxroundtrip <file>' will
be executed.

saxstaxdiff <input>
-------------------
Tests to ascertain if an XML document can be serialized using the FI SAX 
serializer and the FI StAX serializer to equivalent fast infoset documents.

staxroundtripdir <dir>
----------------------
Tests the contents (and sub-contents) of a directory containing XML files for
fast infoset serializing consistency. For each file 'saxstaxdiff <file>' will
be executed.

xmltodomtosaxtofi <input> <output>
----------------------------------
Parses an XML document and transforms it into an equivalent fast infoset
document. The XML document is parsed to a DOM tree then serialized using an
identity transform to a fast infoset document using the FI SAX parser.
	The 'input' shall be a XML document file.
	If the input is ommited then the standard input is used.
	The 'output' is the resulting fast infoset document file.
	If the output is ommited then the standard output is used.

saxtodomtosaxevent <input> <output>
-----------------------------------

Parses an XML document or a fast infoset document and outputs a SAX event
XML document containing all the SAX events resulting from the parse. The XML or
fast infoset document is parsed to a DOM tree and the DOM tree is serialized
using an identity transform to the SAX event XML document.
	The 'input' shall be an XML document file or a fast infoset document
	file.  If the input is ommited then the standard input is used.
	The 'output' is the resulting SAX event XML document file.
	If the output is ommited then the standard output is used.

domsaxroundtrip <input>
-----------------------
Tests to ascertain if an XML document can roundtrip to a fast infoset document
and back to an XML document without loss of infoset information items when using
the DOM as an intermediate stage in the parsing and serializing process.
If roundtripping passes then 'PASSED' will be output. If round tripping fails
then 'FAILED' will be output, in addition the diff file location will be 
printed, which can be looked at for further evaluation.
	The 'input' shall be an XML document file. After exceution the
        following files will be created 'input'.sax-event, 'intput'.finf,
        'input'.finf.sax-event, 'input'.sax-event.diff'

domsaxroundtripdir <dir>
------------------------
Tests the contents (and sub-contents) of a directory containing XML files for
fast infoset serializing consistency. For each file 'domsaxroundtrip <file>'
will be executed.

saxdomsaxdiff <input>
---------------------
Tests to ascertain if an XML document can be serialized using the FI SAX 
serializer directly and via a DOM tree to equivalent fast infoset documents.

saxdomsaxdiffdir <dir>
----------------------
Tests the contents (and sub-contents) of a directory containing XML files for
fast infoset serializing consistency. For each file 'saxdomsaxdiff <file>' will
be executed.
