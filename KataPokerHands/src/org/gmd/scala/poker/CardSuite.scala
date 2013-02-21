package org.gmd.scala.poker

import org.scalatest.FunSuite

class CardSuite extends FunSuite {

	test("create a card with length more than 2") {
		intercept[IllegalArgumentException] {
			new Card("invalid")
		}
	}

	test("create a card whose value is not valid") {
		intercept[IllegalArgumentException] {
			new Card("1D")
		}
	}

	test("create a card whose suite is not valid") {
		intercept[IllegalArgumentException] {
			new Card("2Z")		
		}		
	}

	test("create a valid card") {
		val twoOfDiamonds = new Card("2D")
		assert(twoOfDiamonds.value === 2)
		assert(twoOfDiamonds.suit === "diamonds")
	}
}
