package org.gmd.scala.poker

import org.scalatest.FunSuite
import scala.collection.immutable.TreeSet

class HandSuite extends FunSuite {

	test("create a hand without 5 cards") {
		intercept[IllegalArgumentException] {
			val cards = Array(new Card("2D"))
			new Hand(cards)
		}
	}

	test("create a valid hand a check the toString output") {
		val h = new Hand(Array(
			new Card("2D"),
			new Card("3D"),
			new Card("5D"),
			new Card("2H"),
			new Card("8D")
		))

		assert(h.toString === "8D 5D 3D 2H 2D")
	}

	test("check that the higher rank hand is greater") {
		val a = new Hand(Array(
                        new Card("2D"),
                        new Card("3D"),
                        new Card("5D"),
                        new Card("9H"),
                        new Card("8D")
                ))

		val b = new Hand(Array(
                        new Card("2D"),
                        new Card("AD"),
                        new Card("5D"),
                        new Card("2H"),
                        new Card("8D")
                ))

		assert(b > a)
	}

        test("check that the straight flush with higher rank is greater") {
                val a = new Hand(Array(
                        new Card("2D"),
                        new Card("3D"),
                        new Card("5D"),
                        new Card("4D"),
                        new Card("6D")
                ))

                val b = new Hand(Array(
                        new Card("3D"),
                        new Card("4D"),
                        new Card("5D"),
                        new Card("6D"),
                        new Card("7D")
                ))

		assert(b.isFlush && b.isStraight)
		assert(a.isFlush && a.isStraight)
                assert(b > a)
        }

        test("check that the straight flush is greater than high rank") {
                val a = new Hand(Array(
                        new Card("2D"),
                        new Card("9H"),
                        new Card("5D"),
                        new Card("4D"),
                        new Card("6D")
                ))

                val b = new Hand(Array(
                        new Card("3D"),
                        new Card("4D"),
                        new Card("5D"),
                        new Card("6D"),
                        new Card("7D")
                ))

                assert(b.isFlush && b.isStraight)
                assert(!a.isFlush && !a.isStraight)
                assert(b > a)
        }



	test("check that a hand is straight") {
                val a = new Hand(Array(
                        new Card("2D"),
                        new Card("3D"),
                        new Card("4D"),
                        new Card("5H"),
                        new Card("6D")
                ))

		assert(a.isStraight === true)
	}

	test("check that a hand is notstraight") {
                val a = new Hand(Array(
                        new Card("2D"),
                        new Card("3D"),
                        new Card("4D"),
                        new Card("5H"),
                        new Card("7D")
                ))

                assert(a.isStraight === false)
        }
	
	test("check that a hand is flush") {
                val a = new Hand(Array(
                        new Card("2D"),
                        new Card("3D"),
                        new Card("4D"),
                        new Card("5D"),
                        new Card("6D")
                ))

                assert(a.isFlush === true)
        }

	test("check that a hand is not flush") {
                val a = new Hand(Array(
                        new Card("2D"),
                        new Card("3D"),
                        new Card("4D"),
                        new Card("5H"),
                        new Card("6D")
                ))

                assert(a.isFlush === false)
        }

        test("check that a hand has four of a kind") {
                val a = new Hand(Array(
                        new Card("2D"),
                        new Card("2H"),
                        new Card("2S"),
                        new Card("2C"),
                        new Card("6D")
                ))

                assert(a.fourOfAKind === 2)
        }

	test("check that a hand has three of a kind") {
                val a = new Hand(Array(
                        new Card("2D"),
                        new Card("2H"),
                        new Card("2S"),
                        new Card("3C"),
                        new Card("6D")
                ))

                assert(a.threeOfAKind === 2)
        }

        test("check that a hand has two of a kind (twice)") {
                val a = new Hand(Array(
                        new Card("2D"),
                        new Card("2H"),
                        new Card("3S"),
                        new Card("3C"),
                        new Card("6D")
                ))

                assert(a.twoOfAKind(2) === 3)
		assert(a.twoOfAKind(3) === 2)
        }

