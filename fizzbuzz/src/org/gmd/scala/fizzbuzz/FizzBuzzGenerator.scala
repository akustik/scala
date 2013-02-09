package org.gmd.scala.fizzbuzz

class FizzBuzzGenerator {

  private val fizz = "Fizz"
  private val buzz = "Buzz"

  def generate(n: Int): String = {
    if (n > 0) {
      generate(n - 1) + " " + generateElement(n)
    } else
      ""
  }

  private def generateElement(n: Int): String = {
    if (n % 15 == 0) {
      fizz + buzz
    } else if (n % 5 == 0) {
      buzz
    } else if (n % 3 == 0) {
      fizz
    } else {
      "%d".format(n)
    }
  }
}
