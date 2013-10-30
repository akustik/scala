package structural

//The common interface for both the proxy and the proxied object
trait Adder {
  def add(a: Int, b:Int)
}

class SimpleAdder extends Adder {
  def add(a: Int, b:Int) = a + b
}

class AdderLoggerProxy(adder: Adder) extends Adder {
  def add(a: Int, b: Int) = {
    println("Add: " + a + ", " + b)
    adder.add(a, b)
  }
}

object ProxyClient {
  def useIt() = {
    val adder = new SimpleAdder
    val proxy = new AdderLoggerProxy(adder)
    proxy.add(2, 3)
  }
}

/**
 * The PROXY pattern is used to control the access to an object
 * using a common interface so the proxy fits in place of the 
 * element.
 */
object ProxyPattern {
  def main(args: Array[String]): Unit = {
    ProxyClient.useIt()
  }
}
