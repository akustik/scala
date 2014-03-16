package scalabcn.marsrover

import akka.actor.{Terminated, Props, Actor}
import akka.event.LoggingReceive
import scalabcn.marsrover.MarsSatellite.{StartMission, AbortMission}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class Esa extends Actor {

  val satellite = context.actorOf(Props(classOf[MarsSatellite]), "mars-satellite")
  context.watch(satellite)

  satellite ! StartMission

  import ExecutionContext.Implicits.global
  context.system.scheduler.scheduleOnce(2 seconds) {
  	satellite ! West
  }

  context.system.scheduler.scheduleOnce(10 seconds) {
  	satellite ! AbortMission
  }

  override def receive = LoggingReceive {
    case Terminated(_) =>
      context.stop(self)
  }
}

object Esa {
  def main(args: Array[String]): Unit = {
    akka.Main.main(Array(classOf[Esa].getName))
  }

}