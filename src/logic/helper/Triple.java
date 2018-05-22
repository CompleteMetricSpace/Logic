package logic.helper;

public class Triple<U, V, W> 
{
    U u;
    V v;
    W w;

    public Triple(U u, V v, W w)
    {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    public U getFirst()
    {
        return u;
    }

    public V getSecond()
    {
        return v;
    }
    
    public W getThird()
    {
        return w;
    }

    public void setFirst(U u)
    {
        this.u = u;
    }

    public void setSecond(V v)
    {
        this.v = v;
    }
    
    public void setThird(W w)
    {
        this.w = w;
    }

    @Override
	public String toString()
    {
        return "Triple[" + u + " | " + v + "|" + w + "]";
    }

    @Override 
    public int hashCode()
    {
    	return u.hashCode()+31*v.hashCode()+31*31*w.hashCode();
    }
    
    @Override 
    public boolean equals(Object b)
    {
    	if(!(b instanceof Triple))
    		return false;
    	Triple<?,?,?> p = (Triple<?,?,?>)b;
    	return p.getFirst().equals(this.getFirst()) && p.getSecond().equals(this.getSecond()) && p.getThird().equals(this.getThird());
    }
}
