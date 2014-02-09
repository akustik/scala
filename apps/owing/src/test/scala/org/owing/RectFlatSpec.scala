package org.owing

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import org.owing._
import Math._

@RunWith(classOf[JUnitRunner])
class RectFlatSpec extends FlatSpec with ShouldMatchers {

  val r1 = Rect(Point(0, 10), 0, 20, 10)
  val r2 = Rect(Point(0, 10), toRadians(45), 20, 10)
 
  "topRight" should "return the top right point of the rectangle" in {
    r1.topRight should be(Point(20, 10))
    r2.topRight should be(Point(14, -4))
  }

  "bottomLeft" should "return the bottom left point of the rectangle" in {
    r1.bottomLeft should be(Point(0, 0))
    r2.bottomLeft should be(Point(-7, 3))
  }

  "bottomRight" should "return the bottom right point of the rectangle" in {
    r1.bottomRight should be(Point(20, 0))
    r2.bottomRight should be(Point(7, -11))
  }

  "center" should "return the center point" in {
    r1.center should be(Point(10, 5))
    r2.center should be(Point(4, -1))
  }

  "contains" should "return true for contained points in r1" in {
    r1.contains(Point(5,5)) should be(true)
    r1.contains(r1.topLeft) should be(true)
  } 

  it should "return false for non contained points in r1" in {
    r1.contains(Point(20,20)) should be(false)
  }

  it should "return true for contained points in r2" in {
    r2.contains(Point(3, 3)) should be(true)
    r2.contains(r2.topLeft) should be(true)
    r2.contains(r2.bottomLeft) should be(true)
    r2.contains(r2.topRight) should be(true)
    r2.contains(r2.bottomRight) should be(true)
  }

  it should "return false for non contained points in r2" in {
    r2.contains(Point(9,2)) should be(false)
    r2.contains(Point(2,9)) should be(false)
    r2.contains(Point(12, -1)) should be(false)
    r2.contains(Point(-12, 0)) should be(false)
  }

}
