name := "sbtHelloWorld"

version := "1.0.0"

scalaVersion := "2.10.2"

lazy val myTask = taskKey[Unit]("An example task")

myTask := {
 println("starting task...")
 println("task ended")
}
