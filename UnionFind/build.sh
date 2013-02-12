#!/bin/bash

mkdir -p bin
SCALA_FILES=`find . -name "*.scala"`
fsc -d $PWD/bin $SCALA_FILES
