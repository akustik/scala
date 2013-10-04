package behavioral

trait Aggregate[T] {
  def createIterator: Iterator[T]
}

trait Iterator[T] {
  def next(): T
}

class MyBizarreAggregation(a: String, b: String, c: String) extends Aggregate[String] {
  private val _a = a
  private val _b = b
  private val _c = c
  def createIterator(): Iterator[String] = new MyBizarreAggregationIterator(this)

  class MyBizarreAggregationIterator(aggr: MyBizarreAggregation) extends Iterator[String] {
    var idx = 0
    def next(): String = {
      idx = idx + 1
      idx match {
        case 1 => aggr._b
        case 2 => aggr._c
        case 3 => aggr._a
        case _ => ""
      }
    }
  }
}

/**
 * The ITERATOR pattern defines a way of traversing a given collection without showing
 * the concrete implementation of the aggregation.
 */

object IteratorPattern {
  def main(args: Array[String]): Unit = {
    val aggregation = new MyBizarreAggregation("a", "b", "c")
    val itAggregation = aggregation.createIterator()
    var s: String = ""
    do {
      s = itAggregation.next
      println("Element: " + s)
    } while (!s.isEmpty)

  }
}