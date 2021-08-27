#!/bin/sh

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
    #ext=${file##*.}
    ext=`echo "$file" | sed 's/.*\.//'`
    if [ $ext = "xml" ]; then
        #echo $file is an xml file
        removeXML1_1.csh $file
        if [ -f $file ]; then
            xercesTest "$file"
        fi
    #else
        #echo $file is not an xml file
    fi
}
xercesTest() {
    xercesTestBase.csh $1
}


rm -rf `find $1 -name CVS` 

for file in *
do
    if [ -d "$file" ]; then
        #echo $file is directory
        handle_dir "$file"
    else
        #echo $file is file, perform Xerces test
        handle_file "$file"
    fi
done 

removeEmptyDir.sh
