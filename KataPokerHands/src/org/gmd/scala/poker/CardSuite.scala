package org.gmd.scala.poker

import org.scalatest.FunSuite
import scala.util.Sorting

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

	test("compare two cards with their natural order") {
		assert(new Card("2D") < new Card("3D") === true)
	}

	test("order an array of cards by value") {
		val cards = Array(new Card("3D"), new Card("4D"), new Card("2D"))
		Sorting.quickSort(cards)(CardValueOrdering)
		
		assert(cards(0) === new Card("2D"))
		assert(cards(2) === new Card("4D"))
	}

	test("order an array of cards by color") {
		val cards = Array(new Card("3H"), new Card("4D"))
	        Sorting.quickSort(cards)(CardColorOrdering)

		assert(cards(0) === new Card("4D"))
		assert(cards(1) === new Card("3H"))

	}
}
