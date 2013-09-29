package behavioral


object ChainOfResponsibilityPattern {

  trait Logger {
    protected var level: Int = 0
    protected var nextLogger: Logger = null
    def setNext(l: Logger) = {
      nextLogger = l;
    }

    def message(message: String, priority: Int) {
      if (level == priority) {
        writeMessage(message)
      } else if(nextLogger != null){
        nextLogger.message(message, priority)
      } else {
        println("Not processed: " +  message)
      }
    }

    protected def writeMessage(message: String)
  }
  
  class ErrorLogger extends Logger {
    level = 1
    def writeMessage(message: String) = {
      println("ERROR: " + message)
    }
  }
  
  class InfoLogger extends Logger {
    level = 5
    def writeMessage(message: String){
      println("INFO: " + message)
    }
  }
  
  class SilentLogger extends Logger {
    level = 0
    override def message(message: String, priority: Int) = {
      //Matches always!
      writeMessage(message)
    }
    def writeMessage(message: String){
      //sshhhh
    }
  }

  /**
   * The CHAIN OF RESPONSIBILITY pattern allows to decouple the sender from the
   * receiver and creates a chain that passes the message until a receiver is
   * able to handle it.
   */
  
  def main(args: Array[String]): Unit = {
    
    //We create a set of objects that are able to handle logging messages. 
    val l2 = new InfoLogger
    val l1 = new ErrorLogger
    val l3 = new SilentLogger
    
    //We chain them in order to use them with a single call
    l1.setNext(l2)
   
    //See how the matching is done depending on the priority. We just need to see
    //the public method message to use any logger combination
    l1.message("An error message should be handler by the error logger", 1)
    l1.message("This passes the error logger and is processed by the info logger", 5)
    l1.message("When there is no matching, it drops the message", 1000)
    
    //Hey, we may just add a final handler that matches always to keep track of it
    l2.setNext(l3)
    
    //Let's see what happens
    l1.message("Now there is always matching, but the silent logger does nothing "
        + "(now, maybe in the future...)", 1000)
  }

}