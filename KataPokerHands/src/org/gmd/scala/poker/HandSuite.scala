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

		assert(h.toString === "2H 3D 5D 8D")
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

	
}
