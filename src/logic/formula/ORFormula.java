package logic.formula;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import logic.helper.LogicHelper;

public class ORFormula extends Formula
{
	public List<Formula> formulaList;

	public ORFormula(List<Formula> formulaList) 
	{
		super();
		this.formulaList = formulaList;
	}
	
	public ORFormula(Formula... formulaList) 
	{
		super();
		this.formulaList = new ArrayList<Formula>();
		for(int i = 0;i<formulaList.length;i++)
			this.formulaList.add(formulaList[i]);
	}

	public List<Formula> getFormulaList() {
		return new ArrayList<>(formulaList);
	}
	
	public Formula[] getFormulaArray() {
		return formulaList.toArray(new Formula[formulaList.size()]);
	}
	
	public Formula getFormulaAt(int i)
	{
		return formulaList.get(i);
	}
	
	public int getLength()
	{
		return formulaList.size();
	}

	@Override
	public boolean equals(Object arg0) {
		if(arg0 instanceof ORFormula)
			return ((ORFormula)arg0).getFormulaList().equals(formulaList);
		return false;
	}

	@Override
	public int hashCode() {
		return formulaList.hashCode();
	}

	@Override
	public String toString() {
		String s = "OR(";
		Iterator<Formula> it = formulaList.iterator();
		while(it.hasNext())
			s += it.next().toString() + ",";
		if(s.charAt(s.length()-1) == ',')
			return s.substring(0, s.length()-1)+")";
		else
			return s+")";
	}

	@Override
	public int compareTo(Formula f)
	{
		if(f instanceof AtomicFormula || f instanceof NOTFormula || f instanceof ANDFormula)
			return 1;
		return LogicHelper.compare(formulaList, ((ORFormula)f).formulaList);
	}
	
}
