package org.owing.physics

import org.owing.physics.Types._
import Math._

case class Rect(val topLeft: Point, val angle: AngleInRadians, val w: Int, val h: Int) extends Shape {
  private val dxW = cos(angle) * w
  private val dyW = sin(angle) * w
  private val dxH = cos(angle + toRadians(90)) * h
  private val dyH = sin(angle + toRadians(90)) * h
  lazy val topRight = Point(topLeft.x + dxW, topLeft.y - dyW)
  lazy val bottomLeft = Point(topLeft.x + dxH, topLeft.y - dyH)
  lazy val bottomRight = Point(topRight.x + dxH, topRight.y - dyH)
  lazy val center = Point(topLeft.x + dxW / 2 + dxH / 2, topLeft.y - dyW / 2 - dyH / 2)
  def contains(p: Point) = {
    if (angle != 0) {
      val rotatedPoint = (p - topLeft).rotate(angle, false) + topLeft
      Rect(topLeft, 0, w, h).contains(rotatedPoint)
    } else {
      val translatedPoint = p - bottomLeft
      translatedPoint.x >= 0 && translatedPoint.y >= 0 && translatedPoint.x <= w && translatedPoint.y <= h
    }
  }
  def intersects(s: Shape) = {
    s match {
      case r: Rect => r.contains(topLeft) || r.contains(topRight) || r.contains(bottomLeft) || r.contains(bottomRight)
      case _ => throw new java.lang.IllegalArgumentException("Unknown shape")
    }
  }
  def move(d: Int): Shape = {
    val distanceWithAngle = Distance(round(d * sin(angle)).toInt, round(d * cos(angle)).toInt)
    Rect(topLeft + distanceWithAngle, angle, w, h)
  }
  def rotate(r: AngleInRadians, cw: Boolean = true): Shape = {
    val rotatedTopLeft = (topLeft - center).rotate(r, cw)
    val rotatedTopRight = (topRight - center).rotate(r, cw)
    val rotatedTopRightFromOrigin = rotatedTopRight - rotatedTopLeft
    val newTopLeft = rotatedTopLeft + center
    Rect(newTopLeft, rotatedTopRightFromOrigin.angle - toRadians(90), w, h)
  }
  def circle(radius: Int, a: AngleInRadians, cw: Boolean = true): Shape = {
    val rotationPointAngle = 
      if(cw) toRadians(360) - angle
      else toRadians(180) - angle
    val rotationPointDistance = Distance(round(radius*cos(rotationPointAngle)).toInt, round(radius*sin(rotationPointAngle)).toInt)
    val rotationPoint = topLeft + rotationPointDistance
    val topLeftWithRotationPointOnOrigin = topLeft - rotationPoint
    val newTopLeft = topLeftWithRotationPointOnOrigin.rotate(a, cw) + rotationPoint
    Rect(newTopLeft, if(cw) angle + a else angle - a, w, h) 
  }
}
