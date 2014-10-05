
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

  val gitCmdPullParser: Parser[String] = "pull"

  val gitCmdPushParser: Parser[String] = "push"

  val gitCmdFetchParser: Parser[String] = "fetch"

  val gitCmdStatusParser: Parser[String] = "status"

  val gitCmdCommitParser = "commit" ~ ' ' ~ "-a" ~ ' ' ~ "-m" ~ ' ' ~ StringBasic.examples("\"Added this and that\"")

  val gitCmdUpdateModuleParser = "submodule" ~ ' ' ~ "foreach" ~ ' ' ~ "git" ~ ' ' ~ "pull"

  val gitArgsParser = ' ' ~ oneOf(Seq(gitCmdPullParser, gitCmdStatusParser,
    gitCmdPushParser, gitCmdFetchParser, gitCmdCommitParser, gitCmdUpdateModuleParser))

  val gitNaiveParser = spaceDelimited("<arg>")

  val git = inputKey[String]("A git command line.")

  val gitCmd = inputKey[String]("A git command line with some checks.")

  val apiVersion = settingKey[String]("The API version")

  val appVersion = settingKey[String]("The application version")


  override lazy val settings = super.settings ++ Seq(
    version := "1.0.0",
    testJs := {
      println("Testing js files...")
    },
    gitStatus := shellExecutor("git status"),
    git := shellExecutor("git" + " " + gitNaiveParser.parsed.mkString(" ")),
    gitCmd := {
      def parsedToString(parsed: Any): String = {
        parsed match {
          case text: String => "\"" + text + "\""
          case ' ' => " "
          case (c, (a, b)) => parsedToString(c)  + parsedToString((a, b))
          case ((a, b), c) => parsedToString((a, b)) + parsedToString(c)
          case (a, b) => parsedToString(a) + parsedToString(b)
          case _ => {
            throw new IllegalArgumentException(parsed.toString)
          }
        }
      }
      shellExecutor("git" + parsedToString(gitArgsParser.parsed))
    }
  )
}

