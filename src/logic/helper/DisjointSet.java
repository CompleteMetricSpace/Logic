package logic.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class DisjointSet<S> 
{
	/*
	 * A table where s -> (t,n) means that t is the parent of s and n is the rank (height) of s.
	 */
	private Hashtable<S, Pair<S, Integer>> table = new Hashtable<S, Pair<S, Integer>>();
	private int setCount;
	
	public DisjointSet(Set<S> set)
	{
		for (S s : set)
			table.put(s, new Pair<S, Integer>(s, 0));
		setCount = set.size();
	}
	
	/**
	 * Finds representative of the subset that contains x.
	 * @param x
	 * @return the representative of the set that contains x.
	 */
	public S find(S x) {
		Pair<S,Integer> p = table.get(x);
		if(p == null)
			throw new IllegalArgumentException("Element "+x+" is not in this set");
		S parent = p.getFirst();
		while (!parent.equals(x)) {
			x = parent;
			parent = table.get(x).getFirst();
		}
		return x;
	}

	/**
	 * Unites two disjoint sets 
	 * @param x - a member of the first set
	 * @param y - a member of the second set
	 */
	public void union(S x, S y) {
		S rootX = find(x);
		S rootY = find(y);
		if (rootX.equals(rootY))
			return; // Same tree
		setCount--;
		Pair<S, Integer> pairX = table.get(rootX);
		Pair<S, Integer> pairY = table.get(rootY);
		if (pairX.getSecond().compareTo(pairY.getSecond()) > 0)
			pairY.setFirst(rootX);
		else {
			pairX.setFirst(rootY);
			if (pairX.getSecond().equals(pairY.getSecond()))
				pairX.setSecond(pairX.getSecond() + 1);
		}
	}
	
	public int getNumberOfDisjointSets()
	{
		return setCount;
	}
	
	public Set<S> getRepresentatives()
	{
		Set<S> set = new HashSet<>();
		table.keySet().forEach(s -> set.add(find(s)));
		return set;
	}
	
	public Map<S,S> representativeMap()
	{
		Map<S,S> map = new HashMap<>();
		table.keySet().forEach(s -> map.put(s, find(s)));
		return map;
	}
	
	public Function<S,S> representativeFunction()
	{
		Map<S,S> map = representativeMap();
		return s -> map.get(s);
	}
}
