val values = Array(28, 39, 63, 47, 31, 78, 66, 45, 30, 22 )

for(i <- 0 until values.size)
{
	val minIdx = values.indexOf(values.slice(i, values.size).min)
	val swap = values(i)
	values(i) = values(minIdx)
	values(minIdx) = swap	
		
	values.map(x => print(x + " "));
	println
}

