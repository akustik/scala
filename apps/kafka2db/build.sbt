name := "kafka2db"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.2"

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

libraryDependencies += "org.specs2" % "specs2_2.10" % "2.2"
            
libraryDependencies += "org.apache.kafka" % "kafka_2.10" % "0.8.0" exclude("com.sun.jdmk", "jmxtools") exclude("com.sun.jmx", "jmxri")
