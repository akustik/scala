package org.gmd.scala.uf

class QFUF(n: Int) {
	assert(n >= 0);
	var values = new Array[Int](n);
	initialize(0);
	
	private def initialize(idx: Int){
		if(idx < values.length){
		  values(idx) = idx;
		  initialize(idx+1);
		}
	}
	
	def union(p: Int, q: Int){
		val idp = values(p);
		val idq = values(q);
		values = values.map((x: Int) => (if (x == idp) idq else x));
		println(this);
	}
	
	override def toString(): String = {
		return this.values.deep.mkString(" ");
	}
	
}
