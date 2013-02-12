#!/bin/bash

scala -classpath bin org.gmd.scala.uf.$1 test/$1.unions.txt test/$1.output.txt
