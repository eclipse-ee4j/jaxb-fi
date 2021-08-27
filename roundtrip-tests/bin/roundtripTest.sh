#!/bin/sh

if [ $# != 2 ]; then
    echo Usage: roundtripTest [roundtrip test name] [report name]
    echo Usage example: roundtripTest saxstaxdiff_rtt xmlts_report.html
    exit
fi


handle_dir() {
    
    cd "$1"
    for file in *
    do
        if [ -d "$file" ]; then
            #echo $file is directory
            handle_dir "$file"
        else
            #echo $file is file
            handle_file "$file"
        fi
    done 
    cd ".."
}     

handle_file() {
    file=$1
    ext=`echo "$file" | sed 's/.*\.//'`
    #echo extionsion: $EXTENSION
    #ext=${file##*.}
    if [ $ext = "xml" ]; then
        #echo $file is an xml file
        $cmd $file "$PWD" $reportname
    #else
        #echo $file is not an xml file
    fi
}


cmd=$1
reportname=$2

for file in *
do
    if [ -d "$file" ]; then
        #echo $file is directory
        handle_dir "$file"
#    else
#        echo $file is file, perform Xerces test
#        handle_file "$file"
    fi
done 


removeUnusedFiles.sh ent -d
removeUnusedFiles.sh dtd -d

