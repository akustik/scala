package org.gmd.scala.poker

import scala.collection.immutable.TreeSet
import scala.collection.mutable.HashMap
import scala.util.Sorting

private[poker] class Hand (h: Array[Card]){
	require(h.length >= 2 && h.length <= 7)
	val cards = (new TreeSet[Card]() ++ h).toArray.reverse
	require(cards.size == h.length)
}

trait Ranked extends Ordered[Ranked]{
	protected[poker] def hand: Hand
	def name: String
	protected[poker] def value: Int
	protected[poker] def applies: Boolean
	def compare(that: Ranked) = if(this.value != that.value) this.value - that.value else compareSameRank(that)
	protected[poker] def compareSameRank(that: Ranked): Int
	protected[poker] def usedCards: Array[Card]
	protected[poker] def nonUsedCards: Array[Card]
	override def toString: String = "%s(%d): %s => %s | %s".format(name, value, applies, usedCards.mkString(" "), nonUsedCards.mkString(" "))

	protected final def flushFilter(c: Array[Card]) = {
		val map = c.groupBy[String](x => x.suit)
		val flush: Array[Card] = map.find(x => x._2.length >= 5) match {
			case Some(x) => x._2
			case _ => Array[Card]()
		}
		(flush, c.filterNot(x => flush contains x))
	}

	protected final def amountFilter(c: Array[Card], min: Int) = {
		val map = c.groupBy[Int](x => x.face)
		val amount: Array[Card] = map.find(x => x._2.length >= min) match {
		        case Some(x) => x._2
                        case _ => Array[Card]()
                }
                (amount, c.filterNot(x => amount contains x))
	}

	protected final def straightFilter(c: Array[Card]): (Array[Card], Array[Card]) = {		
		val al2c = straightFilter(
			new TreeSet[Card]()(AceAsLowerOneCardValueOrdering) ++ c,
			(x: Card) => x.lValue
		)	
		val ah2c = straightFilter(
			new TreeSet[Card]()(AceAsThirteenCardValueOrdering) ++ c,
			(x: Card) => x.face
		)
		if(ah2c._1.length >= al2c._1.length) ah2c else al2c
	}

	protected final def straightFilter(t: TreeSet[Card], f: Card => Int): (Array[Card], Array[Card]) = {		
		val cards = t.toArray.reverse
		val sc: Array[Card] = cards.filter(x => {
			val idx = cards.indexOf(x)
			val max = cards.length - 1
			if(idx != max) {
				f(x) - 1 == f(cards(idx + 1))
			} else {
				true
			}
		})

		(sc, cards.filterNot(x => sc contains x))		
	}

	protected final def kickerDecision(a: Array[Card], b: Array[Card]): Int = {
		if(a(0).face != b(0).face || a.length == 1 || b.length == 1)
			a(0).face - b(0).face
		else
			kickerDecision(a.slice(1, a.length),
					b.slice(1, b.length))
	}
	
}

object RankedFactory {

	def rank(s: String): String = {
		val players = s.split("\n")
		val ranks = players.map(p => build(new Hand(Card.parse(p))))
		val winner = ranks.max[Ranked]
		ranks.map(r => {
			val uc =  r.usedCards.mkString(" ")
			val nuc = r.nonUsedCards.mkString(" ")
			val name = r.name
			val applies = r.hand.cards.length == 7
			
			if(applies && r == winner)
				"%s %s %s (winner)".format(uc, nuc, name)
			else if(applies)
				"%s %s %s".format(uc, nuc, name)
			else if(uc.length > 0 && nuc.length > 0)
				"%s %s".format(uc, nuc)
			else 
				"%s".format(nuc)
		}).mkString("\n")
	}

	def build(s: String): Ranked = {
		build(new Hand(Card.parse(s)))
	}

	def build(h: Hand): Ranked = {
		val rankings = Array[Ranked](
			new StraightFlushRanked(h),
			new FourOfAKindRanked(h),
			new FullHouseRanked(h),
			new FlushRanked(h),
			new StraightRanked(h),
			new ThreeOfAKindRanked(h),
			new TwoPairsRanked(h),
			new PairRanked(h),
			new HighCardRanked(h)
		)

		rankings.filter(x => {
			//println(x)
			x.applies			
		})(0) 
	}
}

private[poker] class StraightFlushRanked(h: Hand) extends Ranked {
	val ff = flushFilter(h.cards)
	val sf = straightFilter(ff._1)
	val uc = if(sf._1.length > 5) sf._1.slice(0, 5) else sf._1
	val nuc = if(sf._1.length > 5) sf._1.slice(5, sf._1.length) ++ sf._2 ++ ff._2 else sf._2 ++ ff._2
	Sorting.quickSort(nuc)
	val rnuc = nuc.reverse
	def name = "Straight Flush"
	def value = 9
	def applies: Boolean = {
		uc.length == 5
	}
	def compareSameRank(that: Ranked): Int = {
		kickerDecision(Array(usedCards(0)), 
				Array(that.usedCards(0)))
	}
	def usedCards: Array[Card] = uc
        def nonUsedCards: Array[Card] = rnuc
	def hand: Hand = h
}

