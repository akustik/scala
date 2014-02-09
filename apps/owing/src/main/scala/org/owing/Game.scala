package org.owing

import Math._

case class Point(x: Int, y: Int) {
  def +(o: Point) = Point(x + o.x, y + o.y)
  def -(o: Point) = Point(x - o.x, y - o.y)
  def /(a: Int) = Point(x/a, y/a)
  def rotate(r: Double, cw: Boolean = true) = {
    if(cw) Point(x * cos(r) + y * sin(r), -x * sin(r) + y * cos(r))
    else Point(x * cos(r) - y * sin(r), x * sin(r) + y * cos(r))
  }
  def distance(p: Point) = sqrt(pow(x - p.x, 2) + pow(y - p.y, 2))
  def angle() = atan2(x, y) 
}

object Point {
  def apply(x: Float, y: Float): Point = new Point(round(x), round(y))
  def apply(x: Double, y: Double): Point = Point(x.toFloat, y.toFloat)
}

trait Shape {
  def contains(p: Point): Boolean
  def intersects(s: Shape): Boolean
  def center(): Point
}

class Rect(val topLeft: Point, angle: Double, w: Int, h: Int) extends Shape {
  override def intersects(s: Shape) = false 
  private val dxW = cos(angle) * w
  private val dyW = sin(angle) * w
  private val dxH = cos(angle + toRadians(90)) * h
  private val dyH = sin(angle + toRadians(90)) * h
  val topRight = Point(topLeft.x + dxW, topLeft.y - dyW)
  val bottomLeft = Point(topLeft.x + dxH, topLeft.y - dyH)
  val bottomRight = Point(topRight.x + dxH, topRight.y - dyH)
  override def center = Point(topLeft.x + dxW/2 + dxH/2, topLeft.y - dyW/2 - dyH/2) 
  override def contains(p: Point) = {
    if(angle != 0) {
      val rotatedPoint = (p - topLeft).rotate(angle, false) + topLeft
      Rect(topLeft, 0, w, h).contains(rotatedPoint)
    } else {
      val translatedPoint = p - bottomLeft
      translatedPoint.x >= 0 && translatedPoint.y >= 0 && translatedPoint.x <= w && translatedPoint.y <= h
    }
  }
}

object Rect {
  def apply(topLeft: Point, angle: Double, w: Int, h: Int) = new Rect(topLeft, angle, w, h)
}

trait BoardElement {
  def id(): String
}
trait Movement {
}

trait Board {
  def place(element: BoardElement, center: Point)
  def move(id: String, m: Movement)
}

object Game {

}
