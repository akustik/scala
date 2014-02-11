package org.owing

import org.owing.physics._
import Math._

import scala.collection.mutable.Map

object Cmd {
  type MovementUnits = Int

  trait Movement {
    def units: MovementUnits
  }

  case class Forward(units: MovementUnits) extends Movement
  case class RotateRight(units: MovementUnits) extends Movement
  case class RotateLeft(units: MovementUnits) extends Movement
}

trait BoardElement {
  def id(): String
  def shape(): Shape
}

class Game(val w: Int, val h: Int) {
  import org.owing.Cmd._

  case class StarShip(val id: String, val shape: Shape) extends BoardElement {
    val minForward = 1
    val maxForward = 5
  }

  val ships = Map[String, StarShip]()

  def addStarShip(id: String, topLeft: Point) = {
    ships += (id -> StarShip(id, Rect(topLeft, 0, 50, 50)))
  }

  def move(id: String, m: Movement) = {
    ships.get(id).foreach(s => {
      m match {
        case f: Cmd.Forward => {
          assert(f.units >= s.minForward && f.units <= s.maxForward)
          ships += (s.id -> StarShip(s.id, s.shape.move(f.units * 50)))
        }
        case r: Cmd.RotateRight => {
          ships += (s.id -> StarShip(s.id, s.shape.rotate(toRadians(r.units), true)))
        }
        case l: Cmd.RotateLeft => {
          ships += (s.id -> StarShip(s.id, s.shape.rotate(toRadians(l.units), false)))
        }
      }
    })
  }

  def getStarShip(id: String) = ships(id)

  def elements(): Seq[BoardElement] = {
    ships.values.toSeq
  }
}




