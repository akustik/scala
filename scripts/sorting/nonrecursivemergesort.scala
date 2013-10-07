object nonrecursivemergesort {

	
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


	def sort(a: Array[Int]): Unit = {
		var aux = new Array[Int](a.length)
		var N = a.length	
		var sz = 1
		while(sz < N){
			var lo = 0
			while(lo < N - sz) {
				merge(a, aux, lo, lo+sz-1, Math.min(lo+sz+sz-1, N-1))
				lo += sz+sz

			}		
			sz = sz + sz
		}
		
	}


	def main(args:Array[String]) = {
		val a = args.map(x => x.toInt)	
		println("Initial: " + a.mkString(" "))
		sort(a)
	}

}
