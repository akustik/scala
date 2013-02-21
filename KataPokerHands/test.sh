#!/bin/bash

function mkclasspath {
        temp=$IFS
        IFS=":"
        array=$1
        result="${array[*]}"
        IFS=$temp
        echo $result 
}


SCALA_LIBS=`find lib -name "*.jar"`
CLASSPATH=`mkclasspath $SCALA_LIBS`

scala -cp bin:$CLASSPATH org.scalatest.run org.gmd.scala.poker.CardSuite
