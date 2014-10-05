
import sbt.Keys._
import sbt._
import complete.Parser
import complete.DefaultParsers._

object Common {
  def organization = "org.gmd"
}

object AppBuild extends Build {

  import org.gmd.shell._
  val shellExecutor = new Executor

  val testJs = taskKey[Unit]("Perform tests on the javascript files")

  val gitStatus = taskKey[String]("Checks the status of the git repository")

  val gitCmdParser: Parser[String] = "pull"

  val gitArgsParser = ' ' ~ gitCmdParser

  val gitNaiveParser = spaceDelimited("<arg>")

  val git = inputKey[String]("A git command line.")

  val apiVersion = settingKey[String]("The API version")

  val appVersion = settingKey[String]("The application version")

  override lazy val settings = super.settings ++ Seq(
    version := "1.0.0",
    testJs := {
      println("Testing js files...")
    },
    gitStatus := shellExecutor("git status"),
    git := {
      //TODO: Improve the parser to allow double quote with the commit command
      shellExecutor("git" + " " + gitNaiveParser.parsed.mkString(" "))
    }
  )
}

