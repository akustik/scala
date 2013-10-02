package behavioral

trait Aggregate[T] {
  def createIterator: Iterator[T]
}

trait Iterator[T] {
  def next(): T
}

class MyBizarreAggregation(a: String, b: String, c: String) extends Aggregate[String] {
  val _a = a
  val _b = b
  val _c = c
  def createIterator(): Iterator[String] = new MyBizarreAggregationIterator(this)
}

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

object IteratorPattern {
  def main(args: Array[String]): Unit = {
    val aggregation = new MyBizarreAggregation("a", "b", "c")
    val itAggregation = aggregation.createIterator()
    var s: String = ""
    do {
      s = itAggregation.next
      println("Element: " + s)
    }while(!s.isEmpty)
    
  }
}