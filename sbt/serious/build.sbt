name := "app"

artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  artifact.name + "-" + module.revision + "." + artifact.extension
}

scalaVersion := "2.11.2"

apiVersion in ThisBuild := "0.9.5"

appVersion := "0.8.0"

organization := Common.organization

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

lazy val root = (project in file(".")).aggregate(util, core).dependsOn(core).settings(
  aggregate in gitStatus := false,
  aggregate in git := false
)

lazy val util = project

lazy val core = project.dependsOn(util)

(test in Test) := {
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