package org.gmd.scala.uf

class WQUUF(n: Int) {
	assert(n >= 0);
	var values = new Array[Int](n);
	val sizes = Array.fill(n)(1);
	
	initialize(0);
	
	
	private def initialize(idx: Int){
		if(idx < values.length){
		  values(idx) = idx;
		  initialize(idx+1);
		}
	}
	
	def root(p: Int): Int = {
	  if(values(p) != p){
	    root(values(p));
	  } else {
	    p;
	  }
	}
	
	def union(p: Int, q: Int){
		val i = root(p);
		val j = root(q);
		if(sizes(i) < sizes(j)){
		  values(i) = j;
		  sizes(j) += sizes(i);
		} else {
		  values(j) = i;
		  sizes(i) += sizes(j);
		}
		println(this);
	}
	
	override def toString(): String = {
		return this.values.deep.mkString(" ");
	}
	
}