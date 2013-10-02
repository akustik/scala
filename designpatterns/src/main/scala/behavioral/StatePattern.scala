package behavioral

object StatePattern {

  //The state
  trait Printer {
    def print(message: String)
  }
  
  //Concrete state
  class ErrorOutput extends Printer {
    def print(message: String) = Console.err.println(message)
  }
  
  //Concrete state
  class StandardOutput extends Printer{
    def print(message: String) = Console.out.println(message)
  }
  
  //the context
  class Logger {
    var printer: Printer = new StandardOutput
    
    def print(message: String) = printer.print(message)    
    def setError() = printer = new ErrorOutput
    def setStandard() = printer = new StandardOutput     
  }
  
  /**
   * The STATE patterns is intended to change the behavior of a specific object
   * (CONTEXT) without changing the contract. In fact, this can be used to avoid
   * several if/else blocks in the code.
   */
  
  def main(args: Array[String]): Unit = {
    val logger = new Logger;
    logger.print("Print using standard output...")
    logger.setError()
    logger.print("Now print using error output...")
  }

}