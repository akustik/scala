package scalabcn.marsrover

import akka.actor.{ActorRef, ActorLogging, Actor}
import akka.event.LoggingReceive
import scalabcn.marsrover.MarsRover._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

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

case class Position(x: Int, y: Int)

case class Status(p: Position, d: Direction) {
  def +(vel: Int): Status = d match {
    case East => Status(Position((p.x + vel) % 10, p.y), d)
    case West => Status(Position((p.x - vel + 10) % 10, p.y), d)
    case North => Status(Position(p.x, (p.y + vel) % 10), d)
    case South => Status(Position(p.x, (p.y - vel + 10) % 10), d)
  }
  def face(dir: Direction) = Status(p, dir)
}

object MarsRover {

  case object SelfDestruct

  case object StartEngine

  case object StopEngine

  private case object Tick

  case object Subscribe

}

class MarsRover extends Actor with ActorLogging {

  var status: Status = Status(Position(0, 0), East)
  var running: Boolean = false
  var subscribers = List.empty[ActorRef]

  import ExecutionContext.Implicits.global
  context.system.scheduler.schedule(1 second, 1 second, self, Tick)


  override def receive = internalReceive orElse engineReceive

  private def internalReceive:Receive = {
    case Subscribe =>
      subscribers = sender :: subscribers
    case Tick =>
      if (running){
        status = status + 1
        subscribers.foreach(s => s ! status)
      }
    case SelfDestruct => {
      running = false
      subscribers.foreach(s => s ! SelfDestruct)
      context.stop(self)
    }
    case newDirection: Direction => {
      if (running){
        status = status face newDirection
        subscribers.foreach(s => s ! status)
      }
    }
  }

  private def engineReceive:Receive = LoggingReceive {
    case StartEngine => running = true
    case StopEngine => running = false
  }
}
