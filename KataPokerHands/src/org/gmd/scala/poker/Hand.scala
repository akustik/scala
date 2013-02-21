package org.gmd.scala.poker

import scala.collection.immutable.TreeSet

class Hand (h: Array[Card]) extends Ordered[Hand]{
	require(h.length == 5)
	val cards = TreeSet(h(0), h(1), h(2), h(3), h(4))
	require(cards.size == 5)

	def compare(that: Hand) = this.value - that.value

	/**
	 * Calculate the value of the whole hand
         * 1. Higher card
	 */
	def value = {
		cards.max.value
	}

	def isStraight: Boolean = isStraight(cards.toArray)

	private def isStraight(c: Array[Card]): Boolean = {
		if(c.length == 1)
			true
		else 
			(c(0).value + 1 == c(1).value) && isStraight(c.slice(1, c.length))
	}

	def isFlush: Boolean = isFlush(cards.toArray)

	private def isFlush(c: Array[Card]): Boolean = {
		if(c.length == 1)
			true
		else
			(c(0).suit == c(1).suit) && isFlush(c.slice(1, c.length))
	}

	def fourOfAKind: Int = anyOfAKind(cards.toArray, 4, 0, -1, -1)

	def threeOfAKind: Int = anyOfAKind(cards.toArray, 3, 0, -1, -1)

	def twoOfAKind(ignore: Int): Int = anyOfAKind(cards.toArray, 2, 0, -1, ignore) 

	private def anyOfAKind(c: Array[Card], target: Int, amount: Int, last: Int, ignore: Int): Int = {
		if(c.length == 0 || target <= amount){
			if(target <= amount)
				last
			else
				-1
		} else {
			if(last == c(0).value && c(0).value != ignore){
				anyOfAKind(c.slice(1, c.length), target, amount + 1, c(0).value, ignore)
			}
			else {
				anyOfAKind(c.slice(1, c.length), target, 1, c(0).value, ignore)
			}
		}	
		
			
	}

	override def toString = cards.mkString(" ")
	
}



