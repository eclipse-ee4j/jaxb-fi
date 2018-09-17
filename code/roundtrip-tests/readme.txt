FastInfoset RoundTrip Tests (RoundTripTests)

1. General
   The FastInfoset RoundTrip Test suite contains XML files filtered from the W3C test suite:
(http://www.w3.org/XML/Test/) and XBC Test Corpus (http://www.w3.org/XML/Binary/2005/03/test-data/).
   The filtering process is as the following:
   a. Remove all CVS folders;
   b. Remove XML 1.1 files;
   c. Remove xml files that fail Xerces test. That is, all xml files are tested using the default
Xerces parser in JDK 5.0.  All xml files that fail the test are removed.
   d. Remove empty directories


2. Getting well-formed XML files
   a. Download XML test data mentioned above (http://java.net/projects/fi/sources/svn/content/trunk/xml-data.zip)
to the module's root;
   b. Unpack the archive
   c. Edit RoundTripTests/bin/env.sh and execute to set FI_HOME and FIRTT_HOME as well as paths to
FI_HOME/bin and FIRTT_HOME/bin;
   d. Make sure JDK 1.5 is in the path;
   e. Change to data/xmlconf directory and execute "xercesTest.sh ."

3. Running RoundTrip Tests
   a. Perform 2.c. above if it's not have been performed;
   b. Change to FIRTT_HOME/data/xmlconf
   c. Execute "allRoundTripTests.sh xmlts_report.html" to test against the XMLTS suite and generate
a report "xmlts_report.html" under FIRTT_HOME/data directory
   d. Change to FIRTT_HOME/data/XBC
   e. Execute "allRoundTripTests.sh xbc_report.html" to test against the XBC files and generate a report
called "xbc_report.html" under FIRTT_HOME/data directory.

4. History
   Historical tests along with versions of FastInfoset jars are saved under subdirectory "History".
