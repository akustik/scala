package behavioral

trait Command {
  def execute()
  def name(): String
}

class Invoker {
  private var history: List[Command] = Nil
  
  def invoke(cmd: Command) = {
    cmd.execute()
    history :+= cmd
  }
  
  def status() = "so forth this opeartions have been executed: " + history.map(_.name)
}

class Receiver {
  def action1() = println("action1")
  def action2() = println("action2")
}

class Action1Command(r: Receiver) extends Command {
  def execute() = r.action1()
  def name() = "Action1Command"
}

class Action2Command(r: Receiver) extends Command {
  def execute() = r.action2()
  def name() = "Action2Command"
}

/**
 * The COMMAND pattern allows encapsulate a request (command) as an object
 * with a common interface. These commands are called by the invoker who 
 * centralizes the calls allowing to have control of the commands that are
 * executed. These commands operate on a receiver which is the class that 
 * has the business logic. All of this is used by a client class which 
 * contains both the receiver and the invoker, and can apply a set of set up
 * commands on them. This may be used to undo operations, keep track of the
 * executed commands, etc.
 */

object Client {

  def main(args: Array[String]): Unit = {
    val r = new Receiver
    val i = new Invoker
    val cmd1: Command = new Action1Command(r)
    val cmd2: Command = new Action2Command(r)
    
    i.invoke(cmd1)
    i.invoke(cmd2)
    
    println(i.status)
    
  }
}