#!/bin/bash
#Mami Hackl mami1203
#Cuijun Wu cuijunwu
#John Keesling keesling
cd src
javac -classpath .:lucene-core-2.9.1.jar ContIndexer.java
javac -classpath .:lucene-core-2.9.1.jar ContRet.java
#java -classpath .:lucene-core-2.9.1.jar ContRet ~/dropbox/09-10/573/corpora/testdata/
java -classpath .:lucene-core-2.9.1.jar ContRet ~/dropbox/09-10/573/D4/duc07.results.data/testdata/
postProcess.py ../output
javac -classpath .:lingpipe-3.9.2.jar SentCohesion.java
java -classpath .:lingpipe-3.9.2.jar SentCohesion
javac PostProcess.java
java PostProcess
