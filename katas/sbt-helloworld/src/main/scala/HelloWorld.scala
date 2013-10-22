object HelloWorld {
 def main(args: Array[String]): Unit = {
  val a = args mkString " "
  println("hello world: " + a)
 }
}
