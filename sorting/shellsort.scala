val values = Array(14, 97, 33, 82, 40, 83, 43, 54, 34, 68 )
val step = 4
val amount = values.size / step

for(i <- 1 to amount)
{
	for(j <- 0 until i)
	{
		for(m <- 0 to step)
		{
			val left = m + ((i - j - 1) * step)
			val right = m + ((i - j) * step)
			if((right < 10 && left < 10) && values(right) < values(left))
			{
				val swap = values(left)
				values(left) = values(right)
				values(right) = swap

			        println(left + " <--> " + right)
				values.map(x => print(x + " "))
			        println()

			}
		} 
	}
}
