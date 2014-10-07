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

  //Creates the project structure. With aggregation, commands executed in the parent also
  //execute in the children. To avoid this, tasks might be removed from the aggregation.
  //Project dependencies might be also set here
  lazy val root = (project in file(".")).aggregate(util, core).dependsOn(core, shell).settings(
    aggregate in gitStatus := false,
    aggregate in git := false
  )
  lazy val util = project
  //A project that is directly a git repository
  lazy val shell = RootProject(uri("git://github.com/akustik/shellexecutor.git"))
  lazy val core = project.dependsOn(util).settings(
    version := "0.9.8"
  )

  //Override the default settings for this build. These might be also overriden for
  //each project sbt/scala files
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

