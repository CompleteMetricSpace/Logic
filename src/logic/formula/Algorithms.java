package logic.formula;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import logic.formula.SetFormula.Type;
import logic.helper.BinaryIterator;
import logic.helper.Holder;
import logic.helper.LogicHelper;
import logic.helper.Triple;

public class Algorithms {
	private Algorithms() {
	};

	/**
	 * Creates an equisatisfiable formula in CNF. 
	 * This formula has a length that is only linearly dependent on the length of the initial formula.
	 * New atomic variables are introduced.
	 * @param f - a formula where all negations all at the roots
	 * @param occurences - the set of all atomic variables that occur in f
	 * @return the triple (F,L,S) where
	 * 			- F is the equisatisfiable formula in CNF
	 * 			- L is the leading literal
	 * 			- S is the set of all atomic formulas that occur in F, including the atomic in L
	 */
	private static Triple<SetFormula, Literal, Set<AtomicFormula>> getEquisatisfiableCNFRec(Formula f, Set<AtomicFormula> occurences) {
		if (f instanceof AtomicFormula) {
			Literal l = new Literal((AtomicFormula) f);
			SetFormula s = SetFormula.getEmptySetFormula(Type.CNF);
			// Just return essentially the inital formula
			return new Triple<>(s,l, occurences);
		}
		if(f instanceof NOTFormula)
		{
			NOTFormula g = (NOTFormula)f;
			Literal l = new Literal((AtomicFormula) g.getArgumentFormula(), false);
			SetFormula s = SetFormula.getEmptySetFormula(Type.CNF);
			// Just return essentially the inital formula
			return new Triple<>(s,l, occurences);
		}
		if(f instanceof ORFormula)
		{
			ORFormula g = (ORFormula)f;
			SetFormula setFormula = SetFormula.getEmptySetFormula(Type.CNF);
			Holder<SetFormula> setFormulaHolder = new Holder<SetFormula>(setFormula);
			Set<AtomicFormula> newOccurences = new HashSet<>(occurences);
			Set<Literal> leadingLiterals = new HashSet<>();
			g.getOrigList().forEach(e -> {
				Triple<SetFormula, Literal, Set<AtomicFormula>> triple = getEquisatisfiableCNFRec(e, newOccurences);
				newOccurences.addAll(triple.getThird());
				leadingLiterals.add(triple.getSecond());
				setFormulaHolder.set(setFormulaHolder.get().and(triple.getFirst()));
			});
			setFormula = setFormulaHolder.get();
			Literal leadingLiteral = new Literal(Formula.getNewAtomicFormula(newOccurences));
			newOccurences.add(leadingLiteral.getFormula());
			Set<Literal> firstSet = new HashSet<>(leadingLiterals);
			firstSet.add(leadingLiteral.negate());
			setFormula = setFormula.and(new SetFormula(LogicHelper.createSet(firstSet), Type.CNF));
			Holder<SetFormula> setFormulaHolder2 = new Holder<SetFormula>(setFormula);
			leadingLiterals.forEach(l -> {
				setFormulaHolder2.set(setFormulaHolder2.get().and(new SetFormula(LogicHelper.createSet(LogicHelper.createSet(leadingLiteral, l.negate())),Type.CNF)));
			});
			setFormula = setFormulaHolder2.get();
			return new Triple<>(setFormula, leadingLiteral, newOccurences);
		}
		if(f instanceof ANDFormula)
		{
			ANDFormula g = (ANDFormula)f;
			SetFormula setFormula = SetFormula.getEmptySetFormula(Type.CNF);
			Holder<SetFormula> setFormulaHolder = new Holder<SetFormula>(setFormula);
			Set<AtomicFormula> newOccurences = new HashSet<>(occurences);
			Set<Literal> leadingLiterals = new HashSet<>();
			g.getOrigList().forEach(e -> {
				Triple<SetFormula, Literal, Set<AtomicFormula>> triple = getEquisatisfiableCNFRec(e, newOccurences);
				newOccurences.addAll(triple.getThird());
				leadingLiterals.add(triple.getSecond());
				setFormulaHolder.set(setFormulaHolder.get().and(triple.getFirst()));
			});
			setFormula = setFormulaHolder.get();
			Literal leadingLiteral = new Literal(Formula.getNewAtomicFormula(newOccurences));
			newOccurences.add(leadingLiteral.getFormula());
			Set<Literal> firstSet = new HashSet<>(LogicHelper.convert(leadingLiterals, l -> l.negate()));
			firstSet.add(leadingLiteral);
			setFormula = setFormula.and(new SetFormula(LogicHelper.createSet(firstSet), Type.CNF));
			Holder<SetFormula> setFormulaHolder2 = new Holder<SetFormula>(setFormula);
			Literal leadingLiteralNegation = leadingLiteral.negate();
			leadingLiterals.forEach(l -> {
				setFormulaHolder2.set(setFormulaHolder2.get().and(new SetFormula(LogicHelper.createSet(LogicHelper.createSet(leadingLiteralNegation, l)),Type.CNF)));
			});
			setFormula = setFormulaHolder2.get();
			return new Triple<>(setFormula, leadingLiteral, newOccurences);
		}
		throw new IllegalArgumentException("Formula type not supported");
	}
	
	public static SetFormula getEquisatisfiableCNF(Formula f)
	{
		f = f.pushDownNOT();
		Set<AtomicFormula> occ = f.getAtomicFormulaOccurences();
		Triple<SetFormula, Literal, Set<AtomicFormula>> triple = getEquisatisfiableCNFRec(f, occ);
		SetFormula setFormula = triple.getFirst();
		return setFormula.and(new SetFormula(triple.getSecond(), Type.CNF));
 	}
	
	/**
	 * Tries to find a model using a truth table. This method has exponential runtime in the worst case.
	 * @param f - a formula
	 * @return a model iff f is satisfiable, null otherwise
	 */
	public static Function<AtomicFormula, Boolean> getModelTT(Formula f)
	{
		Set<AtomicFormula> atomOccurences = f.getAtomicFormulaOccurences();
		BinaryIterator<AtomicFormula> it = new BinaryIterator<>(atomOccurences);
		while(it.hasNext())
		{
			Function<AtomicFormula, Boolean> interpretation = it.next();
			if(f.isTrue(interpretation))
				return interpretation;
		}
		return null;
	}
}
