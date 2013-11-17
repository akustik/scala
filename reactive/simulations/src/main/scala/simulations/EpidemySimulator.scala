package simulations

import math.random
import scala.util.Random

class EpidemySimulator extends Simulator {

  def randomBelow(i: Int) = (random * i).toInt

  protected[simulations] object SimConfig {
    val population: Int = 300
    val roomRows: Int = 8
    val roomColumns: Int = 8

    // to complete: additional parameters of simulation
    val prevalenceRate = 0.01
    val transmissibilityRate = 0.4
  }

  import SimConfig._

  val persons: List[Person] = {
    (for (i <- 0 until population) yield new Person(i)).toList
  }

  case class Room(row: Int, col: Int)

  class Person(val id: Int) {
    var infected = false
    var sick = false
    var immune = false
    var dead = false

    // demonstrates random number generation
    var row: Int = randomBelow(roomRows)
    var col: Int = randomBelow(roomColumns)

    //
    // to complete with simulation logic
    //

    private def getNeighbouringRooms: Array[Room] = {
      Array(
        Room((row - 1 + roomRows) % roomRows, col),
        Room((row + 1) % roomRows, col),
        Room(row, (col - 1 + roomColumns) % roomColumns),
        Room(row, (col + 1) % roomColumns))
    }

    def room: Room = Room(row, col)

    def setInfect = {
      if (canBeInfected) {
        infected = true
        afterDelay(6)(setSick)
        afterDelay(14)(maybeDie)
        afterDelay(16)(setImmune)
        afterDelay(18)(setHealthy)
      }
    }

    def setSick = {
      infected = true
      sick = true
    }

    def setDead = {
      infected = true
      sick = true
      dead = true
    }

    def setHealthy = {
      if (!dead) {
        infected = false
        sick = false
        immune = false
      }
    }

    def setImmune = {
      if (!dead) {
        infected = false
        sick = false
        immune = true
      }
    }

    def maybeInfect = {
      if (random < SimConfig.transmissibilityRate)
        setInfect
    }

    def maybeDie = {
      if (randomBelow(4) == 0)
        setDead
    }

    def seemsInfectuous = sick || dead

    def isInfectuous = infected || sick || dead || immune
    
    def canBeInfected = !infected && !immune && !dead

    def move: Unit = {
      //Dead people does not move
      if (!dead) {
        //Find neighbouring rooms
        val rooms = getNeighbouringRooms

        //Remove not suitable rooms: sick or dead people
        val safeRooms = rooms.filterNot(r => {
          persons.filter(_.room == r).exists(_.seemsInfectuous)
        })

        //If there are suitable rooms, move
        if (safeRooms.size > 0) {
          val r = safeRooms(randomBelow(safeRooms.size))
          row = r.row
          col = r.col
          
          //If there is infected people in the room, this person may get infected
          if (persons.filter(_.room == room).exists(_.isInfectuous))
           maybeInfect
        }

        //Trigger next move
        triggerMoveAction
      }
    }

    def triggerMoveAction = {
      afterDelay(randomBelow(5) + 1)(move)
    }
  }

  private def infectInitialPopulation = {
    val amount: Int = Math.round(SimConfig.prevalenceRate * SimConfig.population).toInt
    val shuffledPersons = (new Random).shuffle(persons)
    (shuffledPersons take amount).toList.foreach(_.setInfect)
  }

  private def triggerFirstMove = {
    persons.foreach(_.triggerMoveAction)
  }

  //initialization
  infectInitialPopulation
  triggerFirstMove
}
