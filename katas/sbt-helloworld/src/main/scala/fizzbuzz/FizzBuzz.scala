package fizzbuzz

class FizzBuzz {
  def execute = {
     def isMultiple(mod: Int)(i: Int): Boolean = i % mod == 0 
     val isFizz  = isMultiple(3)(_)
     val isBuzz  = isMultiple(5)(_) 

    (1 to 100).map(x => x match {
      case x: Int if isFizz(x) && isBuzz(x) => "FizzBuzz"
      case x: Int if isFizz(x) => "Fizz"
      case x: Int if isBuzz(x) => "Buzz"
      case y: Int => y.toString
    })
  }
}

