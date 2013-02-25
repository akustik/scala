package org.gmd.scala.poker

class Card (c: String) extends Ordered[Card]{
	val canonical = c.toUpperCase
	require(canonical.length == 2)	
	require(value != -1)
	require(suit != null)

	def compare(that: Card) = {
		if(this.value == that.value)
			this.suit compare that.suit
		else
			this.value - that.value
	}
	
	def suit = canonical(1) match {
		case 'C' => "clubs"
		case 'D' => "diamonds"
		case 'H' => "hearts"
		case 'S' => "spades"
		case _ => null
	}

	def value = canonical(0) match {
		case '2' => 2
		case '3' => 3
		case '4' => 4
		case '5' => 5
		case '6' => 6
		case '7' => 7
		case '8' => 8
		case '9' => 9
		case 'T' => 10
		case 'Q' => 11
		case 'K' => 12
		case 'A' => 13
		case _ => -1
	}

	override def equals(b: Any) = b match {
		case card: Card => card.suit == this.suit && card.value == this.value
		case _ => false
	}

	override def toString = c
}

object CardValueOrdering extends Ordering[Card] {
	def compare(a: Card, b: Card) = a compare b
}

object CardColorOrdering extends Ordering[Card] {
	def compare(a: Card, b: Card) = a.suit compare b.suit
}
