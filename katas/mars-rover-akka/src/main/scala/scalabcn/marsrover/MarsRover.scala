package scalabcn.marsrover

import akka.actor.{ActorRef, ActorLogging, Actor}
import akka.event.LoggingReceive
import scalabcn.marsrover.MarsRover._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

case class Position(x: Int, y: Int) {

  def +(vel: Int): Position = Position((x + vel) % 10, y)

}

object MarsRover {

  case object SelfDestruct

  case object StartEngine

  case object StopEngine

  private case object Tick

  case object Subscribe

}

class MarsRover extends Actor with ActorLogging {

  var position: Position = Position(0, 0)
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
        position = position + 1
        subscribers.foreach(s => s ! position)
      }
    case SelfDestruct => context.stop(self)
  }

  private def engineReceive:Receive = LoggingReceive {
    case StartEngine => running = true
    case StopEngine => running = false
  }



}
