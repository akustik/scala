package org.owing.physics

import org.owing.physics.Types._
import Math._

case class Point(x: Int, y: Int) {
  def +(o: Point) = Point(x + o.x, y + o.y)
  def +(d: Distance) = Point(x + d.x, y + d.y)
  def -(o: Point) = Point(x - o.x, y - o.y)
  def -(d: Distance) = Point(x - d.x, y - d.y)
  def /(a: Int) = Point(x / a, y / a)
  def rotate(r: AngleInRadians, cw: Boolean = true) = {
    if (cw) Point(x * cos(r) + y * sin(r), -x * sin(r) + y * cos(r))
    else Point(x * cos(r) - y * sin(r), x * sin(r) + y * cos(r))
  }
  def distance(p: Point) = sqrt(pow(x - p.x, 2) + pow(y - p.y, 2))
  def angle(): AngleInRadians = atan2(x, y)
}

object Point {
  def apply(x: Float, y: Float): Point = new Point(round(x), round(y))
  def apply(x: Double, y: Double): Point = Point(x.toFloat, y.toFloat)
}
