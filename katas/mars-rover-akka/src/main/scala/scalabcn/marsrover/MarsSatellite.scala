package scalabcn.marsrover

import akka.actor.{ActorLogging, Props, Actor}
import akka.event.LoggingReceive
import scalabcn.marsrover.MarsSatellite.{StartMission, AbortMission}
import scalabcn.marsrover.MarsRover.{SelfDestruct, Subscribe, StartEngine, StopEngine}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

object MarsSatellite {

  case object AbortMission

  case object StartMission

}

class MarsSatellite extends Actor with ActorLogging{

  val rover = context.actorOf(Props[MarsRover], "mars-rover")

  override def receive = LoggingReceive {
    case AbortMission =>
      rover ! StopEngine
      rover ! SelfDestruct

    case SelfDestruct => 
      log.info("SelfDestruct message received from the rover")
      import ExecutionContext.Implicits.global
      context.system.scheduler.scheduleOnce(2 seconds) {
  	    context.stop(self)
      }

    case StartMission => 
      rover ! Subscribe
      rover ! StartEngine

    case status: Status => 
      log.info(s"MarsRover updated its status to $status")

    case newDirection: Direction => 
      log.info(s"Forwarding new direction order to the mars rover...")
      rover ! newDirection
  }
}


