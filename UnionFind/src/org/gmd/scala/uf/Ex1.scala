package org.gmd.scala.uf

import scala.io.Source

object Ex1{
	
	def main(args:Array[String]) = {
		val test = new QFUF(10)
		for(line <- Source.fromFile(args(0)).getLines(); union <- line.split(" ")){
			val p = union.split("-")(0)
			val q =  union.split("-")(1)
			print(p + "-" + q + ": ")
			test.union(p.toInt, q.toInt)
		}

		//Check the result
		val expected = Source.fromFile(args(1)).getLines().next
		println(if(expected == test.toString) "Success" else "Expected " +  expected + ", actual " + test)

	}
}
