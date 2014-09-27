/**
 * Created by guillermo.mercadal on 27/09/14.
 */

import collection.mutable.Stack
import org.scalatest._

class GreetingSpec extends FlatSpec with Matchers {

  "A Greeting" should "say hi to the proper person" in {
    val greeting = new Greeting
    greeting.greet("John") should be ("hi John")
  }

  it should "throw IllegalArgumentException if it's me" in {
    val greeting = new Greeting
    a [IllegalArgumentException] should be thrownBy {
      greeting.greet("me")
    }
  }

}
