name := "sbtHelloWorld"

version := "1.0.0"

scalaVersion := "2.10.2"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"

lazy val myTask = taskKey[Unit]("An example task")

myTask := {
 println("starting task...")
 println("task ended")
}
