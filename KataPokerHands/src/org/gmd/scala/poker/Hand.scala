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
	def value = cards.max.value

	override def toString = cards.mkString(" ")
	
}



