package logic.formula;

public class Literal 
{
	AtomicFormula formula;
	boolean positive;
	
	public Literal(AtomicFormula fomula, boolean positive)
	{
		this.formula = fomula;
		this.positive = positive;
	}

	public AtomicFormula getFormula() {
		return formula;
	}

	public boolean isPositive() {
		return positive;
	}
	
	public Literal negate()
	{
		return new Literal(formula, !positive);
	}
	
	public Formula toFormula()
	{
		if(isPositive())
			return formula;
		else
			return new NOTFormula(formula);
	}

	@Override
	public int hashCode() {
		return formula.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Literal)
		{
			Literal l = ((Literal)obj);
			return l.isPositive() == this.isPositive() && l.getFormula().equals(this.getFormula());
		}
		return false;
	}

	@Override
	public String toString() {
		if(isPositive())
			return formula.toString();
		else
			return "-"+formula.toString();
	}
	
	
}
