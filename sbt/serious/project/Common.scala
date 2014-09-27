import sbt._
import Keys._



object AppBuild extends Build {

  val testJs = taskKey[Unit]("Perform tests on the javascript files")

  override lazy val settings = super.settings ++ Seq(
    version := "1.0.0",
    testJs := {
      println("Testing js files...")
    }
  )
}

object Common {
  def organization = "org.gmd"
}
