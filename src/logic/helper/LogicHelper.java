package logic.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LogicHelper 
{
	public static <T> List<T> removeDuplicates(List<T> list)
	{
		ArrayList<T> newList = new ArrayList<>();
		list.forEach(t -> {
			if(!newList.contains(t))
				newList.add(t);
		});
		return newList;
	}
	
	public static <T> Set<Set<T>> powerSet(Set<T> set)
	{
		if(set.isEmpty())
		{
			Set<T> empty = new HashSet<T>(set);
			Set<Set<T>> powerset = new HashSet<Set<T>>();
			powerset.add(empty);
			return powerset;
		}
		List<T> list = new LinkedList<T>(set);
		T head = list.remove(0);
		Set<Set<T>> powerSet = powerSet(new HashSet<T>(list));
		Set<Set<T>> newPowerSet = new HashSet<Set<T>>();
		for(Set<T> s : powerSet)
		{
			Set<T> st = new HashSet<T>(s);
			st.add(head);
			newPowerSet.add(st);
			newPowerSet.add(s);
		}
		return newPowerSet;
	}
	
	public static <T> boolean contains(T[] array, T elem)
	{
		for(int i = 0;i<array.length;i++)
			if(array[i].equals(elem))
				return true;
		return false;
	}
	
	public static int count(String s, char c) {
		int counter = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == c) {
				counter++;
			}
		}
		return counter;
	}
		
	public static <T extends Comparable<T>> int compare(List<T> l1, List<T> l2)
	{
		Iterator<T> it1 = l1.iterator();
		Iterator<T> it2 = l2.iterator();
		while(it1.hasNext() && it2.hasNext())
		{
			int c = it1.next().compareTo(it2.next());
			if(c != 0)
				return c;
		}
		if(!it1.hasNext() && !it2.hasNext())
			return 0;
		if(it1.hasNext())
			return 1;
		else
			return -1;
	}

	@SafeVarargs
	public static <T> List<T> createList(T... elem)
	{
		List<T> list = new ArrayList<>();
		for(T t: elem)
			list.add(t);
		return list;
	}
}
