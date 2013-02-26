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

        test("create a straight flush rank for a straight flush hand with more than five cards") {
                val h = new Hand(Card.parse("2d 3d 4d 5d 6d 7d 7h"))
                val r = new StraightFlushRanked(h)
                assert(r.applies === true)
                assert(r.usedCards.mkString(" ") === "7d 6d 5d 4d 3d")
                assert(r.nonUsedCards.mkString(" ") === "7h 2d")
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
                assert(r1 >= r2 && r2 >= r1)
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
		assert(r1.name === "Four Of A Kind")
		assert(r2.name === "Four Of A Kind")
                assert(r2 > r1)
	}

        test("compare two four of a kind hands with same higher card but different kicker"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("4s 4h 4d 4c 5s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("4s 4h 4d 4c 9s")))
                assert(r1.name === "Four Of A Kind")
                assert(r2.name === "Four Of A Kind")
                assert(r2 > r1)
        }

        test("compare two four of a kind hands with same higher card and same kicker"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("4s 4h 4d 4c 5s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("4s 4h 4d 4c 5d")))
                assert(r1.name === "Four Of A Kind")
                assert(r2.name === "Four Of A Kind")
                assert(r2 <= r1 && r2 >= r1)
        }

        test("compare two full house hands with different higher three card"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("4s 4h 4d 5c 5s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("5s 5h 5d 9c 9s")))
                assert(r1.name === "Full House")
                assert(r2.name === "Full House")
                assert(r2 > r1)
        }

        test("compare two full house hands with different higher pair card"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("5s 5h 5d 4c 4s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("5s 5h 5d 9c 9s")))
                assert(r1.name === "Full House")
                assert(r2.name === "Full House")
                assert(r2 > r1)
        }

        test("compare two full house hands with same higher cards"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("5s 5h 5d 9c 9s As")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("5s 5h 5d 9c 9s 7s")))
                assert(r1.name === "Full House")
                assert(r2.name === "Full House")
                assert(r1 >= r2 && r2 >= r1)
        }

        test("compare two flush hands with higher card"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("5s 6s 8s Ts Ks As")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("4s 5s 6s 8s Ts Ks")))
                assert(r1.name === "Flush")
                assert(r2.name === "Flush")
                assert(r1 > r2)
        }

        test("compare two flush hands with same higher cards"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("2s 5s 6s 8s Ts Ks")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("4s 5s 6s 8s Ts Ks")))
                assert(r1.name === "Flush")
                assert(r2.name === "Flush")
                assert(r1 >= r2 && r1 >= r2)
        }

        test("compare two straight hands with different higher cards"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("7s 8c 9s Tc Qc Ks")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("8s 9c Ts Qc Ks Ac")))
                assert(r1.name === "Straight")
                assert(r2.name === "Straight")
                assert(r2 > r1)
        }

        test("compare two straight hands with same higher cards"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("8c 9s Tc Qc Ks Ac")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("8s 9c Ts Qc Ks Ac")))
                assert(r1.name === "Straight")
                assert(r2.name === "Straight")
                assert(r1 >= r2 && r2 >= r1)
        }

        test("compare two three of a kind hands with different higher cards"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad Ah 3s 4s 2s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("Kc Kd Kh 3s 4s 2s")))
                assert(r1.name === "Three Of A Kind")
                assert(r2.name === "Three Of A Kind")
                assert(r1 > r2)
        }

        test("compare two three of a kind hands with different kicker cards"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad Ah 3s 6s 2s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad Ah 3s 4s 2s")))
                assert(r1.name === "Three Of A Kind")
                assert(r2.name === "Three Of A Kind")
                assert(r1 > r2)
        }

        test("compare two three of a kind hands with same kicker cards"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad Ah 3s 4s 2s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad Ah 3s 4s 2s")))
                assert(r1.name === "Three Of A Kind")
                assert(r2.name === "Three Of A Kind")
                assert(r1 >= r2 && r2 >= r1)
        }

        test("compare two two pairs hands with different first pair face"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad 3h 3s 4s 2s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("Kc Kd 3h 3s 4s 2s")))
                assert(r1.name === "Two pairs")
                assert(r2.name === "Two pairs")
                assert(r1 > r2)
        }

        test("compare two two pairs hands with different second pair face"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad 3h 3s 4s 2s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad 2h 2s 4s 8s")))
                assert(r1.name === "Two pairs")
                assert(r2.name === "Two pairs")
                assert(r1 > r2)
        }

        test("compare two two pairs hands with different kicker card"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad 3h 3s 4s 2s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad 3h 3s 4s 8s")))
                assert(r1.name === "Two pairs")
                assert(r2.name === "Two pairs")
                assert(r2 > r1)
        }

        test("compare two two pairs hands with tie"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad 3h 3s 5s 8s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad 3h 3s 4s 8s")))
                assert(r1.name === "Two pairs")
                assert(r2.name === "Two pairs")
                assert(r2 >= r1 && r1 >= r2)
        }

        test("compare two pair hands with different face"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad 3h 8s 4s 2s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("Kc Kd 3h 9s 4s 2s")))
                assert(r1.name === "Pair")
                assert(r2.name === "Pair")
                assert(r1 > r2)
        }

        test("compare two pair hands with tie"){
                val r1 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad 8h 9s 4s 3s")))
                val r2 = RankedFactory.build(
                        new Hand(Card.parse("Ac Ad 8h 9s 4s 2s")))
                assert(r1.name === "Pair")
                assert(r2.name === "Pair")
                assert(r1 >= r2 && r2 >= r1)
        }

	test("rank a players game"){
		val game = "Kc 9s Ks Kd 9d 3c 6d\n" +
			"9c Ah Ks Kd 9d 3c 6d\n" +
			"Ac Qc Ks Kd 9d 3c\n" +
			"9h 5s\n" +
			"4d 2d Ks Kd 9d 3c 6d\n" +
			"7s Ts Ks Kd 9d"

		val expected = "Ks Kd Kc 9s 9d 6d 3c Full House (winner)\n" +
			"9d 9c Ks Kd Ah 6d 3c Two pairs\n" +
			"Ks Kd Ac Qc 9d 3c\n" +
			"9h 5s\n" +
			"Kd 9d 6d 4d 2d Ks 3c Flush\n" +
			"Ks Kd Ts 9d 7s"
			
		assert(expected === RankedFactory.rank(game))
	}
}

