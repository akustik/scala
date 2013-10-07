#!/bin/bash

function mkclasspath {
	temp=$IFS
	IFS=":"
	array=$1
	result="${array[*]}"
	IFS=$temp
	echo $result 
}

rm -fr bin
mkdir -p bin
SCALA_FILES=`find . -name "*.scala"`
SCALA_LIBS=`find lib -name "*.jar"`
CLASSPATH=`mkclasspath $SCALA_LIBS`
fsc -d $PWD/bin -deprecation -classpath $CLASSPATH $SCALA_FILES
