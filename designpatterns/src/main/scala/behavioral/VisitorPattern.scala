package behavioral

trait Visitable {
  def accept(v: Visitor)
}

class Friend extends Visitable {
  def accept(v: Visitor) = v.visitFriend(this)
  def lendMoney(): Int = 40
}

class Mother extends Visitable {
  def accept(v: Visitor) = v.visitMother(this)
  def getFood(): String = "Sausages"
}

class People(f: Friend, m: Mother) {
  def accept(v: Visitor) = {
    v.visitFriend(f)
    v.visitMother(m)
  }
}

trait Visitor {
  def visitFriend(f: Friend)
  def visitMother(m: Mother)
}

class HelloVisitor extends Visitor {
  def visitFriend(f: Friend) = println("hi, how's going?")
  def visitMother(m: Mother) = println("hello mum! Somehting in the fridge?")
}

class InterestedVisitor extends Visitor{
  var amountOfMoney = 0
  var thingsToEat = List[String]()
  def visitFriend(f: Friend) = amountOfMoney = amountOfMoney + f.lendMoney
  def visitMother(m: Mother) = thingsToEat = m.getFood +: thingsToEat
  def letsSeeWhatWeGot() = println("Got " + amountOfMoney + 
      " coins and this to eat: " + thingsToEat.mkString(","))
}

/**
 * The VISITOR template allows to perform several different operations in a 
 * structure of elements without changing these elements when adding new 
 * operations.
 */

object VisitorPattern {
  def main(args: Array[String]): Unit = {
    val people = new People(new Friend, new Mother)
    val helloVisitor = new HelloVisitor
    val interestedVisitor = new InterestedVisitor
    
    people.accept(helloVisitor)
    people.accept(interestedVisitor)
    interestedVisitor.letsSeeWhatWeGot()
    
  }
}