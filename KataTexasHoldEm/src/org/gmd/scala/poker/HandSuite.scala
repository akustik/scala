package org.gmd.scala.poker

import org.scalatest.FunSuite
import scala.collection.immutable.TreeSet

class HandSuite extends FunSuite {

	test("create a hand with less than two cards") {
		intercept[IllegalArgumentException] {
			val cards = Card.parse("2d")
			new Hand(cards)
		}
	}

        test("create a hand with more than seven cards") {
                intercept[IllegalArgumentException] {
                        val cards = Card.parse("2d 3d 4d 5d 6d 7d 8d 9d")
                        new Hand(cards)
                }
        }

        test("create a hand with five cards with repetitions") {
                intercept[IllegalArgumentException] {
                        val cards = Card.parse("2d 3d 4d 5d 5d")
                        new Hand(cards)
                }
        }

	test("create a straight flush rank for a straight flush hand") {
		val h = new Hand(Card.parse("2d 3d 4d 5d 6d 7h"))
		val r = new StraightFlushRanked(h)
		assert(r.applies === true)
		assert(r.usedCards.mkString(" ") === "6d 5d 4d 3d 2d")
		assert(r.nonUsedCards.mkString(" ") === "7h")
	}

        test("create a straight flush rank for a straight flush hand with lower ace") {
                val h = new Hand(Card.parse("2d 3d Ad 4d 5d 7d"))
                val r = new StraightFlushRanked(h)
                assert(r.applies === true)
                assert(r.usedCards.mkString(" ") === "5d 4d 3d 2d Ad")
                assert(r.nonUsedCards.mkString(" ") === "7d")
        }

        test("create a straight flush rank for a straight flush hand with higher ace"){
                val h = new Hand(Card.parse("9d Td Qd Kd Ad"))
                val r = new StraightFlushRanked(h)
                assert(r.applies === true)
                assert(r.usedCards.mkString(" ") === "Ad Kd Qd Td 9d")
                assert(r.nonUsedCards.mkString(" ") === "")
        }

	test("create a straight flush rank for a straight hand") {
                val h = new Hand(Card.parse("2d 3h 4d 5h 6d 7h"))
		val r = new StraightFlushRanked(h)
                assert(r.applies === false)
                assert(r.usedCards.mkString(" ") === "")
                assert(r.nonUsedCards.mkString(" ") === "7h 6d 5h 4d 3h 2d")
        }

	test("create a four of a kind rank for a four of a kind hand") {
		val r = new FourOfAKindRanked(
			new Hand(Card.parse("4d 4h 4s 4c 6d 7h")))

		assert(r.applies === true)
                assert(r.usedCards.mkString(" ") === "4s 4h 4d 4c")
                assert(r.nonUsedCards.mkString(" ") === "7h 6d")
	}

	test("compare two ranked hands with different rank value") {
                val hcr = new HighCardRanked(
			new Hand(Card.parse("2d 3h 4d 5h 6d 7h")))
                val sfr = new StraightFlushRanked(
			new Hand(Card.parse("2d 3d 4d 5d 6d")))
		assert(hcr.applies)
		assert(sfr.applies)
		assert(sfr > hcr)
        }

	test("compare two straight flush hands with same rank value but different higher card") {
                val r1 = new StraightFlushRanked(
                        new Hand(Card.parse("3d 4d 5d 6d 7d")))
                val r2 = new StraightFlushRanked(
                        new Hand(Card.parse("2d 3d 4d 5d 6d")))
                assert(r1.applies)
                assert(r2.applies)
                assert(r1 > r2)
	}

        test("compare two straight flush hands with same rank, same higher card but different kicker") {
                val r1 = new StraightFlushRanked(
                        new Hand(Card.parse("3d 4d 5d 6d 7d 3s")))
                val r2 = new StraightFlushRanked(
                        new Hand(Card.parse("3d 4d 5d 6d 7d 3s As")))
                assert(r1.applies)
                assert(r2.applies)
                assert(r1 < r2)
        }

        test("compare two straight flush hands with same rank, same higher card and same kicker") {
                val r1 = new StraightFlushRanked(
                        new Hand(Card.parse("3d 4d 5d 6d 7d 4s As")))
                val r2 = new StraightFlushRanked(
                        new Hand(Card.parse("3d 4d 5d 6d 7d As 4s")))
                assert(r1.applies)
                assert(r2.applies)
                assert(r1 <= r2 && r2 <= r1)
        }

	test("compare two four of a kind hands with different higher card"){
		val r1 = RankedFactory.build(
			new Hand(Card.parse("4s 4h 4d 4c 5s")))
		val r2 = RankedFactory.build(
			new Hand(Card.parse("5s 5h 5d 5c 9s")))
                assert(r2 > r1)
	}

        test("compare two four of a kind hands with same higher card but different kicker"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("4s 4h 4d 4c 5s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("4s 4h 4d 4c 9s")))
                assert(r2 > r1)
        }

        test("compare two four of a kind hands with same higher card and same kicker"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("4s 4h 4d 4c 5s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("4s 4h 4d 4c 5d")))
                assert(r2 <= r1 && r2 >= r1)
        }

}

