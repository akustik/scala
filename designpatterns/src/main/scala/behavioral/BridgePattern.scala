package behavioral

trait BridgeImplementation {
  def doOperation(): Unit
}

class SomeImplementation extends BridgeImplementation {
  def doOperation(): Unit = println("Some")
}

class SomeOtherImplementation extends BridgeImplementation {
  def doOperation(): Unit = println("Some other")
}

class BridgeAbstraction(impl: BridgeImplementation) {  
  def doIt = {
    println("start")
    impl.doOperation()
    println("end") 
  }
}

object BridgePattern {
  def main(args: Array[String]): Unit = {
    val abstraction = new BridgeAbstraction(new SomeImplementation)
    abstraction.doIt
  }
}
