package org.gmd.scala.poker

import scala.collection.immutable.TreeSet
import scala.collection.mutable.HashMap
import scala.util.Sorting

class Hand (h: Array[Card]){
	require(h.length >= 2 && h.length <= 7)
	val cards = (new TreeSet[Card]() ++ h).toArray.reverse
	require(cards.size == h.length)
}

trait Ranked extends Ordered[Ranked]{
	def name: String
	def value: Int
	def applies: Boolean
	def compare(that: Ranked) = if(this.value != that.value) this.value - that.value else compareSameRank(that)
	def compareSameRank(that: Ranked): Int
	def usedCards: Array[Card]
	def nonUsedCards: Array[Card]
	override def toString: String = usedCards.toString + " " + nonUsedCards

	def flushFilter(c: Array[Card]) = {
		val map = c.groupBy[String](x => x.suit)
		val flush: Array[Card] = map.find(x => x._2.length >= 5) match {
			case Some(x) => x._2
			case _ => Array[Card]()
		}
		(flush, c.filterNot(x => flush contains x))
	}

	def amountFilter(c: Array[Card], min: Int) = {
		val map = c.groupBy[Int](x => x.face)
		val amount: Array[Card] = map.find(x => x._2.length >= min) match {
		        case Some(x) => x._2
                        case _ => Array[Card]()
                }
                (amount, c.filterNot(x => amount contains x))
	}

	def straightFilter(c: Array[Card]): (Array[Card], Array[Card]) = {		
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

	def straightFilter(t: TreeSet[Card], f: Card => Int): (Array[Card], Array[Card]) = {		
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

	def kickerDecision(a: Array[Card], b: Array[Card]): Int = {
		if(a(0).face != b(0).face || a.length == 1 || b.length == 1)
			a(0).face - b(0).face
		else
			kickerDecision(a.slice(1, a.length),
					b.slice(1, b.length))
	}
	
}

object RankedFactory {
	def build(h: Hand): Ranked = {
		val rankings = Array[Ranked](
			new StraightFlushRanked(h),
			new HighCardRanked(h),
			new FourOfAKindRanked(h)
		)

		rankings.filter(x => x.applies)(0) 
	}
}

class StraightFlushRanked(h: Hand) extends Ranked {
	val ff = flushFilter(h.cards)
	val sf = straightFilter(ff._1)
	def name = "Straight Flush"
	def value = 9
	def applies: Boolean = {
		sf._1.length >= 5
	}
	def compareSameRank(that: Ranked): Int = {
		kickerDecision(usedCards(0) +: nonUsedCards,
			that.usedCards(0) +: that.nonUsedCards)
	}
	def usedCards: Array[Card] = sf._1
        def nonUsedCards: Array[Card] = sf._2 ++ ff._2
}

class FourOfAKindRanked(h: Hand) extends Ranked {
        val af = amountFilter(h.cards, 4)
        def name = "Four Of A Kind"
        def value = 8
        def applies: Boolean = {
                af._1.length >= 4
        }
        def compareSameRank(that: Ranked): Int = {
                kickerDecision(usedCards(0) +: nonUsedCards,
                        that.usedCards(0) +: that.nonUsedCards)
	}
        def usedCards: Array[Card] = af._1
        def nonUsedCards: Array[Card] = af._2
}

class HighCardRanked(h: Hand) extends Ranked {
	def name = "High Card"
	def value = 1
	def applies: Boolean = true
	def compareSameRank(that: Ranked): Int = {
                kickerDecision(nonUsedCards, that.nonUsedCards)
	}
	def usedCards: Array[Card] = Array()
	def nonUsedCards: Array[Card] = h.cards
}


