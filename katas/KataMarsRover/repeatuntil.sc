object repeatuntil {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  class RepeatRepeater(command : => Unit) {
    def UNTIL(condition: => Boolean): Unit = {
      command
      if(!condition) REPEAT(command) UNTIL (condition)
    }
  }
  
  def REPEAT(command : => Unit) = new RepeatRepeater(command)
                                                  //> REPEAT: (command: => Unit)repeatuntil.RepeatRepeater
  
  def DO(body: => Unit) = new WhileRepeater(body) //> DO: (body: => Unit)repeatuntil.WhileRepeater

  class WhileRepeater(body: => Unit) {
    def WHILE(cond: => Boolean) {
      body;
      val value: Boolean = cond;

      if (value) DO(body).WHILE(cond)
    }
  }

/*
  def REPEAT(command : => Unit)(condition : => Boolean) {
    command
    if(condition) ()
    else REPEAT(command)(condition)
  }
  */
  
  var x = 0                                       //> x  : Int = 0
  REPEAT {
    x = x +1
    println(x)
  } UNTIL (x > 10)                                //> 1
                                                  //| 2
                                                  //| 3
                                                  //| 4
                                                  //| 5
                                                  //| 6
                                                  //| 7
                                                  //| 8
                                                  //| 9
                                                  //| 10
                                                  //| 11
                                                  
}