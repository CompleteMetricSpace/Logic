package logic.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BiMap<S, T> {
	private Map<S, T> map1 = new HashMap<S, T>();
	private Map<T, S> map2 = new HashMap<T, S>();

	public BiMap(Map<S, T> map) {

		map1 = new HashMap<S, T>(map);
		for (Entry<S, T> ent : map1.entrySet()) {
			map2.put(ent.getValue(), ent.getKey());
		}
	}
	
	public static <T,S> BiMap<S,T> createBiMapReverse(Map<T,S> map)
	{
		HashMap<S, T> map2 = new HashMap<S, T>();
		for (Entry<T, S> ent : map.entrySet()) {
			map2.put(ent.getValue(), ent.getKey());
		}
		return new BiMap<S,T>(map2);
	}
	
	public T getImage(S s)
	{
		return map1.get(s);
	}
	
	public S getPreImage(T t)
	{
		return map2.get(t);
	}

}
