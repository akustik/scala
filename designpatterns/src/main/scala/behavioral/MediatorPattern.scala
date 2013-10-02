package behavioral

//Colleague
trait Animal {
  def execute()
}

class Dog(m: Mediator) extends Animal {
  def execute() = {
    println("Dog: Bub!")
    m.bub()
  }
  m.registerDog(this)
  
  def noop() = println("Dog: Doing nothing...")
  def attack() = println("Dog: Attacking!")
}

class Cat(m: Mediator) extends Animal {
  def execute() = {
    println("Cat: Meow!")
    m.meow()
  }
  m.registerCat(this)
  
  def noop() = println("Cat: Doing nothing...")
  def hide() = println("Cat: Hide somewhere")
}

trait Mediator {
  def registerCat(c: Cat)
  def registerDog(d: Dog)
  def meow()
  def bub()
}

//The animals are in different rooms ;)
class CarefulMediator extends Mediator {
  var kitty: Cat = null
  var puppy: Dog = null
  def registerCat(c: Cat) = { kitty = c }
  def registerDog(d: Dog) = { puppy = d }
  def meow() = puppy.noop
  def bub() = kitty.noop
}

//The animals are kept in the same room, you know...
class NotSoCarefulMediator extends Mediator {
  var kitty: Cat = null
  var puppy: Dog = null
  def registerCat(c: Cat) = { kitty = c }
  def registerDog(d: Dog) = { puppy = d }
  def meow() = puppy.attack
  def bub() = kitty.hide
}

/**
 * The MEDIATOR pattern allows to have a weak relation between a set of 
 * COLLEAGUES. The mediator stores how the objects interact between them
 * avoiding to have direct references. The colleagues notify the mediator
 * of their existence and communicate events to them; the concrete mediator
 * performs the interaction between the colleagues
 */

object MediatorPattern {
  def main(args: Array[String]): Unit = {
    println("At home...")
    val m1 = new CarefulMediator
    val cat1 = new Cat(m1)
    val dog1 = new Dog(m1)
    cat1.execute()
    dog1.execute()
        
    println("In another home...")
    val m2 = new NotSoCarefulMediator
    val cat2 = new Cat(m2)
    val dog2 = new Dog(m2)
    
    cat2.execute()
    dog2.execute()
  }
}