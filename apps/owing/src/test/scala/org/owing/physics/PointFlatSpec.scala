package org.owing.physics

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import org.owing._
import Math._

@RunWith(classOf[JUnitRunner])
class PointFlatSpec extends FlatSpec with ShouldMatchers {

  "A point" should "rotate clockwise the given radians" in {
    Point(0, 1).rotate(toRadians(90)) should be(Point(1, 0))
  }

  it should "rotate counter clockwise the given radians" in {
    Point(1, 0).rotate(toRadians(90), false) should be(Point(0, 1))
  }

  it should "add another point" in {
    Point(2, 2) + Point(3, 4) should be(Point(5, 6))
  }

  it should "remove another point" in {
    Point(2, 2) - Point(3, 4) should be(Point(-1, -2))
  }

  it should "divide a point by a magnitude" in {
    Point(2, 2) / 2 should be(Point(1, 1))
  }

  it should "compute the euclidean distance to another other point" in {
    Point(2, 2).distance(Point(2, 4)) should be(2)
  }

  it should "compute the angle of the point to the origin" in {
    Point(1, 0).angle should be(toRadians(90))
    Point(0, 1).angle should be(0)
    Point(1, 1).angle should be(toRadians(45))
    Point(0, -1).angle should be(toRadians(180))
  }
}
