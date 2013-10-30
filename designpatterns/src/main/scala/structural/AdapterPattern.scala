package structural

trait Logger {
  //Defines the new logger classes
  def log(message: String)
}

//Create a class that contains an instance of the old
//logger type classes to avoid modifying it
class PrinterLogger(printer: Printer) extends Logger {
  def log(message: String) = printer.print(message)
}

//Legacy class
class Printer {
  def print(message: String) = println(message)
}

object Client {
  val logger:Logger = new PrinterLogger(new Printer)
  def useIt(message: String) = logger.log(message)
}


/**
 * The ADAPTER pattern is used to adapt a legacy class
 * with another interface without modifying it
 */
object AdapterPattern {
  def main(args: Array[String]): Unit = {
    Client.useIt("hello world")
  }
}
