package org.gmd

import scala.annotation.tailrec

trait Direction {
  def toString: String
  def opposite: Direction
  def left: Direction
  def right: Direction = left.opposite
}

case object North extends Direction {
  override def toString = "N"
  override def opposite = South
  override def left = West
}
case object South extends Direction {
  override def toString = "S"
  override def opposite = North
  override def left = East
}
case object East extends Direction {
  override def toString = "E"
  override def opposite = West
  override def left = North
}
case object West extends Direction {
  override def toString = "W"
  override def opposite = East
  override def left = South
}

case class Coordinate(x: Int, y: Int)
case class Edge(d: Direction, to: Location)
case class Rover(c: Coordinate, d: Direction)

case class Location(c: Coordinate, edges: Map[Direction, Coordinate]) {
  def moveForward(d: Direction) = edges.getOrElse(d, c)
  def moveBackwards(d: Direction) = edges.getOrElse(d.opposite, c)
}

trait Terrain {
  def locate(c: Coordinate): Location
}

class Grid(size: Int) extends Terrain {
  
  private val coordinates = for {
    x <- 0 until size
    y <- 0 until size
  } yield Coordinate(x,y)
  
  private def getCoordinateOrDefault(c: Coordinate, d: Coordinate): Coordinate =
    if(coordinates.contains(c)) c else d
  
  private val locations = for {
    c <- coordinates
  } yield Location(c, Map(
    North -> getCoordinateOrDefault(Coordinate(c.x, c.y + 1), c),
    South -> getCoordinateOrDefault(Coordinate(c.x, c.y - 1), c),
    East -> getCoordinateOrDefault(Coordinate(c.x + 1, c.y), c),
    West -> getCoordinateOrDefault(Coordinate(c.x - 1, c.y), c)
  ))
  
  override def locate(c: Coordinate) = {
    val res = locations.filter(_.c == c)
    if(res.size == 1) res(0)
    else throw new IllegalArgumentException("Invalid coordinate " + c)
  }
}

trait MarsRoverAPI {
  def command(r: Rover, args: Iterable[Char]): Rover
}

class MarsRoverGrid(size: Int) extends MarsRoverAPI {
  val terrain = new Grid(size)
  
  @tailrec
  final def command(r: Rover, args: Iterable[Char]): Rover = {
    val location = terrain.locate(r.c)
    args match {
      case Nil => r
      case 'f' :: xc => command(Rover(location.moveForward(r.d), r.d), xc)  
      case 'b' :: xc => command(Rover(location.moveBackwards(r.d), r.d), xc)
      case 'l' :: xc => command(Rover(r.c, r.d.left), xc)
      case 'r' :: xc => command(Rover(r.c, r.d.right), xc)
      case c :: xc => throw new IllegalArgumentException("Invalid coordinate " + c)
    }    
  }
}


