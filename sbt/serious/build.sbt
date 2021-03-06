import org.gmd.shell._

//This is an assignment
name := "app"

//Override built-in settings
onLoadMessage := "Project commands: gitStatus, git, testJs, printState\n" + onLoadMessage.value

//Settings might be overridden. In this case it is a function that creates the
//artifact name and has been changed to remove the scala version from the name
artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  artifact.name + "-" + module.revision + "." + artifact.extension
}

scalaVersion := "2.11.2"

//This setting affects al the build and thus core and utils have it too
apiVersion in ThisBuild := "0.9.5"

//This other setting is set in the default scope, affecting just this project
appVersion := "0.8.0"

organization := Common.organization

//Appends the scala test for this scala version to the list of dependencies. The
//classpath might be checked with "show core/test:fullClasspath"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

//A task might be overridden to execute another task before this. Note that this appears in
//the task dependencies
(test in Test) := {
  println((version in (Test, test)).value)
  testJs.value
  (test in Test).value
}

(packageBin in Compile) := {
  val log = streams.value.log
  val status = gitStatus.value
  if(status.contains("modified")) {
    log.error(status)
    throw new IllegalArgumentException("Dirty repo")
  }
  (packageBin in Compile).value
}
