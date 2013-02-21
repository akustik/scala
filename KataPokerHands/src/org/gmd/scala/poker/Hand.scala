package org.gmd.scala.poker

class Hand (h: Array[Card]) extends Ordered[Hand]{
	require(h.length == 5)

	def compare(that: Hand) = this.value - that.value

	def value = 0
	
}


