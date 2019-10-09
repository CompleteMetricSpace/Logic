import java.util.function.Function;

import logic.formula.Algorithms;
import logic.formula.AtomicFormula;
import logic.formula.Formula;

/**
 *  Current TODO list
 *  	-# Algorithm for computing normal forms (CNF and DNF)
 *  	-# Algorithm for checking the satisfiability of horn formulas
 *  	- Algorithm for creating efficiently an equisatisfiable formula in CNF
 *  	- Resolution algorithm for propositional logic
 * 
 *
 */

public class Main 
{
	
	public static void main(String[] args)
	{
		//Formula f = Formula.parseFormula("AND(A,B,C,D,F,G,H)");
	//	Formula f = Formula.parseFormula("AND(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U)");
		//Formula f = Formula.parseFormula("OR(A,C,AND(D,R,OR(A,B,H,G),NOT(V),OR(AND(A,B),AND(E,W))),AND(X,NOT(OR(Y,Z))))");
	//  Formula f = Formula.parseFormula("AND(OR(X1,Y1),OR(X2,Y2),OR(X3,Y3),OR(X4,Y4),OR(X5,Y5),OR(X7,Y7),OR(X8,Y8),OR(X9,Y9),OR(X10,Y10),OR(X11,Y11))");
	  //  Formula f = Formula.parseFormula("AND(OR(NOT(A),NOT(B),NOT(D)),OR(NOT(E)),OR(NOT(C),A),OR(C),OR(B),OR(NOT(G),D))");
		Formula f = Formula.parseFormula("AND(OR(A,NOT(B),C),OR(NOT(C),NOT(D),A),OR(A,NOT(D),B),OR(NOT(A),NOT(B)))");
		
		//System.out.println("CNF: "+f.toNormalForm().getFirst().asFormula().toString().length());
		//System.out.println("DNF: "+f.toNormalForm().getSecond().asFormula().toString().length());
	   //System.out.println(Algorithms.getEquisatisfiableCNF(f).asFormula().toString());
	    //System.out.println(f.getAtomicFormulaOccurences());
	//	Function<AtomicFormula, Boolean> model = Algorithms.getModelTT(f);
		//System.out.println(model);
		//Formula eqf = Algorithms.getEquisatisfiableCNF(f).asFormula();
		//System.out.println((eqf));
		System.out.println(f.toPrettyString());
		System.out.println(Algorithms.getModelTT(f));
		System.out.println(f.toNormalForm().getFirst().resolventSet());
		
	}
}
