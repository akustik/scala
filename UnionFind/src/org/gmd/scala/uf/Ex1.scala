package org.gmd.scala.uf

object Execute extends App {
	val test = new QFUF(10);
	test.union(8,1);
	test.union(9,3);
	test.union(4,1);
	test.union(0,1);
	test.union(2,4);
	test.union(7,5);
	println(test);
}