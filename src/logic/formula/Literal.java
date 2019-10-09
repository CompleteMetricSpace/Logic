package logic.formula;

public class Literal 
{
	private AtomicFormula formula;
	private boolean positive;
	
	public Literal(AtomicFormula fomula, boolean positive)
	{
		this.formula = fomula;
		this.positive = positive;
	}
	
	public Literal(AtomicFormula fomula)
	{
		this.formula = fomula;
		this.positive = true;
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
	
	public Formula asFormula()
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
