package logic.formula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import logic.helper.LogicHelper;
import logic.helper.Pair;

/**
 * Represents a Formula in CNF or DNF in set notation
 * @author andrei
 *
 */
public class SetFormula {
	private Set<Set<Literal>> formula;
	private Type type;

	enum Type {
		CNF, DNF
	};

	/**
	 * Creates a new empty formula, i.e. {{}}
	 * @param type - formula type
	 */
	public SetFormula(Type type) {
		this(LogicHelper.createSet(new HashSet<Literal>()), type, false, false);
	}
	
	/**
	 * Creates a new totally empty fomula, i.e. {}
	 * @param type - formula type
	 * @return the formula {}
	 */
	public static SetFormula getEmptySetFormula(Type type) {
		return new SetFormula(new HashSet<>(), type, false, false);
	}
	
	/**
	 * Creates a new formula from a set
	 * @param formula - a set of sets of literals
	 * @param type - formula type
	 */
	public SetFormula(Set<Set<Literal>> formula, Type type) {
		// Make copy and simplify
		this(formula, type, true, true);
	}
	
	/**
	 * Creates a new formula from a single literal
	 * @param literal - a literal
	 * @param type - formula type
	 */
	public SetFormula(Literal literal, Type type) {
		// Make copy and simplify
		this(LogicHelper.createSet(LogicHelper.createSet(literal)), type, true, true);
	}

	/**
	 * Creates a new formula from a set
	 * @param formula - a set of sets of literals
	 * @param type - formula type
	 * @param simplify - if true, this constructor simplifies this formula on creation
	 * @param makecopy - if true, the set of sets of literals is copied
	 */
	private SetFormula(Set<Set<Literal>> formula, Type type, boolean simplify, boolean makecopy) {
		if(!makecopy)
			this.formula = formula;
		else
		{
			Set<Set<Literal>> set = new HashSet<>();
			formula.forEach(s -> {
				if(simplify)
				{
					Set<Literal> s2 = removeTautologies(s);
					if(s2 != null)
						set.add(s2);
				}
				else
					set.add(s);
			});
			this.formula = set;
		}
		this.type = type;
	}

	/**
	 * @return type of formula, i.e. CNF or DNF
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * @return true if this formula is in CNF, false otherwise
	 */
	public boolean isCNF()
	{
		return type == Type.CNF;
	}
	
	/**
	 * @return true if this formula is in DNF, false otherwise
	 */
	public boolean isDNF()
	{
		return type == Type.DNF;
	}

	/**
	 * @return formula as set of sets of literals
	 */
	public Set<Set<Literal>> asSet() {
		// Make defensive copy
		Set<Set<Literal>> set = new HashSet<>();
		formula.forEach(s -> set.add(new HashSet<>(s)));
		return set;
	}

	/**
	 * @return formula as sets of sets of literals that is unmodifiable (but the inner sets are modifiable)
	 */
	public Set<Set<Literal>> asReadOnlySet() {
		// No copy, but this does NOT defend against access to inner sets
		return Collections.unmodifiableSet(formula);
	}
	
	/**
	 * @return formula as a Formula object
	 */
	public Formula asFormula() {
		if(isCNF())
		{
			List<Formula> list = new ArrayList<>();
			for(Set<Literal> s:formula)
			{
				List<Formula> list2 = new ArrayList<>();
				s.forEach(l -> list2.add(l.asFormula()));
				list.add(new ORFormula(list2));
			}
			return new ANDFormula(list).simplify();
		}
		else
		{
			List<Formula> list = new ArrayList<>();
			for(Set<Literal> s:formula)
			{
				List<Formula> list2 = new ArrayList<>();
				s.forEach(l -> list2.add(l.asFormula()));
				list.add(new ANDFormula(list2));
			}
			return new ORFormula(list).simplify();
		}
	}

	/**
	 * @param f - another formula
	 * @return the logical conjunction with f
	 */
	public SetFormula and(SetFormula f)
	{
		if(this.getType() != f.getType())
			throw new IllegalArgumentException("Formula not of same type");
		if(this.getType() == Type.CNF) 
		{
			Set<Set<Literal>> set = new HashSet<>(this.formula);
			f.formula.forEach(s -> set.add(s));
			return new SetFormula(set, Type.CNF, false, false);
		}
		else
		{
			Set<Set<Literal>> result = new HashSet<>();
			this.formula.forEach(s1 -> {
				f.formula.forEach(s2 -> {
					Set<Literal> set = new HashSet<>(s1);
					set.addAll(s2);
					set = removeTautologies(set);
					if(set != null)
						result.add(set);
				});
			});
			return new SetFormula(result, Type.DNF, false, false);
		}
	}
	
