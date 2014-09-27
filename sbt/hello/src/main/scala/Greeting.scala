import org.apache.commons.exec.{DefaultExecutor, CommandLine}

object Welcome {
  def main(args: Array[String]) = {
    val g = new Greeting
    println(g.greet("everyone"))
  }
}

class Greeting {
  def greet(name: String): String = {
    if(name.equalsIgnoreCase("me")){
      throw new IllegalArgumentException("what the hell?")
    }
    return s"hi $name";
  }

  def echo(what: String): Int = {
    val command = new CommandLine("/bin/sh");
    command.addArgument("-c")
    command.addArgument("echo 'test'", false)
    new DefaultExecutor().execute(command);
  }
}
