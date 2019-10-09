package logic.formula;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import logic.formula.SetFormula.Type;
import logic.helper.LogicHelper;
import logic.helper.Pair;

/**
 * Represents a Horn Formula as
 * 
 * {[S_1,B_1],...,[S_n,B_n]}
 * 
 * where S_i are sets of non-basic atomic formulas (possibly empty) and B_i are non-basic atomic formulas. 
 * If B_i == null, then this means, that a positive literal B_i is missing.  
 * 
 * @author andrei
 *
 */
public class HornFormula 
{
	private Set<Pair<Set<AtomicFormula>, AtomicFormula>> hornRep;
	
	public HornFormula(Set<Pair<Set<AtomicFormula>, AtomicFormula>> hr)
	{
		hornRep = hr;
	}
	
	public HornFormula(Pair<Set<AtomicFormula>, AtomicFormula> ... h)
	{
		hornRep = new HashSet<>();
		for(Pair<Set<AtomicFormula>, AtomicFormula> p:h)
			hornRep.add(p);
	}
	
	/**
	 * @return	SetFormula representation of this horn formula
	 */
	public SetFormula asSetFormula()
	{
		Set<Set<Literal>> set = new HashSet<>();
		for(Pair<Set<AtomicFormula>, AtomicFormula> p:hornRep)
		{
			Set<Literal> set2 = new HashSet<>();
			p.getFirst().forEach(l -> set2.add(new Literal(l,false)));
			set2.add(new Literal(p.getSecond(), true));
			set.add(set2);
		}
		return new SetFormula(set, Type.CNF);
	}
	
	/**
	 * @return	Formula representation of this horn formula
	 */
	public Formula asFormula()
	{
		return this.asSetFormula().asFormula();
	}
	
	/**
	 * Computes a model of this formula: See "Logic for Computer Scientists" by Uwe Schoening, p. 32
	 * @return	A model of this formula or null if this formula is not satisfiable
	 */
	public Function<AtomicFormula, Boolean> findModel()
	{
		Set<AtomicFormula> flag = new HashSet<>();
		hornRep.forEach(p -> {
			if(p.getFirst().isEmpty())
			{
				if(p.getSecond() != null)
					flag.add(p.getSecond());
			}
		});
		boolean done = false;
		while(!done)
		{
			done = true;
			for(Pair<Set<AtomicFormula>, AtomicFormula> p:hornRep)
			{
				if(LogicHelper.isSubSetOf(p.getFirst(), flag))
				{
					if(p.getSecond() == null)
						return null; //this formula has no model
					else 
					{
						if(flag.add(p.getSecond()))
							done = false;
					}
				}
			}
		}
		return f -> flag.contains(f);
	}
	
	@Override 
	public String toString()
	{
		return hornRep.toString();
	}
}
