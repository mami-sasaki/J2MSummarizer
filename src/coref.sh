#!/bin/sh

## NOTE: Use absolute filepaths
##Usage: ARG1=file to be processed
##       ARG2=file containing the sentence in question
##       ARG3=file containing the anaphor in question
##       ARG4=path in which this and the helper scripts can be found

cd /NLP_TOOLS/ie_tools/LingPipe/latest/demos/generic/bin/

./cmd_coref_en_news_muc6.sh -inFile=$1 -outFile=$4/coref > $4/tmp

cd $4

python prePro.py $4/coref > $4/coref.proc

python findRef.py $4/coref.proc $4/$2 $4/$3

rm $4/coref
rm $4/coref.proc
rm $4/tmp
