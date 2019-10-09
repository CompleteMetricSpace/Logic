package logic.helper;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

public class BinaryIterator<T> implements Iterator<Function<T, Boolean>>
{
	Map<T, Integer> num;
	BigInteger count;
	int size;
	
	public BinaryIterator(Set<T> set)
	{
		Iterator<T> it = set.iterator();
		num = new HashMap<>();
		int c = 0;
		while(it.hasNext())
		{
			num.put(it.next(), c);
			c++;
		}
		count = new BigInteger("0");
		size = set.size();
	}
	
	@Override
	public boolean hasNext() {
		return count.bitCount() <= size;
	}

	@Override
	public Function<T, Boolean> next() {
		if(!hasNext())
			throw new NoSuchElementException();
		BigInteger tmp = count;
		
		Function<T,Boolean> result =  t -> tmp.testBit(num.get(t));
		
		//Increment count:
		count = count.add(BigInteger.ONE);
		
		return result; 
	}
	
}
