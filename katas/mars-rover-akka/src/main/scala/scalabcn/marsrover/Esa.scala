package scalabcn.marsrover

import akka.actor.{Terminated, Props, Actor}
import akka.event.LoggingReceive
import scalabcn.marsrover.MarsSatellite.StartMission

class Esa extends Actor {

  val satellite = context.actorOf(Props(classOf[MarsSatellite]), "mars-satellite")
  context.watch(satellite)

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