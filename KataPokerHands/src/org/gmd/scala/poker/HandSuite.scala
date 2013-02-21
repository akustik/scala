package org.gmd.scala.poker

import org.scalatest.FunSuite

class HandSuite extends FunSuite {

	test("create a hand without 5 cards") {
		intercept[IllegalArgumentException] {
			val cards = Array(new Card("2D"))
			new Hand(cards)
		}
	}
}
