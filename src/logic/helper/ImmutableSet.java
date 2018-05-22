package logic.helper;

import java.util.Collection;
import java.util.HashSet;

public class ImmutableSet<S> 
{
	private HashSet<S> set;
	
	public ImmutableSet()
	{
		set = new HashSet<>();
	}
	
	public ImmutableSet(Collection<? extends S> c)
	{
		set = new HashSet<>(c);
	}
	
	private ImmutableSet(HashSet<S> c)
	{
		set = c;
	}
	
	public ImmutableSet<S> add(S s)
	{
		HashSet<S> newSet = new HashSet<S>(set);
		newSet.add(s);
		return new ImmutableSet<S>(newSet);
	}
	
	public boolean contains(S s)
	{
		return set.contains(s);
	}
	
	public String toString()
	{
		return set.toString();
	}
	
	public int size()
	{
		return set.size();
	}
}
