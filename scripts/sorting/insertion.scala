val values = Array(13, 15, 55, 63, 74, 37, 84, 78, 49, 69 )

for(i <- 1 until values.size)
{	
	for(j <- 0 until i)
	{
		val left = i - j - 1
		val right = i - j
		if(values(right) < values(left))
		{
			val swap = values(left)
			values(left) = values(right)
			values(right) = swap

	                values.map(x => print(x + " "))
	                println()

		}
	} 
}

