package org.gmd.scala.uf

object Ex2 extends App {
	val test = new WQUUF(10);
	test.union(2,3);
	test.union(1,9);
	test.union(1,6);
	test.union(6,5);
	test.union(7,4);
	test.union(7,2);
	test.union(6,2);
	test.union(8,6);
	test.union(0,7);
}