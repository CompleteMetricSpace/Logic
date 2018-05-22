package logic.helper;

public class UPair<U> 
{
	U u, v;


    public UPair(U u, U v)
    {
        this.u = u;
        this.v = v;
    }

    public U getFirst()
    {
        return u;
    }

    public U getSecond()
    {
        return v;
    }

    public void setFirst(U u)
    {
        this.u = u;
    }

    public void setSecond(U v)
    {
        this.v = v;
    }

    @Override
	public String toString()
    {
        return "UPair[" + u + " | " + v + "]";
    }

    @Override 
    public boolean equals(Object b)
    {
    	if(!(b instanceof UPair))
    		return false;
    	UPair<?> p = (UPair<?>)b;
    	boolean x = p.getFirst().equals(this.getFirst()) && p.getSecond().equals(this.getSecond());
    	boolean y = p.getFirst().equals(this.getSecond()) && p.getSecond().equals(this.getFirst());
    	return x || y;
    }
    
    @Override 
    public int hashCode()
    {
    	return u.hashCode()+v.hashCode();
    }
}
