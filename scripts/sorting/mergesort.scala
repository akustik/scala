object mergesort {

	
	def merge(a: Array[Int], aux: Array[Int], lo: Int, mid: Int, hi: Int) = {
		for(k <- lo to hi)
		{
			aux(k) = a(k)
		}

		var i = lo
		var j = mid + 1;

		for(k <- lo to hi)
		{
			if(i>mid){
				a(k) = aux(j)
				j += 1
			}else if(j>hi){
				a(k) = aux(i)
				i += 1
			}else if(aux(j) < aux(i)){
				a(k) = aux(j)
				j += 1
			}else {
				a(k) = aux(i)
				i += 1				
			}
		}

		println("Merge: " + a.mkString(" "))

	}

	def sort(a: Array[Int], aux: Array[Int], lo: Int,  hi: Int): Unit = {
		if(hi > lo){
			val mid = lo + (hi - lo)/2
			sort(a, aux, lo, mid)
			sort(a, aux, mid+1, hi)
			merge(a, aux, lo, mid, hi)
		}

	}

	def sort(a: Array[Int]): Unit = {
		var aux = new Array[Int](a.length)
		sort(a, aux, 0, a.length - 1)
	
	}


	def main(args:Array[String]) = {
		val a = args.map(x => x.toInt)	
		println("Initial: " + a.mkString(" "))
		sort(a)
	}

}
