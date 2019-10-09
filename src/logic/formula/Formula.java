package logic.formula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import logic.formula.SetFormula.Type;
import logic.helper.BinaryIterator;
import logic.helper.LogicHelper;
import logic.helper.Pair;
import logic.helper.Parser;

/**
 * 
 *
 * Represents a logical formula 
 * <br>
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
	
	public SetFormula asCNF()
	{
		Set<Set<Literal>> set = new HashSet<>();
		Set<Formula> set1 = this.asANDFormulaSet();
		for(Formula f:set1)
		{
			Set<Formula> set2 = f.asORFormulaSet();
			Set<Literal> set3 = new HashSet<>();
			for(Formula g: set2)
			{
				Literal l = g.toLiteral();
				if(l == null)
					return null;
				else
					set3.add(l);
			}
			set.add(set3);
		}
		return new SetFormula(set, Type.CNF);
	}
	
	public SetFormula asDNF()
	{
		Set<Set<Literal>> set = new HashSet<>();
		Set<Formula> set1 = this.asORFormulaSet();
		for(Formula f:set1)
		{
			Set<Formula> set2 = f.asANDFormulaSet();
			Set<Literal> set3 = new HashSet<>();
			for(Formula g: set2)
			{
				Literal l = g.toLiteral();
				if(l == null)
					return null;
				else
					set3.add(l);
			}
			set.add(set3);
		}
		return new SetFormula(set, Type.DNF);
	}
	
	public Literal toLiteral()
	{
		if(this instanceof AtomicFormula)
			return new Literal((AtomicFormula)this, true);
		if(this instanceof NOTFormula)
		{
			Formula arg = ((NOTFormula) this).getArgumentFormula();
			if(arg instanceof AtomicFormula)
				return new Literal((AtomicFormula)arg, false);
		}
		return null;
	}
	
	private Set<Formula> asORFormulaSet()
	{
		if(this instanceof ORFormula)
			return new HashSet<>(((ORFormula)this).getFormulaList());
		return LogicHelper.createSet(this);
	}
	

	private Set<Formula> asANDFormulaSet()
	{
		if(this instanceof ANDFormula)
			return new HashSet<>(((ANDFormula)this).getFormulaList());
		return LogicHelper.createSet(this);
	}
	
	private List<Formula> asORFormulaList()
	{
		if(this instanceof ORFormula)
			return ((ORFormula)this).getFormulaList();
		return LogicHelper.createList(this);
	}
	

	private List<Formula> asANDFormulaList()
	{
		if(this instanceof ANDFormula)
			return ((ANDFormula)this).getFormulaList();
		return LogicHelper.createList(this);
	}
	
	public Pair<SetFormula, SetFormula> toNormalForm()
	{
		if(this instanceof AtomicFormula)
		{
			Literal l = new Literal((AtomicFormula)this, true);
			return new Pair<>(new SetFormula(LogicHelper.createSet(LogicHelper.createSet(l)), Type.CNF), new SetFormula(LogicHelper.createSet(LogicHelper.createSet(l)), Type.DNF));
		}
		if(this instanceof NOTFormula)
		{
			Formula f = ((NOTFormula)this).getArgumentFormula();
			Pair<SetFormula, SetFormula> pair = f.toNormalForm();
			return new Pair<>(pair.getSecond().negate(), pair.getFirst().negate());
		}
		List<Pair<SetFormula, SetFormula>> normalForms = new ArrayList<>();
		if(this instanceof ANDFormula)	
		{
			((ANDFormula)this).getFormulaList().forEach(f -> normalForms.add(f.toNormalForm()));
			//Create CNF and DNF
			SetFormula cnf = SetFormula.getEmptySetFormula(Type.CNF); //i.e. {} which is a true formula in CNF
			SetFormula dnf = new SetFormula(Type.DNF); //i.e. {{}} which is a true formula in DNF
			for(Pair<SetFormula, SetFormula> pair:normalForms)
			{
				cnf = cnf.and(pair.getFirst());
				dnf = dnf.and(pair.getSecond());
			}
			return new Pair<>(cnf, dnf);
		}
		if(this instanceof ORFormula)	
		{
			((ORFormula)this).getFormulaList().forEach(f -> normalForms.add(f.toNormalForm()));
			//Create CNF and DNF
			SetFormula cnf = new SetFormula(Type.CNF); // i.e. {{}} which is a false formula in CNF
			SetFormula dnf = SetFormula.getEmptySetFormula(Type.DNF); // i.e. {} which is a false formula in DNF
			for(Pair<SetFormula, SetFormula> pair:normalForms)
			{
				cnf = cnf.or(pair.getFirst());
				dnf = dnf.or(pair.getSecond());
			}
			return new Pair<>(cnf, dnf);
		}
		throw new IllegalArgumentException();
	}
	
	
	public Formula simplify()
	{
		if(this instanceof NOTFormula)
		{
			NOTFormula f = (NOTFormula) this;
			Formula arg = f.getArgumentFormula().simplify();
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
			return ((NOTFormula)arg).getArgumentFormula();
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
			List<Formula> list2 = f.asANDFormulaList();
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
			List<Formula> list2 = f.asORFormulaList();
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
	
	public boolean isTrue(Function<AtomicFormula, Boolean> interpretation)
	{
		if(this instanceof AtomicFormula)
		{
			Boolean b = interpretation.apply((AtomicFormula) this);
			if(b == null)
				throw new IllegalArgumentException("model is not defined on all variables in the formula");
			return b.booleanValue();
		}
		if(this instanceof NOTFormula)
		{
			return !((NOTFormula)this).getArgumentFormula().isTrue(interpretation);
		}
		if(this instanceof ANDFormula)
		{
			List<Formula> args = ((ANDFormula)this).getFormulaList();
			for(Formula f:args)
			{
				if(!f.isTrue(interpretation))
					return false;
			}
			return true;
		}
		if(this instanceof ORFormula)
		{
			List<Formula> args = ((ORFormula)this).getFormulaList();
			for(Formula f:args)
			{
				if(f.isTrue(interpretation))
					return true;
			}
			return false;
		}
		throw new IllegalStateException("This type of formula is not supported");
	}
	
	public Set<AtomicFormula> getAtomicFormulaOccurences()
	{
		if(this instanceof AtomicFormula)
			return LogicHelper.createSet((AtomicFormula)this);
		if(this instanceof NOTFormula)
			return ((NOTFormula)this).getArgumentFormula().getAtomicFormulaOccurences();
		if(this instanceof ANDFormula)
		{
			Set<AtomicFormula> set = new HashSet<>();
			for(Formula f:((ANDFormula)this).getFormulaList())
				set.addAll(f.getAtomicFormulaOccurences());
			return set;
		}
		if(this instanceof ORFormula)
		{
			Set<AtomicFormula> set = new HashSet<>();
			for(Formula f:((ORFormula)this).getFormulaList())
				set.addAll(f.getAtomicFormulaOccurences());
			return set;
		}
		throw new IllegalStateException("This type of formula is not supported");
	}
	
	public static AtomicFormula getNewAtomicFormula(Set<AtomicFormula> set)
	{
		int c = 0;
		AtomicFormula f;
		do
		{
			f = new AtomicFormula("X_"+c);
			c++;
		}
		while(set.contains(f));
		return f;
	}
	
	public Formula pushDownNOT()
	{
		if(this instanceof AtomicFormula)
			return this;
		if(this instanceof ANDFormula)
		{
			List<Formula> arg = new ArrayList<>();
			((ANDFormula)this).getOrigList().forEach(e -> arg.add(e.pushDownNOT()));;
			return new ANDFormula(arg);
		}
		if(this instanceof ORFormula)
		{
			List<Formula> arg = new ArrayList<>();
			((ORFormula)this).getOrigList().forEach(e -> arg.add(e.pushDownNOT()));;
			return new ORFormula(arg);
		}
		if(this instanceof NOTFormula)
		{
			Formula arg = ((NOTFormula)this).getArgumentFormula();
			return pushDownNOTRec(arg);
		}
		throw new IllegalStateException("Formula not supported");
	}
	
	private static Formula pushDownNOTRec(Formula f)
	{
		if(f instanceof AtomicFormula)
			return new NOTFormula(f);
		if(f instanceof ANDFormula)
		{
			List<Formula> arg = new ArrayList<>();
			ANDFormula and = (ANDFormula)f;
			for(int i = 0;i<and.getLength();i++)
				arg.add(pushDownNOTRec(and.getFormulaAt(i)));
			return new ORFormula(arg);
		}
		if(f instanceof ORFormula)
		{
			List<Formula> arg = new ArrayList<>();
			ORFormula or = (ORFormula)f;
			for(int i = 0;i<or.getLength();i++)
				arg.add(pushDownNOTRec(or.getFormulaAt(i)));
			return new ANDFormula(arg);
		}
		if(f instanceof NOTFormula)
		{
			Formula arg = ((NOTFormula)f).getArgumentFormula();
			return arg.pushDownNOT();
		}
		throw new IllegalStateException("Formula not supported");
	}
	
	public boolean isEquivalentTo(Formula f)
	{
		Set<AtomicFormula> occ = this.getAtomicFormulaOccurences();
		occ.addAll(f.getAtomicFormulaOccurences());
		BinaryIterator<AtomicFormula> it = new BinaryIterator<>(occ);
		while(it.hasNext())
		{
			Function<AtomicFormula, Boolean> interpretation = it.next();
			if(this.isTrue(interpretation) != f.isTrue(interpretation))
				return false;
		}
		return true;
	}
	
	public String toPrettyString()
	{
		if(this instanceof AtomicFormula)
			return this.toString();
		if(this instanceof NOTFormula)
		{
			NOTFormula f = (NOTFormula)this;
			Formula arg = f.getArgumentFormula();
			if(arg instanceof NOTFormula)
				return "¬("+arg.toPrettyString()+")";
			if(arg instanceof ORFormula && ((ORFormula)arg).getLength() > 1)
				return "¬("+arg.toPrettyString()+")";
			if(arg instanceof ANDFormula && ((ANDFormula)arg).getLength() > 1)
				return "¬("+arg.toPrettyString()+")";
			return "¬"+arg.toPrettyString();
		}
		if(this instanceof ANDFormula)
		{
			ANDFormula f = (ANDFormula)this;
			if(f.getLength() == 0)
				return "TRUE";
			if(f.getLength() == 1)
				return f.getFormulaAt(0).toPrettyString();
			StringBuilder sb = new StringBuilder();
			for(int i = 0;i<f.getLength();i++)
			{
				Formula fi = f.getFormulaAt(i);
				String s;
				if((fi instanceof ORFormula && ((ORFormula)fi).getLength() > 1) || (fi instanceof ANDFormula && ((ANDFormula)fi).getLength() > 1))
					s = "("+fi.toPrettyString()+")";
				else 
					s = fi.toPrettyString();
				sb.append(s+" ∧ ");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.deleteCharAt(sb.length()-1);
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}
		if(this instanceof ORFormula)
		{
			ORFormula f = (ORFormula)this;
			if(f.getLength() == 0)
				return "TRUE";
			if(f.getLength() == 1)
				return f.getFormulaAt(0).toPrettyString();
			StringBuilder sb = new StringBuilder();
			for(int i = 0;i<f.getLength();i++)
			{
				Formula fi = f.getFormulaAt(i);
				String s;
				if((fi instanceof ORFormula && ((ORFormula)fi).getLength() > 1) || (fi instanceof ANDFormula && ((ANDFormula)fi).getLength() > 1))
					s = "("+fi.toPrettyString()+")";
				else 
					s = fi.toPrettyString();
				sb.append(s+" ∨ ");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.deleteCharAt(sb.length()-1);
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}
		throw new IllegalStateException();
	}
}
