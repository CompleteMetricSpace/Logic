package logic.formula;

import logic.helper.LogicHelper;

public class AtomicFormula extends Formula {
	
	public static AtomicFormula TRUE = new AtomicFormula("TRUE");
	public static AtomicFormula FALSE = new AtomicFormula("FALSE");
	
	String name;
	
	public AtomicFormula(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public boolean equals(Object b)
	{
		if(b instanceof AtomicFormula)
			return ((AtomicFormula)b).getName().equals(name);
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
	
	@Override
	public int compareTo(Formula f)
	{
		if(f instanceof AtomicFormula)
			return name.compareTo(((AtomicFormula)f).name);
		return -1;
	}
	
	
}
