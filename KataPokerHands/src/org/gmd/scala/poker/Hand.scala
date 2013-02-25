package org.gmd.scala.poker

import scala.collection.immutable.TreeSet
import scala.collection.mutable.HashMap

class Hand (h: Array[Card]) extends Ordered[Hand]{
	require(h.length == 5)
	val cards = TreeSet(h(0), h(1), h(2), h(3), h(4)).toArray.reverse
	require(cards.size == 5)

	val values = new HashMap[Int, Int]()
	val colors = new HashMap[String, Int]()
	
	cards.foreach(x => {
		updateMap(values, x.value)
		updateMap(colors, x.suit)
	})

	private def updateMap(map: HashMap[Int, Int], key: Int) = {
                map.put(key,
                        if(map.contains(key))
                                map.get(key).get + 1
                        else
                                1
                )
        }

        private def updateMap(map: HashMap[String, Int], key: String) = {
                map.put(key,
                        if(map.contains(key))
                                map.get(key).get + 1
                        else
                                1
                )
        }


	def compare(that: Hand) = {
		StraightFlashOrdering.compare(this, that)		
	}

	def isStraight = values.size == 5 && (for(i <- 0 until 4) yield (cards(i).value - 1 == cards(i+1).value)).foldLeft(true)(_ && _)
	
	def isFlush = colors.size == 1

	def fourOfAKind = anyOfAKind(4, -1)

	private def anyOfAKind(amount: Int, ignore: Int): Int = values.find(x => x._1 != ignore && x._2 == amount).getOrElse((-1, 0))._1

	def threeOfAKind = anyOfAKind(3, -1)

	def twoOfAKind = anyOfAKind(2, -1)

	def oneOfAKind = anyOfAKind(1, -1)

	def twoOfAKind(ignore: Int) = anyOfAKind(2, ignore)

	override def toString = cards.mkString(" ")

	trait FilterOrDelegateOrdering {

		def filterOrDelegate(a: Hand, b: Hand, f: (Hand) => Int, d: Ordering[Hand]): Int = {
			val game = Array(a, b)
			val appliesFn = (h: Hand) => f(h) != -1
                        val filteredHands = game.filter(appliesFn)
                        if(filteredHands.length != 0){
                                f(a) - f(b)
			} else {
                                d.compare(a, b)
                        }
		}

	}

	object HigherRankOrdering extends Ordering[Hand] {
		def compare(a: Hand, b: Hand) = {
			higherCard(a.cards, b.cards)
		}

		private def higherCard(a: Array[Card], b: Array[Card]): Int = {
			if(a.length == 1 || a(0).value != b(0).value)
				a(0).value - b(0).value
			else
				higherCard(a.slice(1, a.length), b.slice(1, b.length))
		}
	}

	object SinglePairOrdering extends Ordering[Hand] with FilterOrDelegateOrdering {
		def compare (a: Hand, b: Hand) = filterOrDelegate(a, b, (h: Hand) => h.twoOfAKind(-1), HigherRankOrdering)
	}

	object TwoPairsOrdering extends Ordering[Hand] {
		def compare(a: Hand, b: Hand) = {
			var game = Array(a, b)
			val filteredHands = game.filter((h: Hand) => h.twoOfAKind(-1) != -1 && h.twoOfAKind(h.twoOfAKind) != -1)
			if(filteredHands.length == 2){
				val firstPairDiff = a.twoOfAKind(-1) - b.twoOfAKind(-1)
				val secondPairDiff = a.twoOfAKind(a.twoOfAKind(-1)) - b.twoOfAKind(b.twoOfAKind(-1))
				if(firstPairDiff != 0) {
					firstPairDiff
				} else if(secondPairDiff != 0){
					secondPairDiff
				} else {
					HigherRankOrdering.compare(a, b)
				}
			} else if (filteredHands.length == 1){
				if(filteredHands(0) eq a) 1 else -1
			} else {
				SinglePairOrdering.compare(a, b)
			}
		}

	}

	object ThreeOfAKindOrdering extends Ordering[Hand] with FilterOrDelegateOrdering {
                def compare (a: Hand, b: Hand) = filterOrDelegate(a, b, (h: Hand) => h.threeOfAKind, TwoPairsOrdering)
        }

	object StraightOrdering extends Ordering[Hand] with FilterOrDelegateOrdering {
		def compare (a: Hand, b: Hand) = filterOrDelegate(a, b, (h: Hand) => if(h.isStraight) h.cards(0).value else -1, ThreeOfAKindOrdering)
	}	

	object FlushOrdering extends Ordering[Hand] with FilterOrDelegateOrdering {
		def compare (a: Hand, b: Hand) = filterOrDelegate(a, b, (h: Hand) => if(h.isFlush) 0 else -1, StraightOrdering)
	}

	object FullHouseOrdering extends Ordering[Hand] with FilterOrDelegateOrdering {
		def compare (a: Hand, b: Hand) = filterOrDelegate(a, b, (h: Hand) => if(h.threeOfAKind != -1 && h.twoOfAKind(h.threeOfAKind) != -1) h.threeOfAKind else -1, FlushOrdering) 
	}

	object StraightFlashOrdering extends Ordering[Hand] with FilterOrDelegateOrdering{				        	
		def compare (a: Hand, b: Hand) = filterOrDelegate(a, b, (h: Hand) => if(h.isStraight && h.isFlush) h.cards(0).value else -1, FourOfAKindOrdering)
	}

	object FourOfAKindOrdering extends Ordering[Hand] with FilterOrDelegateOrdering{
		def compare(a: Hand, b: Hand) = filterOrDelegate(a, b, (h: Hand) => h.fourOfAKind, FullHouseOrdering)
	}
	
}



