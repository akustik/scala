package recfun
import common._

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if (c == r || c == 0)
      1
    else
      pascal(c - 1, r - 1) + pascal(c, r - 1)
  }

  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean = {
    doBalance(chars, 0)
  }

  def doBalance(chars: List[Char], opened: Int): Boolean = {
    if (opened == -1) {
      false
    } else if (chars.isEmpty) {
      opened == 0
    } else if (chars.head == '(') {
      doBalance(chars.tail, opened + 1)
    } else if (chars.head == ')') {
      doBalance(chars.tail, opened - 1)
    } else {
      doBalance(chars.tail, opened)
    }
  }

  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    if(money <= 0) 0
    else if (coins.isEmpty) 0
    else {
      doCountChange(money, coins)
    }
    
  }
  
  def doCountChange(money: Int, coins: List[Int]): Int = {
    if(money == 0) 1
    else if(coins.isEmpty) 0
    else if(coins.head <= money){
      doCountChange(money - coins.head, coins) + doCountChange(money, coins.tail)
    } else {
      doCountChange(money, coins.tail)
    }
  }
}
