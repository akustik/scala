package behavioral

object ObserverPattern {
  
  case class Event(who: String, where: String)
  
  trait Observer extends Ordered[Observer] {
    def id(): String
    def notify(e: Event)
    def compare(that: Observer) = {
      this.id().compareTo(that.id())
    }
  }
  
  trait Observable {
    private val observers: scala.collection.mutable.TreeSet[Observer] = new scala.collection.mutable.TreeSet[Observer]
    def attach(o: Observer) = observers += o
    def dettach(o: Observer) = observers -= o
    protected def notify(e: Event) = {
      observers.foreach(o => o.notify(e))
    }
  }
  
  class Cat(n: String) extends Observer {
    private val name = n
    def id(): String = name
    def notify(e: Event) = {
      println(name + ": Following the " + e.who + " to the " + e.where)
    }
  }
  
  class Mouse() extends Observable {
    def move(where: String) = {
      notify(new Event("mouse", where))
    }
  }

  /**
   * The OBSERVER pattern maintains a one-to-may relationship between an object and the observers
   * so that when the status of the object is changed, the observers are notified accordingly and
   * the can act in consequence.
   */
  def main(args: Array[String]): Unit = {
    
    //Let's pack the animals in the kitchen...
    val mouse = new Mouse
    val cat1 = new Cat("Felix")
    val cat2 = new Cat("Don Gato")
    val cat3 = new Cat("Garfield")
    
    //Oops, kitty kitty kitty! They've seen the mouse
    mouse.attach(cat1)
    mouse.attach(cat2)
    mouse.attach(cat3)
    
    //Hurry up!
    mouse.move("Bedroom")
    
    //Lasagna + Garfield
    mouse.dettach(cat3)
    mouse.move("Bathroom")
    
  }

}