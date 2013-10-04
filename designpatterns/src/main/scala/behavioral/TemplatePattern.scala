package behavioral

trait IntegerConversion {
  def getData(): List[String]
  def toIntOperation(): Unit = consolidateData(getData().map((_.toInt)))
  def consolidateData(data: List[Int])
}

class InMemoryIntegerConversion(source: List[String]) extends IntegerConversion {
  def getData(): List[String] = source
  def consolidateData(data: List[Int]) = "InMemoryIntegerConversion: " + println(data.mkString(","))
}

class InMemoryPositiveIntegerConversion(source: List[String]) extends IntegerConversion {
  def getData(): List[String] = source
  def consolidateData(data: List[Int]) = "InMemoryPositiveIntegerConversion: " + 
		  println((data.filter(_ > -1)).mkString(","))
}

/**
 * The TEMPLATE pattern uses an abstract class to implement an algorithm that delegates some
 * step of it to the concrete implementation so this could be changed without modifying the 
 * the algorithm structure. 
 */

object TemplatePattern {

  def main(args: Array[String]): Unit = {
    new InMemoryIntegerConversion(List("-1", "4", "7")).toIntOperation()
    new InMemoryPositiveIntegerConversion(List("-1", "4", "7")).toIntOperation()
  }

}