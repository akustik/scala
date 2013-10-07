package org.gmd.scala.pokerspecs

import org.scalatest._
import org.gmd.scala.poker.RankedFactory

class RankedSpec extends FlatSpec {

	"A RankedFactory" should "build a ranked hand from a given valid string" in {
		val r = RankedFactory.build("2s 3s 4s 5s 6s 7c")
		assert(r.name === "Straight Flush")
	}

	it should "throw IllegalArgumentException if an invalid string is give" in {
		intercept[IllegalArgumentException] {
			RankedFactory.build("2p 4p 1242")
		}
	}

	"A straight flush ranked hand" should "be greater than any four of a kind hand" in {
		val sf = RankedFactory.build("2s 3s 4s 5s 6s 7c")
		val fk = RankedFactory.build("As Ac Ah Ad Ks Kh")
		expectResult("Straight Flush"){
			sf.name
		}
		expectResult("Four Of A Kind"){
			fk.name
		}
		assert(sf > fk)
	}

        it should "be greater than another straight flush ranked hand with a lower rank" in {
                val sf1 = RankedFactory.build("2s 3s 4s 5s 6s 7s")
                val sf2 = RankedFactory.build("3c 4c 5c 6c 7c 8c")
                expectResult("Straight Flush"){
                        sf1.name
                }
                expectResult("Straight Flush"){
                        sf2.name
                }
                assert(sf1 < sf2)
        }

        it should "be equal than another straight flush ranked hand with same rank" in {
                val sf1 = RankedFactory.build("2s 3s 4s 5s 6s 7s")
                val sf2 = RankedFactory.build("3c 4c 5c 6c 7c")
                expectResult("Straight Flush"){
                        sf1.name
                }
                expectResult("Straight Flush"){
                        sf2.name
                }
                assert(sf1 >= sf2 && sf2 >= sf1)
        }

}
