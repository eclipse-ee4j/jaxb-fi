#!/bin/sh

find $1 -name '*.finf' -exec rm {} \;
find $1 -name '*.sax-event' -exec rm {} \;
find $1 -name '*.diff*' -exec rm {} \;

