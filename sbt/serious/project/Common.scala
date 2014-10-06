import sbt.Keys._
import sbt._
import complete.Parser
import complete.DefaultParsers._

// Common code might me shared
object Common {
  def organization = "org.gmd"
}

// Use scala classes to share code among all the projects. In this case the build object is edited to create
// settings and tasks keys. Default settings for this build are overridden. The same might be done with a
// project, but it is a good practice to try to have the settings in tbe build.sbt that belongs just to the
// container project.
object AppBuild extends Build {

  // Dependencies for the shell executor come from the project build.sbt
  import org.gmd.shell._
  val shellExecutor = new Executor

  // A task that has no output, only side effects
  val testJs = taskKey[Unit]("Perform tests on the javascript files")

  // A task that returns the result of performing git status
  val gitStatus = taskKey[String]("Checks the status of the git repository")

  val gitCmdPullParser: Parser[String] = "pull"

  val gitCmdPushParser: Parser[String] = "push"

  val gitCmdFetchParser: Parser[String] = "fetch"

  val gitCmdStatusParser: Parser[String] = "status"

  val gitCmdCommitParser = "commit" ~ ' ' ~ "-a" ~ ' ' ~ "-m" ~ ' ' ~ StringBasic.examples("\"Added this and that\"")

  val gitCmdUpdateModuleParser = "submodule" ~ ' ' ~ "foreach" ~ ' ' ~ "git" ~ ' ' ~ "pull"

  val gitArgsParser = ' ' ~ oneOf(Seq(gitCmdPullParser, gitCmdStatusParser,
    gitCmdPushParser, gitCmdFetchParser, gitCmdCommitParser, gitCmdUpdateModuleParser))

  //Example of bash completion using parsers
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
      def parsedToString(parsed: Any): String = {
        parsed match {
          case text: String => if(text.indexOf(' ') != -1) "\"" + text + "\"" else text
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

