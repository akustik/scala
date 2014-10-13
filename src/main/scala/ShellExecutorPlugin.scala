import sbt._
import Keys._

import complete.Parser
import complete.DefaultParsers._


object ShellExecutorPlugin extends AutoPlugin {

  val gitCmdPullParser: Parser[String] = "pull"

  val gitCmdPushParser: Parser[String] = "push"

  val gitCmdFetchParser: Parser[String] = "fetch"

  val gitCmdStatusParser: Parser[String] = "status"

  val gitCmdCommitParser = "commit" ~ ' ' ~ "-a" ~ ' ' ~ "-m" ~ ' ' ~ StringBasic.examples("\"Added this and that\"")

  val gitCmdUpdateModuleParser = "submodule" ~ ' ' ~ "foreach" ~ ' ' ~ "git" ~ ' ' ~ "pull"

  val gitArgsParser = ' ' ~ oneOf(Seq(gitCmdPullParser, gitCmdStatusParser,
    gitCmdPushParser, gitCmdFetchParser, gitCmdCommitParser, gitCmdUpdateModuleParser))

  import org.gmd.shell._
  val shellExecutor = new Executor

  //Enable automatically the plugin
  override def trigger = allRequirements

  //Import the given settings and tasks automatically in the project
  object autoImport {
    val git = inputKey[String]("A git command line.")
  }
  import autoImport._

  //Override the build settings to add the git command
  override lazy val buildSettings = Seq(
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
    })  
}
