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
		0
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

	object StraightFlashOrdering extends Ordering[Hand] {				        	
		def compare(a: Hand, b: Hand) = {
	                val game = Array(a, b)
        	        val straightFlushHands = game.filter(hand => hand.isStraight && hand.isFlush)
                	if(straightFlushHands.length == 2){
	                        straightFlushHands(0).cards(0).value -  straightFlushHands(1).cards(0).value
        	        } else if(straightFlushHands.length == 1) {
                	        if(straightFlushHands(0) eq a) 1 else - 1
	                } else {
        	                HigherRankOrdering.compare(a, b)
                	}

		}		
	}
	
}



