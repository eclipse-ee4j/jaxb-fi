#!/bin/sh

if [ $# != 1 ]; then
    echo Usage: allRoundtripTests [report name]
    echo Usage example: allRoundtripTests xmlts_report.html
    echo Usage note: report will be placed under FIRTT_DATA as defined in env.sh
    exit
fi

roundtripTest.sh saxroundtrip_rtt $1
roundtripTest.sh staxroundtrip_rtt $1
roundtripTest.sh domroundtrip_rtt $1
roundtripTest.sh domsaxroundtrip_rtt $1
roundtripTest.sh saxstaxdiff_rtt $1
