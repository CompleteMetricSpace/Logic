package logic.helper;

import logic.helper.Pair;

public class Pair<U, V> 
{
    U u;
    V v;

    public Pair(U u, V v)
    {
        this.u = u;
        this.v = v;
    }

    public U getFirst()
    {
        return u;
    }

    public V getSecond()
    {
        return v;
    }

    public void setFirst(U u)
    {
        this.u = u;
    }

    public void setSecond(V v)
    {
        this.v = v;
    }

    @Override
	public String toString()
    {
        return "Pair[" + u + " | " + v + "]";
    }

    @Override 
    public int hashCode()
    {
    	return u.hashCode()+31*v.hashCode();
    }
    
    @Override 
    public boolean equals(Object b)
    {
    	if(!(b instanceof Pair))
    		return false;
    	Pair<?,?> p = (Pair<?,?>)b;
    	return p.getFirst().equals(this.getFirst()) && p.getSecond().equals(this.getSecond());
    }
}