	/**
	 * @param f - another formula
	 * @return the logical disjunction with f 
	 */
	public SetFormula or(SetFormula f)
	{
		if(this.getType() != f.getType())
			throw new IllegalArgumentException("Formula not of same type");
		if(this.getType() == Type.DNF)
		{
			Set<Set<Literal>> set = new HashSet<>(this.formula);
			f.formula.forEach(s -> set.add(s));
			return new SetFormula(set, Type.DNF, false, false);
		}
		else
		{
			Set<Set<Literal>> result = new HashSet<>();
			this.formula.forEach(s1 -> {
				f.formula.forEach(s2 -> {
					Set<Literal> set = new HashSet<>(s1);
					set.addAll(s2);
					set = removeTautologies(set);
					if(set != null)
						result.add(set);
				});
			});
			return new SetFormula(result, Type.CNF, false, false);
		}
	}
	
	/**
	 * @return the logical negation of this formula
	 */
	public SetFormula negate()
	{
		Set<Set<Literal>> set = new HashSet<>();
		this.formula.forEach(s -> {
			Set<Literal> set2 = new HashSet<>();
			s.forEach(l -> set2.add(l.negate()));
			set.add(set2);
		});
		Type t = this.isCNF()?Type.DNF:Type.CNF;
		return new SetFormula(set, t, false, false);
	}
	
	/**
	 * Returns a horn formula if possible
	 * @return a HornFormula object if this is a horn formula, null otherwise
	 */
	public HornFormula asHornFormula()
	{
		if(!this.isCNF())
			return null;
		Set<Pair<Set<AtomicFormula>, AtomicFormula>> set = new HashSet<>();
		for(Set<Literal> s:formula)
		{
			Set<AtomicFormula> positive = new HashSet<>();
			Set<AtomicFormula> negative = new HashSet<>();
			s.forEach(l -> {
				if(l.isPositive())
					positive.add(l.getFormula());
				else
					negative.add(l.getFormula());
			});
			if(positive.size() > 1)
				return null; // this is not a horn formula
			if(positive.size() == 1)
				set.add(new Pair<>(negative, positive.iterator().next()));
			else
				set.add(new Pair<>(negative, null));
		}
		return new HornFormula(set);
	}

	/**
	 * Removes statements as A AND NOT(A) or A OR NOT(A)
	 * 
	 * @param set
	 *            - set of literals to be simplified
	 * @return  null if the set has a subset of the form {A,NOT(A)},
	 *         and set otherwise
	 */
	private static Set<Literal> removeTautologies(Set<Literal> set) {
		Set<Literal> newSet = new HashSet<>();
		for (Literal l : set) {
			if (newSet.contains(l.negate()))
				return null;
			newSet.add(l);
		}
		return newSet;
	}
	
	
	/**
	 * Computes the intersection of literals:
	 * 
	 * @param set1 - a set of literals
	 * @param set2 - a set of literals
	 * @return a set of literals L such that L is in set1 and NOT(L) is in set2
	 */
	private static Set<Literal> literalIntersection(Set<Literal> set1, Set<Literal> set2)
	{
		Set<Literal> literalSet = new HashSet<>();
		set1.forEach(l -> {
			if(set2.contains(l.negate()))
				literalSet.add(l);
		});
		return literalSet;
	}
	
	/**
	 * Computes the resolution of two literal sets with respect to a literal
	 * @param set1 - a set of literals
	 * @param set2 - a set of literals 
	 * @param l - a literal such that l is in set1 and NOT(l) is in set2
	 * @return the resolution of set1 and set2, i.e. (set1 - {l}) U (set2 - {NOT(L)})
	 */
	private static Set<Literal> resolution(Set<Literal> set1, Set<Literal> set2, Literal l)
	{
		Set<Literal> resolvent = new HashSet<>(set1);
		resolvent.remove(l);
		Set<Literal> tmp = new HashSet<>(set2);
		tmp.remove(l.negate());
		resolvent.addAll(tmp);
		return resolvent;
	}
	
	/**
	 * Computes the resolvent of this formula in CNF
	 * @return the resolvent Res
	 */
	public SetFormula resolvent()
	{
		if(!isCNF())
			throw new IllegalStateException("Formula is not in CNF");
		Set<Set<Literal>> newSet = new HashSet<>();
		formula.forEach(set1 -> {
			formula.forEach(set2 ->{
				Set<Literal> literalSet = literalIntersection(set1, set2);
				literalSet.forEach(l -> newSet.add(resolution(set1, set2, l)));
			});
		});
		newSet.addAll(formula);
		return new SetFormula(newSet, Type.CNF);
	}
	
	public SetFormula resolventSet()
	{
		SetFormula oldRes = this;
		SetFormula newRes = this.resolvent();
		while(!oldRes.equals(newRes))
		{
			oldRes = newRes;
			newRes = newRes.resolvent();
		}
		return oldRes;
	}
	
	public boolean isSatisfiable()
	{
		if(!isCNF())
			throw new IllegalStateException("Formula is not in CNF");
		SetFormula s = this;
		SetFormula r = s.resolvent();
		while(!r.equals(s))
		{
			s = r;
			r = r.resolvent();
			if(r.asReadOnlySet().contains(new HashSet<>()))
				return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof SetFormula))
			return false;
		SetFormula f = (SetFormula) obj;
		return f.getType() == this.getType() && f.formula.equals(this.formula);
	}

	@Override
	public String toString() 
	{
		return LogicHelper.setOfSetToString(formula);
	}
	
	
}
