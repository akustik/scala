object repeatuntil {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(64); 
  println("Welcome to the Scala worksheet")
  
  class RepeatRepeater(command : => Unit) {
    def UNTIL(condition: => Boolean): Unit = {
      command
      if(!condition) REPEAT(command) UNTIL (condition)
    }
  };$skip(238); 
  
  def REPEAT(command : => Unit) = new RepeatRepeater(command);System.out.println("""REPEAT: (command: => Unit)repeatuntil.RepeatRepeater""");$skip(53); 
  
  def DO(body: => Unit) = new WhileRepeater(body)

  class WhileRepeater(body: => Unit) {
    def WHILE(cond: => Boolean) {
      body;
      val value: Boolean = cond;

      if (value) DO(body).WHILE(cond)
    }
  };System.out.println("""DO: (body: => Unit)repeatuntil.WhileRepeater""");$skip(323); 

/*
  def REPEAT(command : => Unit)(condition : => Boolean) {
    command
    if(condition) ()
    else REPEAT(command)(condition)
  }
  */
  
  var x = 0;System.out.println("""x  : Int = """ + $show(x ));$skip(58); 
  REPEAT {
    x = x +1
    println(x)
  } UNTIL (x > 10)}
                                                  
}
