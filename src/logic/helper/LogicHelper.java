package logic.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class LogicHelper
{
	public static <T> List<T> removeDuplicates(List<T> list)
	{
		ArrayList<T> newList = new ArrayList<>();
		list.forEach(t -> {
			if (!newList.contains(t))
				newList.add(t);
		});
		return newList;
	}

	public static <T> Set<Set<T>> powerSet(Set<T> set)
	{
		if (set.isEmpty())
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
		for (Set<T> s : powerSet)
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
		for (int i = 0; i < array.length; i++)
			if (array[i].equals(elem))
				return true;
		return false;
	}

	public static int count(String s, char c)
	{
		int counter = 0;
		for (int i = 0; i < s.length(); i++)
		{
			if (s.charAt(i) == c)
			{
				counter++;
			}
		}
		return counter;
	}

	public static <T extends Comparable<T>> int compare(List<T> l1, List<T> l2)
	{
		Iterator<T> it1 = l1.iterator();
		Iterator<T> it2 = l2.iterator();
		while (it1.hasNext() && it2.hasNext())
		{
			int c = it1.next().compareTo(it2.next());
			if (c != 0)
				return c;
		}
		if (!it1.hasNext() && !it2.hasNext())
			return 0;
		if (it1.hasNext())
			return 1;
		else
			return -1;
	}

	@SafeVarargs
	public static <T> List<T> createList(T... elem)
	{
		List<T> list = new ArrayList<>();
		for (T t : elem)
			list.add(t);
		return list;
	}

	@SafeVarargs
	public static <T> Set<T> createSet(T... elem)
	{
		Set<T> set = new HashSet<>();
		for (T t : elem)
			set.add(t);
		return set;
	}

	/**
	 * Given a list of sets of sets [{S_11,...,S_1k},...,{S_m1,...,S_mk}] this
	 * procedure computes the set {L_1,...,L_n} where each L_i is a union of the
	 * sets S_{1,i_1}, ... , S_{m,i_m} where i_1, ... ,i_m are indices
	 * 
	 * @param list
	 * @return
	 */
	public static <T> Set<Set<T>> unify_sets(List<Set<Set<T>>> list)
	{
		if (list.size() == 0)
			return new HashSet<>();
		if (list.size() == 1)
			return list.get(0);
		Set<Set<T>> head = list.get(0);
		List<Set<Set<T>>> tail = list.subList(1, list.size());
		Set<Set<T>> rec = unify_sets(tail);
		Set<Set<T>> result = new HashSet<>();
		head.forEach(set -> {
			rec.forEach(recSet -> {
				Set<T> tmpSet = new HashSet<>(set);
				tmpSet.addAll(recSet);
				result.add(tmpSet);
 			});
 		});
		return result;
	}

	public static <T> boolean isSubSetOf(Set<T> sub, Set<T> set)
	{
		for (T t : sub)
		{
			if (!set.contains(t))
				return false;
		}
		return true;
	}

	public static <T, E> Set<T> convert(Set<E> set, Function<E, T> f)
	{
		Set<T> newSet = new HashSet<T>();
		set.forEach(e -> newSet.add(f.apply(e)));
		return newSet;
	}

	public static <T> String setToString(Set<T> set)
	{
		StringBuilder sb = new StringBuilder("{");
		for (T t : set)
			sb.append(t.toString() + ", ");
		if (!set.isEmpty())
		{
			sb.deleteCharAt(sb.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("}");
		return sb.toString();
	}
	
	public static <T> String setOfSetToString(Set<Set<T>> set)
	{
		StringBuilder sb = new StringBuilder("{");
		for (Set<T> t : set)
			sb.append(LogicHelper.setToString(t)+ ", ");
		if (!set.isEmpty())
		{
			sb.deleteCharAt(sb.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("}");
		return sb.toString();
	}
}
