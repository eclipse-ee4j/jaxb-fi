#!/bin/sh

handle_dir() {
    total_files=`find $1 -type f | wc -l`
    if [ $total_files = 0 ]; then
        echo $1 is empty
        rmdir $1
    else
        cd "$1"
        for file in *
        do
            if [ -d "$file" ]; then
                handle_dir "$file"
            fi
        done 
        cd ".."
    fi    
}     


for file in *
do
    if [ -d "$file" ]; then
        handle_dir "$file"
    fi
done 

