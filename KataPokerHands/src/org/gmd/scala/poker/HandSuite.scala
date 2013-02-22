package org.gmd.scala.poker

import org.scalatest.FunSuite

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
                        new Card("2H"),
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


}
