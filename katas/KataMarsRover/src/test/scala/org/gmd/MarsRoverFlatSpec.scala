package org.gmd

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MarsRoverSuite extends FunSuite {

  class TestData {
    lazy val g10: MarsRoverAPI = new MarsRoverGrid(10)
  }

  test("Grid 10x10#moves to (0,1)N from (0,0)N when f") {
    val api = new TestData().g10
    api.command(Rover(Coordinate(0, 0), North), "f".toList) ===
      Rover(Coordinate(0, 1), North)
  }

  test("Grid 10x10#moves to (1,1)E from (0,0)N when frf") {
    val api = new TestData().g10
    api.command(Rover(Coordinate(0, 0), North), "frf".toList) ===
      Rover(Coordinate(1, 1), East)
  }

  test("Grid 10x10#moves to (2,0)N from (0,0)N when frfflb") {
    val api = new TestData().g10
    api.command(Rover(Coordinate(0, 0), North), "frfflb".toList) ===
      Rover(Coordinate(2, 0), South)
  }

  test("Grid 10x10#raises error when source coordinate is invalid (-1, 0)") {
    val api = new TestData().g10
    intercept[IllegalArgumentException] {
      api.command(Rover(Coordinate(-1, 0), North), "f".toList)
    }
  }
  
  test("Grid 10x10#raises error when source coordinate is invalid (10, 0)") {
    val api = new TestData().g10
    intercept[IllegalArgumentException] {
      api.command(Rover(Coordinate(10, 0), North), "f".toList)
    }
  }
  
  test("Grid 10x10#raises error when source coordinate is invalid (-1, 0) and no command") {
    val api = new TestData().g10
    intercept[IllegalArgumentException] {
      api.command(Rover(Coordinate(-1, 0), North), Nil)
    }
  }
  
  test("Rover#has a human readable string version") {
    val rover = new Rover(Coordinate(0,0), North)
    "Rover(Coordinate(0,0),N)" === rover.toString
  }
}