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

  val apiVersion = settingKey[String]("The API version")

  val appVersion = settingKey[String]("The application version")

  //Creates the project structure. With aggregation, commands executed in the parent also
  //execute in the children. To avoid this, tasks might be removed from the aggregation.
  //Project dependencies might be also set here
  lazy val root = (project in file(".")).aggregate(util, core).dependsOn(core, shell).settings(
    aggregate in gitStatus := false,
    aggregate in ShellExecutorPlugin.autoImport.git := false
  )
  lazy val util = project
  //A project that is directly a git repository
  lazy val shell = RootProject(uri("git://github.com/akustik/shellexecutor.git#master"))
  lazy val core = project.dependsOn(util).settings(
    version := "0.9.8"
  )

  def show[T](s: Seq[T]) =
    s.map("'" + _ + "'").mkString("[", ", ", "]")

  //A command that demonstrates getting information out of State. They are used when tasks are not
  //enough and allow to modify the internal sbt state
  def printState = Command.command("printState") { state =>
    import state._
    println(definedCommands.size + " registered commands")
    println("commands to run: " + show(remainingCommands))
    println()

    println("original arguments: " + show(configuration.arguments))
    println("base directory: " + configuration.baseDirectory)
    println()

    println("sbt version: " + configuration.provider.id.version)
    println("Scala version (for sbt): " + configuration.provider.scalaProvider.version)
    println()

    val extracted = Project.extract(state)
    import extracted._
    println("Current build: " + currentRef.build)
    println("Current project: " + currentRef.project)
    println("Original setting count: " + session.original.size)
    println("Session setting count: " + session.append.size)

    state
  }

  //Override the default settings for this build. These might be also overridden for
  //each project sbt/scala files
  override lazy val settings = super.settings ++ Seq(
    commands ++= Seq(printState),
    version := "1.0.0",
    testJs := {
      println("Testing js files...")
    },
    gitStatus := shellExecutor("git status")
  )
}

