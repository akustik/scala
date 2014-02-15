package org.owing.physics

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
  val r3 = Rect(Point(0, 100), toRadians(45), 200, 100)
  val r4 = Rect(Point(200, 200), 0, 50, 50)
  val r5 = Rect(Point(0, 100), 0, 200, 100)
  val r6 = Rect(Point(50, 50), toRadians(90), 10, 10)

  "A rect" should "have a top right point" in {
    r1.topRight should be(Point(20, 10))
    r2.topRight should be(Point(14, -4))
  }

  it should "have an angle (from x-axis)" in {
    r1.angle should be(0)
    r2.angle should be(toRadians(45))
  }
  
  it should "have a bottom left point" in {
    r1.bottomLeft should be(Point(0, 0))
    r2.bottomLeft should be(Point(-7, 3))
  }

  it should "have a bottom right point of the rectangle" in {
    r1.bottomRight should be(Point(20, 0))
    r2.bottomRight should be(Point(7, -11))
  }

  it should "have a center point of the rectangle" in {
    r1.center should be(Point(10, 5))
    r2.center should be(Point(4, -1))
  }

  it should "return whether a point is contained into its boundaries (including boundaries)" in {
    r1.contains(Point(5, 5)) should be(true)
    r1.contains(r1.topLeft) should be(true)
    r1.contains(Point(20, 20)) should be(false)
    r2.contains(Point(3, 3)) should be(true)
    r2.contains(r2.topLeft) should be(true)
    r2.contains(r2.bottomLeft) should be(true)
    r2.contains(r2.topRight) should be(true)
    r2.contains(r2.bottomRight) should be(true)
    r2.contains(Point(9, 2)) should be(false)
    r2.contains(Point(2, 9)) should be(false)
    r2.contains(Point(12, -1)) should be(false)
    r2.contains(Point(-12, 0)) should be(false)
  }

  it should "displace forward the given distance keeping the same angle" in {
    val r5t = r5.move(2)
    r5t.center should be(Point(100, 52))
    r5t.angle should be(r5.angle)
    val r2t = r2.move(2)
    r2t.center should be(Point(5, 0))
    r2t.angle should be(r2.angle)
    val r4t = r4.move(250)
    r4t.center should be(Point(225, 425))
    r4t.angle should be(r4.angle)
  }
  
  it should "turn the given radians keeping the same center" in {
    val r1r = r1.rotate(toRadians(90))
    r1r.angle should be(r1.angle + toRadians(90))
    r1r.center should be(r1.center)
    val r3r = r3.rotate(toRadians(45))
    r3r.angle should be(r3.angle + toRadians(45))
    r3r.center should be(r3.center)
  }

  it should "move in circle counter clockwise 90 degrees from initial position" in {
    val r1c = r1.circle(20, toRadians(90), false)
    r1c.angle should be(toRadians(-90))
    r1c.center should be(Point(-15, 40))
  }

  it should "move in circle clockwise 90 degrees from initial position" in {
    val r1c = r1.circle(20, toRadians(90), true)
    r1c.angle should be(toRadians(90))
    r1c.center should be(Point(15, 20))
  }

  it should "move in circle clockwise 90 degrees when already facing right" in {
    val r6c = r6.circle(10, toRadians(90), true)
    r6c.angle should be(toRadians(180))
    r6c.center should be(Point(55, 45))
  }
}
