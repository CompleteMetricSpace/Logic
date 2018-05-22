package logic.formula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import logic.helper.*;

import logic.helper.LogicHelper;

/**
 * 
 *
 * Represents a logical formula
 * 
 * Comparable: ATOMIC < NOT < AND < OR
 * 
 * @author andrei
 *
 */
public abstract class Formula implements Comparable<Formula> 
{
	
	public Formula not()
	{
		return simplifyNOTFormula(this);
	}
	
	public List<List<Literal>> asCNF()
	{
		List<List<Literal>> list = new ArrayList<List<Literal>>();
		List<Formula> list1 = this.asANDFormula();
		for(Formula f:list1)
		{
			List<Formula> list2 = f.asORFormula();
			List<Literal> list3 = new ArrayList<>();
			for(Formula g: list2)
			{
				Literal l = g.toLiteral();
				if(l == null)
					return null;
				else
					list3.add(l);
			}
			list.add(list3);
		}
		return list;
	}
	
	public List<List<Literal>> asDNF()
	{
		List<List<Literal>> list = new ArrayList<List<Literal>>();
		List<Formula> list1 = this.asORFormula();
		for(Formula f:list1)
		{
			List<Formula> list2 = f.asANDFormula();
			List<Literal> list3 = new ArrayList<>();
			for(Formula g: list2)
			{
				Literal l = g.toLiteral();
				if(l == null)
					return null;
				else
					list3.add(l);
			}
			list.add(list3);
		}
		return list;
	}
	
	public Literal toLiteral()
	{
		if(this instanceof AtomicFormula)
			return new Literal((AtomicFormula)this, true);
		if(this instanceof NOTFormula)
		{
			Formula arg = ((NOTFormula) this).getFormula();
			if(arg instanceof AtomicFormula)
				return new Literal((AtomicFormula)arg, false);
		}
		return null;
	}
	
	private List<Formula> asORFormula()
	{
		if(this instanceof ORFormula)
			return ((ORFormula)this).getFormulaList();
		List<Formula> list = new ArrayList<Formula>();
		list.add(this);
		return list;
	}
	

	private List<Formula> asANDFormula()
	{
		if(this instanceof ANDFormula)
			return ((ANDFormula)this).getFormulaList();
		List<Formula> list = new ArrayList<Formula>();
		list.add(this);
		return list;
	}
	
	public Pair<List<List<Literal>>, List<List<Literal>>> toNormalForm()
	{
		if(this instanceof AtomicFormula)
		{
			Literal l = new Literal((AtomicFormula)this, true);
			return new Pair<>(LogicHelper.createList(LogicHelper.createList(l)), LogicHelper.createList(LogicHelper.createList(l)));
		}
		if(this instanceof NOTFormula)
		{
			Formula f = ((NOTFormula)this).getFormula();
			Pair<List<List<Literal>>, List<List<Literal>>> pair = f.toNormalForm();
			List<List<Literal>> newList1 = new ArrayList<>();
			for(List<Literal> list:pair.getFirst())
			{
				List<Literal> tmpList = new ArrayList<>();
				for(Literal l:list)
					tmpList.add(l.negate());
				newList1.add(tmpList);
			}
			List<List<Literal>> newList2 = new ArrayList<>();
			for(List<Literal> list:pair.getSecond())
			{
				List<Literal> tmpList = new ArrayList<>();
				for(Literal l:list)
					tmpList.add(l.negate());
				newList2.add(tmpList);
			}
			return new Pair<>(newList2, newList1);
		}
		if(this instanceof ANDFormula)	
		{
			List<Pair<List<List<Literal>>,List<List<Literal>>>> normalForms = new ArrayList<>();
			for(Formula f: ((ANDFormula)this).getFormulaList())
				normalForms.add(f.toNormalForm());
		}
	}
	
	public Formula simplify()
	{
		if(this instanceof NOTFormula)
		{
			NOTFormula f = (NOTFormula) this;
			Formula arg = f.getFormula().simplify();
			return simplifyNOTFormula(arg);
		}
		if(this instanceof ANDFormula)
		{
			ANDFormula f = (ANDFormula) this;
			List<Formula> argList = new ArrayList<>();
			for(int i = 0;i<f.getLength();i++)
				argList.add(f.getFormulaAt(i).simplify());
			return simplifyANDFormula(argList);
		}
		if(this instanceof ORFormula)
		{
			ORFormula f = (ORFormula) this;
			List<Formula> argList = new ArrayList<>();
			for(int i = 0;i<f.getLength();i++)
				argList.add(f.getFormulaAt(i).simplify());
			return simplifyORFormula(argList);
		}
		return this;
	}
	
	private static Formula simplifyNOTFormula(Formula arg)
	{
		if(arg instanceof NOTFormula)
			return ((NOTFormula)arg).getFormula();
		if(arg.equals(AtomicFormula.TRUE))
			return AtomicFormula.FALSE;
		if(arg.equals(AtomicFormula.FALSE))
			return AtomicFormula.TRUE;
		return new NOTFormula(arg);
	}
	
	private static Formula simplifyANDFormula(List<Formula> argList)
	{
		//Merge 
		List<Formula> list = new ArrayList<>();
		for(Formula f : argList)
		{
			List<Formula> list2 = f.asANDFormula();
			for(Formula g : list2)
			{
				if(list.contains(g.not()))
					return AtomicFormula.FALSE;
				if(g.equals(AtomicFormula.FALSE))
					return AtomicFormula.FALSE;
				if(!g.equals(AtomicFormula.TRUE))
					list.add(g);
			}
		}
		
		//Remove duplicates
		list = LogicHelper.removeDuplicates(list);
		
		//Sort list
		list.sort((a,b) -> a.compareTo(b));
		
		//Return
		if(list.size() == 0)
			return AtomicFormula.TRUE;
		if(list.size() == 1)
			return list.get(0);
		return new ANDFormula(list);
	}
	

	private static Formula simplifyORFormula(List<Formula> argList)
	{
		//Merge 
		List<Formula> list = new ArrayList<>();
		for(Formula f : argList)
		{
			List<Formula> list2 = f.asORFormula();
			for(Formula g : list2)
			{
				if(list.contains(g.not()))
					return AtomicFormula.TRUE;
				if(g.equals(AtomicFormula.TRUE))
					return AtomicFormula.TRUE;
				if(!g.equals(AtomicFormula.FALSE))
					list.add(g);
			}
		}
		
		//Remove duplicates
		list = LogicHelper.removeDuplicates(list);
		
		//Sort list
		list.sort((a,b) -> a.compareTo(b));
		
		//Return
		if(list.size() == 0)
			return AtomicFormula.FALSE;
		if(list.size() == 1)
			return list.get(0);
		return new ORFormula(list);
	}
	
	public static Formula parseFormula(String s)
	{
		Map<AtomicFormula, AtomicFormula> afMap = new HashMap<>();
		return Parser.parseFormula(s, afMap);
	}
	
	
	
}
