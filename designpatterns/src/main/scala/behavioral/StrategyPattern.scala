package behavioral

trait Operator {
  def execute(a: Int, b: Int): Int
}

class Add extends Operator {
  def execute(a: Int, b: Int): Int = a + b
}

class Product extends Operator {
  def execute(a: Int, b: Int): Int = a * b
}

class Context(o: Operator) {
  def executeOperation(a: Int, b: Int): Int = {
    o.execute(a, b)
  }

}

/**
 * The STRATEGY pattern defines a common interface for a family of
 * algorithms. Then, the CONTEXT object is setup with the client needed
 * strategy. This is similar to the STATE pattern, but the behavior of 
 * the object does not change, it is always the same. 
 */
object StrategyPattern {
  def main(args: Array[String]): Unit = {
    val addContext = new Context(new Add())
    val prodContext = new Context(new Product())

    println("3 + 5 = " + addContext.executeOperation(3, 5))
    println("3 * 5 = " + prodContext.executeOperation(3, 5))
  }
}