        test("check that a four of a kind with highest rank wins another four of a kind") {
                val a = new Hand(Array(
                        new Card("2D"),
                        new Card("2H"),
                        new Card("2S"),
                        new Card("2C"),
                        new Card("6D")
                ))

                val b = new Hand(Array(
                        new Card("3D"),
                        new Card("3H"),
                        new Card("3S"),
                        new Card("3C"),
                        new Card("7D")
                ))


                assert(a < b)                
        }

        test("check that a full house with highest rank wins another full house"){
                val a = new Hand(Array(
                        new Card("2D"),
                        new Card("2H"),
                        new Card("2S"),
                        new Card("6C"),
                        new Card("6D")
                ))

                val b = new Hand(Array(
                        new Card("3D"),
                        new Card("3H"),
                        new Card("3S"),
                        new Card("4C"),
                        new Card("4D")
                ))


                assert(a < b)
        }

	test("check the order of serveral hands") {
		val hands = TreeSet(
			new Hand(Array(
				new Card("3D"),
				new Card("4D"),
				new Card("5D"),
				new Card("6D"),
				new Card("7D")
			)),
                        new Hand(Array(
                                new Card("3D"),
                                new Card("3C"),
                                new Card("3H"),
                                new Card("8D"),
                                new Card("8H")
                        )),
                        new Hand(Array(
                                new Card("5D"),
                                new Card("5H"),
                                new Card("5S"),
                                new Card("5C"),
                                new Card("7D")
                        )),
                        new Hand(Array(
                                new Card("5D"),
                                new Card("6D"),
                                new Card("7D"),
                                new Card("8D"),
                                new Card("9D")
                        )),
                        new Hand(Array(
                                new Card("2D"),
                                new Card("6D"),
                                new Card("7D"),
                                new Card("8D"),
                                new Card("9D")
                        )),
                        new Hand(Array(
                                new Card("2D"),
                                new Card("6D"),
                                new Card("7H"),
                                new Card("8D"),
                                new Card("9D")
                        )),
                        new Hand(Array(
                                new Card("2D"),
                                new Card("3D"),
                                new Card("4H"),
                                new Card("5D"),
                                new Card("6D")
                        )),
                        new Hand(Array(
                                new Card("2D"),
                                new Card("3D"),
                                new Card("4H"),
                                new Card("4D"),
                                new Card("4S")
                        )),
                        new Hand(Array(
                                new Card("2D"),
                                new Card("2S"),
                                new Card("4H"),
                                new Card("4D"),
                                new Card("8S")
                        )),
                        new Hand(Array(
                                new Card("3D"),
                                new Card("3S"),
                                new Card("4H"),
                                new Card("4D"),
                                new Card("8S")
                        )),
                        new Hand(Array(
                                new Card("3D"),
                                new Card("3S"),
                                new Card("4H"),
                                new Card("4D"),
                                new Card("5S")
                        )),
                        new Hand(Array(
                                new Card("3D"),
                                new Card("3S"),
                                new Card("4H"),
                                new Card("4D"),
                                new Card("7S")
                        )),
                        new Hand(Array(
                                new Card("3D"),
                                new Card("3S"),
                                new Card("4H"),
                                new Card("8D"),
                                new Card("7S")
                        )),
                        new Hand(Array(
                                new Card("AD"),
                                new Card("AS"),
                                new Card("4H"),
                                new Card("8D"),
                                new Card("7S")
                        ))

		)

//		println("actual: " + hands.toString)				
		assert(hands.toString === "TreeSet("
			+ "9D 8D 7H 6D 2D, "
			+ "8D 7S 4H 3S 3D, "
			+ "AS AD 8D 7S 4H, "
			+ "8S 4H 4D 2S 2D, "
			+ "5S 4H 4D 3S 3D, "
			+ "7S 4H 4D 3S 3D, " 
			+ "8S 4H 4D 3S 3D, "
			+ "4S 4H 4D 3D 2D, " 
			+ "6D 5D 4H 3D 2D, " 
			+ "9D 8D 7D 6D 2D, "
			+ "8H 8D 3H 3D 3C, "
			+ "7D 5S 5H 5D 5C, "
			+ "7D 6D 5D 4D 3D, "
			+ "9D 8D 7D 6D 5D)")
	}


}

