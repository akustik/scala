package scalabcn.marsrover

import akka.actor.{ActorLogging, Props, Actor}
import akka.event.LoggingReceive
import scalabcn.marsrover.MarsSatellite.{StartMission, AbortMission}
import scalabcn.marsrover.MarsRover.SelfDestruct

object MarsSatellite {

  case object AbortMission

  case object StartMission

}

class MarsSatellite extends Actor with ActorLogging{

  val rover = context.actorOf(Props[MarsRover], "mars-rover")

  override def receive = LoggingReceive {
    case AbortMission =>
      rover ! SelfDestruct
      context.stop(self)
    case StartMission =>
  }
}


