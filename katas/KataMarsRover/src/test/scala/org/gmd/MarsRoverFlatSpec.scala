package org.gmd

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MarsRoverSuite extends FunSuite {

  class TestData {
    lazy val g10: MarsRoverAPI = new MarsRoverGrid(10)
  }

  test("Grid 10x10#moves to 0,1N from 0,0N when f") {
    val api = new TestData().g10
    assert(api.command(Rover(Coordinate(0, 0), North), "f".toList) ===
      Rover(Coordinate(0, 1), North))
  }

  test("Grid 10x10#moves to 1,1E from 0,0N when frf") {
    val api = new TestData().g10
    assert(api.command(Rover(Coordinate(0, 0), North), "frf".toList) ===
      Rover(Coordinate(1, 1), East))
  }

  test("Grid 10x10#moves to 2,0N from 0,0N when frfflb") {
    val api = new TestData().g10
    assert(api.command(Rover(Coordinate(0, 0), North), "frfflb".toList) ===
      Rover(Coordinate(2, 0), North))
  }
  
  test("Grid 10x10#is able to do a circle with ffrffrffrffr") {
    val api = new TestData().g10
    assert(api.command(Rover(Coordinate(0, 0), North), "ffrffrffrffr".toList) ===
      Rover(Coordinate(0, 0), North))
  }
  
  test("Grid 10x10#is able to do a circle with lbblbblbblbb") {
    val api = new TestData().g10
    assert(api.command(Rover(Coordinate(0, 0), North), "lbblbblbblbb".toList) ===
      Rover(Coordinate(0, 0), North))
  }

  test("Grid 10x10#raises error when source coordinate is invalid -1,0") {
    val api = new TestData().g10
    intercept[IllegalArgumentException] {
      api.command(Rover(Coordinate(-1, 0), North), "f".toList)
    }
  }
  
  test("Grid 10x10#raises error when source coordinate is invalid 10,0") {
    val api = new TestData().g10
    intercept[IllegalArgumentException] {
      api.command(Rover(Coordinate(10, 0), North), "f".toList)
    }
  }
  
  test("Grid 10x10#raises error when source coordinate is invalid -1,0 and no command") {
    val api = new TestData().g10
    intercept[IllegalArgumentException] {
      api.command(Rover(Coordinate(-1, 0), North), Nil)
    }
  }
  
  test("Grid 10x10#rover is unable to move when goes out of bounds") {
    val api = new TestData().g10
    assert(api.command(Rover(Coordinate(0,0), North), (1 to 20).toList.map(_ => 'f')) === 
      Rover(Coordinate(0,9), North))

  }
  
  test("Rover#has a human readable string version") {
    val rover = new Rover(Coordinate(0,0), North)
    assert("Rover(Coordinate(0,0),N)" === rover.toString)
  }
  
  test("Rover#has equality comparator") {
    val rover1 = new Rover(Coordinate(0,0), North)
    val rover2 = new Rover(Coordinate(0,1), South)
    assert(rover1 != rover2)
    assert(rover1 == rover1)
  }
}