private[poker] class FourOfAKindRanked(h: Hand) extends Ranked {
        private val (uc, nuc) = amountFilter(h.cards, 4)
        def name = "Four Of A Kind"
        def value = 8
        def applies: Boolean = {
                uc.length == 4
        }
        def compareSameRank(that: Ranked): Int = {
                kickerDecision(Array(usedCards(0), nonUsedCards(0)),
                        Array(that.usedCards(0), that.nonUsedCards(0)))
	}
        def usedCards: Array[Card] = uc
        def nonUsedCards: Array[Card] = nuc
        def hand: Hand = h
}

private[poker] class FullHouseRanked(h: Hand) extends Ranked {
	private val (uc3, nuc3) = amountFilter(h.cards, 3)
	private val (uc2, nuc2) = amountFilter(nuc3, 2)
	private val (uc, nuc) = (uc3 ++ uc2, nuc2)
	def name = "Full House"
	def value = 7
	def applies: Boolean = {
		uc3.length == 3 && uc2.length == 2
	}
	def compareSameRank(that: Ranked): Int = {
		kickerDecision(usedCards, that.usedCards)
        }
        def usedCards: Array[Card] = uc
        def nonUsedCards: Array[Card] = nuc
        def hand: Hand = h
}

private[poker] class FlushRanked(h: Hand) extends Ranked {
	private val (uc, nuc) = flushFilter(h.cards) match {
		case (u, r) if(u.length > 5) => 
			(u.slice(0, 5), u.slice(5, u.length) ++ r)
		case (u, r) => (u, r)
	}
        def name = "Flush"
        def value = 6
        def applies: Boolean = {
		uc.length == 5
        }
        def compareSameRank(that: Ranked): Int = {
                kickerDecision(usedCards, that.usedCards)
        }
        def usedCards: Array[Card] = uc
        def nonUsedCards: Array[Card] = nuc
        def hand: Hand = h
}

private[poker] class StraightRanked(h: Hand) extends Ranked {
        val sf = straightFilter(h.cards)
        val uc = if(sf._1.length > 5) sf._1.slice(0, 5) else sf._1
        val nuc = if(sf._1.length > 5) sf._1.slice(5, sf._1.length) ++ sf._2 else sf._2
        def name = "Straight"
        def value = 5
        def applies: Boolean = {
                uc.length == 5
        }
        def compareSameRank(that: Ranked): Int = {
                kickerDecision(Array(usedCards(0)), Array(that.usedCards(0)))
        }
        def usedCards: Array[Card] = uc
        def nonUsedCards: Array[Card] = nuc
        def hand: Hand = h
}

private[poker] class ThreeOfAKindRanked(h: Hand) extends Ranked {
        val af = amountFilter(h.cards, 3)
        def name = "Three Of A Kind"
        def value = 4
        def applies: Boolean = {
                af._1.length == 3
        }
        def compareSameRank(that: Ranked): Int = {
                kickerDecision(Array(usedCards(0), nonUsedCards(0), nonUsedCards(1)),
                        Array(that.usedCards(0), that.nonUsedCards(0), that.nonUsedCards(1)))
        }
        def usedCards: Array[Card] = af._1
        def nonUsedCards: Array[Card] = af._2
        def hand: Hand = h
}

private[poker] class TwoPairsRanked(h: Hand) extends Ranked {
        val af1 = amountFilter(h.cards, 2)
	val af2 = amountFilter(af1._2, 2)
	val uc = af1._1 ++ af2._1
	val nuc = af2._2
        def name = "Two pairs"
        def value = 3
        def applies: Boolean = {
                uc.length == 4
        }
        def compareSameRank(that: Ranked): Int = {
                kickerDecision(usedCards :+ nonUsedCards(0), that.usedCards :+ that.nonUsedCards(0))
        }
        def usedCards: Array[Card] = uc
        def nonUsedCards: Array[Card] = nuc
        def hand: Hand = h
}

private[poker] class PairRanked(h: Hand) extends Ranked {
        val af = amountFilter(h.cards, 2)
        val uc = af._1
        val nuc = af._2
        def name = "Pair"
        def value = 2
        def applies: Boolean = {
                uc.length == 2
        }
        def compareSameRank(that: Ranked): Int = {
                kickerDecision(usedCards(0) +: nonUsedCards.slice(0,3), 
			that.usedCards(0) +: that.nonUsedCards.slice(0,3))
        }
        def usedCards: Array[Card] = uc
        def nonUsedCards: Array[Card] = nuc
        def hand: Hand = h
}


private[poker] class HighCardRanked(h: Hand) extends Ranked {
	def name = "High Card"
	def value = 1
	def applies: Boolean = true
	def compareSameRank(that: Ranked): Int = {
                kickerDecision(nonUsedCards, that.nonUsedCards)
	}
	def usedCards: Array[Card] = Array()
	def nonUsedCards: Array[Card] = h.cards
        def hand: Hand = h
}


