package org.owing

import org.owing.physics._
import Math._

object Cmd {
  type MovementUnits = Int

  trait Movement {
    def units: MovementUnits
  }

  case class Forward(units: MovementUnits) extends Movement
  case class RotateRight(units: MovementUnits) extends Movement
  case class RotateLeft(units: MovementUnits) extends Movement
  case class Loop(units: MovementUnits) extends Movement
  case class TurnRight(units: MovementUnits) extends Movement
  case class TurnLeft(units: MovementUnits) extends Movement
}

case class ShipStatus(withCollision: Boolean = false)

case class StarShip(val id: String, val shape: Shape, val status: ShipStatus = ShipStatus()){
  val minForward = 1
  val maxForward = 5
}

class Game(val idx: Int, val w: Int, val h: Int, ships: Map[String, StarShip] = Map(), history: List[Game] = Nil) {
  import org.owing.Cmd._

  def addStarShip(id: String, topLeft: Point, angle: Double = 0) = {
    new Game(idx + 1, w, h, ships + (id -> StarShip(id, Rect(topLeft, toRadians(angle), 50, 50))), this :: history)
  }

  def move(id: String, m: Movement) = {
    val s = ships(id)
    m match {
      case c: Cmd.Forward => {
        assert(c.units >= s.minForward && c.units <= s.maxForward)
        val newShape = s.shape.move(c.units * 50)
        val shipsToCollide = (ships - s.id).filter((e) => e._2.shape.intersects(newShape)).toList
        val newStatus = ShipStatus(!shipsToCollide.isEmpty)
        new Game(idx + 1, w, h, ships + (s.id -> StarShip(s.id, newShape, newStatus)), this :: history)
      }
      case c: Cmd.RotateRight => {
        new Game(idx + 1, w, h, ships + (s.id -> StarShip(s.id, s.shape.circle(c.units * 50, toRadians(90), true))), this :: history)
      }
      case c: Cmd.RotateLeft => {
        new Game(idx + 1, w, h, ships + (s.id -> StarShip(s.id, s.shape.circle(c.units * 50, toRadians(90), false))), this :: history)
      }
      case c: Cmd.Loop => {
        new Game(idx + 1, w, h, ships + (s.id -> StarShip(s.id, s.shape.move(c.units * 50).rotate(toRadians(180)))), this :: history) 
      }
      case c: Cmd.TurnRight => {
        new Game(idx + 1, w, h, ships + (s.id -> StarShip(s.id, s.shape.circle(c.units * 50, toRadians(45), true))), this :: history)
      }
      case c: Cmd.TurnLeft => {
        new Game(idx + 1, w, h, ships + (s.id -> StarShip(s.id, s.shape.circle(c.units * 50, toRadians(45), false))), this :: history)
      }      
    }
  }

  def getStarShip(id: String) = ships(id)

  def elements(): Seq[StarShip] = {
    ships.values.toSeq
  }

  def status(): List[Game] = {
    (this :: history).reverse
  }
}




