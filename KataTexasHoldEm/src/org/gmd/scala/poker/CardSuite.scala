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
			new Card("1d")
		}
	}

	test("create a card whose suite is not valid") {
		intercept[IllegalArgumentException] {
			new Card("2z")		
		}		
	}

	test("create a valid card") {
		val twoOfDiamonds = new Card("2d")
		assert(twoOfDiamonds.face === 2)
		assert(twoOfDiamonds.suit === "diamonds")
	}

	test("create a valid card with uppercase") {
		 intercept[IllegalArgumentException] {
                        new Card("2D")
                }
	}

	test("compare two cards with their natural order") {
		assert(new Card("2d") < new Card("3d") === true)
	}

	test("order an array of cards by value") {
		val cards = Array(new Card("3d"), new Card("4d"), new Card("2d"))
		Sorting.quickSort(cards)(AceAsThirteenCardValueOrdering)
		
		assert(cards(0) === new Card("2d"))
		assert(cards(2) === new Card("4d"))
	}

	test("order an array of cards by color") {
		val cards = Array(new Card("3h"), new Card("4d"))
	        Sorting.quickSort(cards)(CardColorOrdering)

		assert(cards(0) === new Card("4d"))
		assert(cards(1) === new Card("3h"))
	}

	test("order an array of cards by value with ace as lower card"){
		val cards = Card.parse("3h Ah 5h")
		Sorting.quickSort(cards)(AceAsLowerOneCardValueOrdering)
		assert(cards(0) === new Card("Ah"))
	}
}
