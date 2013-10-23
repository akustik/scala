import org.scalatest.FlatSpec

class FlatSpecTest extends FlatSpec {

  "A flat spec test" should "not fail when passes" in {
    assert("a" === "a")
  }

}
