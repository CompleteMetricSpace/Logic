import logic.formula.ANDFormula;
import logic.formula.Formula;

/**
 *  Current TODO list
 *  	- Algorithm for computing normal forms (CNF and DNF)
 *  	- Algorithm for checking the satisfiability of horn formulas
 *  	- Resolution algorithm for propositional logic
 * 
 *
 */

public class Main 
{
	
	public static void main(String[] args)
	{
		Formula f = Formula.parseFormula("AND(OR(A,NOT(C)),OR(A,B,D))");
		System.out.println(f.asCNF());
	}
}
