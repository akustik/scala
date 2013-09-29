package behavioral

object MementoPattern {

  class Originator(initialState: Int) {
    private var state = initialState

    def changeState(otherState: Int) = {
      this.state = otherState
    }

    def setMemento(m: Memento) = {
      this.state = m.state;
    }

    def createMemento() = {
      new Memento(this.state)
    }
    
    override def toString() = {
      "state: " + this.state
    }
  }

  case class Memento(state: Int)

  /**
   * The MEMENTO pattern allows to capture and externalize an object internal
   * state without breaking the encapsulation, so this state can be recovered
   * later on.  
   */
  def main(args: Array[String]) {
    //I'm the care taker of the originator
    
    //Set up the system
    val originator = new Originator(0)
    originator.changeState(1);
    originator.changeState(2);
    
    //Create the memento to backup the state 2
    val memento = originator.createMemento;
    
    //Further modifications
    originator.changeState(5)
    originator.changeState(1000)
    
    //Whoa! We need the original state now
    originator.setMemento(memento)
    
    //It's all right now :)
    println(originator)
    
  }

}