package org.owing.physics

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import org.owing.Game
import org.owing.Cmd._

@RunWith(classOf[JUnitRunner])
class GameFlatSpec extends FlatSpec with ShouldMatchers {
  
  "A game" should "add starships" in {
    val game = new Game(0, 1000, 1000).
      addStarShip("rebel", Point(200, 200)).
      addStarShip("empire", Point(800, 200));
    
    game.elements.size should be(2)
  }
  
  it should "move starships" in {
    val game = new Game(0, 1000, 1000).
      addStarShip("rebel", Point(200, 200)).
      move("rebel", Forward(5));    
    val movedStarShip = game.getStarShip("rebel")
    
    movedStarShip.id should be("rebel")
    movedStarShip.shape.center should be(Point(225, 175 + 250))
  }
}
