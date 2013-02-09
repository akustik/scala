for(n <- 1 until 20){
	var sum = 0
	for(i <- 1 to n)
		for(j <- 1 to i*i*i)
				sum += 1

	println(n + "=>" + sum +  ": " + n*n*n + "-" + n*n*n*n)
}
