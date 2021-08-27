#!/bin/bash
#set env:
#FIRTT_HOME 

export FI_HOME=${FIRTT_HOME}/../FastInfoset
export FIRTT_DATA=${FIRTT_HOME}/report
export RESULT_HOME=${FIRTT_HOME}/report


#.bashrc on fi.eastsource /projects/fws/.bashrc
#source ${HOME}/.bashrc
export PATH=${JAVA_HOME}/bin:${FIRTT_HOME}/bin:${FI_HOME}/bin:.:$PATH

# Get sources from java.net, compile FastInfoset and RoundTripTests
ant -f ${FI_HOME}/build-without-nb.xml clean dist
ant -f ${FIRTT_HOME}/build-without-nb.xml dist

# cd $FIRTT_HOME/data/xmlconf
REPORT_TS=xmlts.html
java -cp ${FIRTT_HOME}/dist/RoundTripTests.jar:${FI_HOME}/dist/FastInfoset.jar com.sun.xml.fastinfoset.roundtriptests.AllRoundTripTest ${FIRTT_HOME}/data/xmlconf ${FIRTT_DATA}/${REPORT_TS}
# ${FIRTT_HOME}/bin/allRoundtripTests.sh ${REPORT_TS}

# cd ${FIRTT_HOME}/data/XBC
REPORT_XBC=xbc.html
java -cp ${FIRTT_HOME}/dist/RoundTripTests.jar:${FI_HOME}/dist/FastInfoset.jar com.sun.xml.fastinfoset.roundtriptests.AllRoundTripTest ${FIRTT_HOME}/data/XBC ${FIRTT_DATA}/${REPORT_XBC}
# ${FIRTT_HOME}/bin/allRoundtripTests.sh ${REPORT_XBC}

# cleandiff.sh ${FIRTT_HOME}/data