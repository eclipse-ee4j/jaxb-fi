#!/bin/sh

usage() {
    echo "Usage: `basename $0` extension [-d]"
    echo extension -- file extension
    echo -d -- optinal, if specfied, remove empty directories
    echo Usage note: the script will remove any files with the 'extension' that are not \
    referenced by other files. **use with caution. use only from the root directory \
    of XML data **
    exit $E_BADARGS
}


handle_dir() {
    cd "$1"
#linus:    count_subdirs=$(find . -type d| wc -l)
    count_subdirs=`find . -type d| wc -l`
    if [ $count_subdirs = 1 ]; then
        #echo $1 does not contain sub-directory
        handle_file $1
    else
        for file in *
        do
            if [ -d "$file" ]; then
                #echo $file is directory
                handle_dir "$file"
            fi
        done 

    fi
    cd ".."
    if [ $numargs = 2 ]; then
        if [ $removedir = -d ]; then
            remove_emptydir $1
        fi
    fi
}     

handle_file() {
    #count_xmlfiles=$(find . -name '*.xml'| wc -l)
    count_xmlfiles=`find . -name '*.xml'| wc -l`
    if [ $count_xmlfiles = 0 ]; then
        #echo $1 does not contain xml files
        for file in *
        do
            if [ -f "$file" ]; then
                #ext=${file##*.}
                ext=`echo "$file" | sed 's/.*\.//'`
                if [ $extention = $ext ]; then
                    reference=`grep -lir "$file" .| wc -l`
                    if [ $reference = 0 ]; then
                        echo file $file is not referenced, remove
                        rm $file
                    fi
                fi
            fi
        done 

    fi
}

remove_emptydir() {
    total_files=`ls $1 | wc -l`
    if [ $total_files = 0 ]; then
        echo $1 is empty, remove
        rmdir $1
    fi
}

NOARGS=0
E_BADARGS=65

numargs=$#

if [ $numargs = $NOARGS ]
then
    usage
else
    if [ $1 = -h ]; then
        usage
    fi
fi 

extention=$1
removedir=$2
home="$PWD"
handle_dir "$home"

