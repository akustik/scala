package fizzbuzz

import org.scalatest.FlatSpec

class FizzBuzzSpecTest extends FlatSpec {

  val f = new FizzBuzz

  "The fizzbuzz execution" should "of be a list of a hundread elements" in {
    assert(f.execute.size === 100)
  }

  it should "have the string 1 in the first position" in {
    assert(f.execute.head === "1")
  }

  it should "have the string 2 in the second position" in {
    assert(f.execute.tail.head === "2")
  }

  it should "have Fizz in the third position" in {
    assert(f.execute.tail.tail.head === "Fizz")
  }

  it should "have Buzz in the fifth position" in {
    assert((f.execute drop 4).head === "Buzz")
  }

  it should "have FizzBuzz in the 15th position" in {
    assert((f.execute drop 14).head === "FizzBuzz")
  }

}
