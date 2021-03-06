package org.owing.physics

import org.owing.physics.Types._

trait Shape {
  def contains(p: Point): Boolean
  def intersects(s: Shape): Boolean
  def center(): Point
  def angle(): AngleInRadians
  def move(d: Int): Shape
  def rotate(a: AngleInRadians, cw: Boolean = true): Shape
  def circle(radius: Int, a: AngleInRadians, cw: Boolean = true): Shape
}
