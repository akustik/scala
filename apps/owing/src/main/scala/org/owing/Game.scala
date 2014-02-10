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
    m match {
      case f: Cmd.Forward => {
        ships.get(id).foreach(s => {
          assert(m.units >= s.minForward && m.units <= s.maxForward)
          val shipWithNewLocation = StarShip(s.id, s.shape.move(m.units * 50))
          println(shipWithNewLocation)
          ships += (s.id -> shipWithNewLocation)
        })
      }
    }
  }

  def getStarShip(id: String) = ships(id)

  def elements(): Seq[BoardElement] = {
    ships.values.toSeq
  }
}




