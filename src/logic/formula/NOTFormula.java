package logic.formula;

import logic.helper.LogicHelper;

public class NOTFormula extends Formula
{
	Formula formula;

	public NOTFormula(Formula formula) {
		super();
		this.formula = formula;
	}
	
	public Formula getFormula() {
		return formula;
	}

	@Override
	public boolean equals(Object arg0) 
	{
		if(arg0 instanceof NOTFormula)
			return ((NOTFormula)arg0).getFormula().equals(formula);
		return false;
	}

	@Override
	public int hashCode() {
			return formula.hashCode();
	}

	@Override
	public String toString() {
		return "NOT("+formula.toString()+")";
	}

	@Override
	public int compareTo(Formula f)
	{
		if(f instanceof AtomicFormula)
			return 1;
		if(f instanceof ANDFormula || f instanceof ORFormula)
			return -1;
		if(f instanceof NOTFormula)
			return formula.compareTo(((NOTFormula)f).formula);
		throw new IllegalArgumentException();
	}
	
	
}
