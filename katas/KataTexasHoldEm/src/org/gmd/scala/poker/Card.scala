package org.gmd.scala.poker

private[poker] object Card {
	def parse(s: String): Array[Card] = {
		val pieces = s.split(' ')
		pieces.map(x => new Card(x))
	}
}

private[poker] class Card (c: String) extends Ordered[Card]{
	require(c.length == 2)	
	require(face != -1)
	require(suit != null)

	def compare(that: Card) = {
		if(this.face == that.face)
			this.suit compare that.suit
		else
			this.face - that.face
	}
	
	def suit = c(1) match {
		case 'c' => "clubs"
		case 'd' => "diamonds"
		case 'h' => "hearts"
		case 's' => "spades"
		case _ => null
	}

	def face = c(0) match {
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

	def lValue = (face - 1) % 12

	override def equals(b: Any) = b match {
		case card: Card => card.suit == this.suit && card.face == this.face
		case _ => false
	}

	override def toString = c
}

private[poker] object AceAsLowerOneCardValueOrdering extends Ordering[Card] {
        def compare(a: Card, b: Card) = a.lValue compare b.lValue
}

private[poker] object AceAsThirteenCardValueOrdering extends Ordering[Card] {
        def compare(a: Card, b: Card) = a.face compare b.face
}

private[poker] object CardColorOrdering extends Ordering[Card] {
	def compare(a: Card, b: Card) = a.suit compare b.suit
}
