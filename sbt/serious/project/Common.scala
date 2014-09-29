import java.io.ByteArrayOutputStream

import org.apache.commons.exec._
import sbt.Keys._
import sbt._
import complete.Parser
import complete.DefaultParsers._

class ShellExecutor {
  def execute(cmd: String): String = {
    val output = new ByteArrayOutputStream
    val psh = new PumpStreamHandler(output)
    val command = new CommandLine("/bin/sh")
    command.addArgument("-c")
    command.addArgument(cmd, false)
    val executor = new DefaultExecutor()
    executor.setStreamHandler(psh)

    try {
      if (executor.execute(command) != 0) {
        throw new IllegalStateException(s"Command $cmd failed")
      } else {
        output.toString("UTF-8")
      }
    } catch {
      case e: ExecuteException => {
        println(output.toString("UTF-8"))
        throw e
      }
    }
  }
}

object Common {
  def organization = "org.gmd"
}

object AppBuild extends Build {

  val shellExecutor = new ShellExecutor

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
    gitStatus := shellExecutor.execute("git status"),
    git := {
      //TODO: Improve the parser to allow double quote with the commit command
      shellExecutor.execute("git" + " " + gitNaiveParser.parsed.mkString(" "))
    }
  )
}

