package logic.helper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import logic.formula.ANDFormula;
import logic.formula.AtomicFormula;
import logic.formula.Formula;
import logic.formula.NOTFormula;
import logic.formula.ORFormula;

public class Parser {

	public static Formula parseFormula(String u, Map<AtomicFormula, AtomicFormula> afMap) {
		u = u.trim();
		final String s = u;

		if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')')
			return parseFormula(s.substring(1, s.length() - 1), afMap);

		if (s.startsWith("NOT")) {
			List<Formula> argList = parseArgsFormula(s.substring(4, s.length() - 1), afMap);
			if (argList.size() != 1)
				throw new IllegalArgumentException();
			return new NOTFormula(argList.get(0));
		}
		if (s.startsWith("AND")) {
			List<Formula> argList = parseArgsFormula(s.substring(4, s.length() - 1), afMap);
			return new ANDFormula(argList);
		}
		if (s.startsWith("OR")) {
			List<Formula> argList = parseArgsFormula(s.substring(3, s.length() - 1), afMap);
			return new ORFormula(argList);
		}
		AtomicFormula g = new AtomicFormula(s);
		AtomicFormula f = afMap.get(g);
		if (f == null) {
			afMap.put(g, g);
			return g;
		}
		return f;

	}

	public static boolean isOperator(char c) {
		if ("+-*/^".indexOf(c) > 0)
			return true;
		return false;
	}

	public static int find_index_of(String s, String f) {
		int index = s.indexOf(f);
		while (index != -1 && isInBrackets(s, index))
			index = s.indexOf(f, index + 1);
		return index;
	}

	public static int find_last_index_of(String s, String f) {
		int index = s.lastIndexOf(f);
		while (index != -1 && isInBrackets(s, index))
			index = s.lastIndexOf(f, index - 1);
		return index;
	}

	public static boolean isInBrackets(String s, int index) {
		int b = 0;
		int c = 0;
		int d = 0;
		int i;
		for (i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '(')
				b++;
			if (s.charAt(i) == ')')
				b--;
			if (s.charAt(i) == '{')
				c++;
			if (s.charAt(i) == '}')
				c--;
			if (s.charAt(i) == '"') {
				if (d == 1)
					d--;
				else
					d++;
			}

			if (i == index) {
				break;
			}
		}
		if (b == 0 && c == 0 && d == 0)
			return false;
		else
			return true;
	}

	public static List<Formula> parseArgsFormula(String s, Map<AtomicFormula, AtomicFormula> afMap) {
		int indexComma = s.indexOf(",");
		while (isInBrackets(s, indexComma) && indexComma != -1)
			indexComma = s.indexOf(",", indexComma + 1);
		if (indexComma == -1) {
			List<Formula> ret = new ArrayList<>();
			ret.add(parseFormula(s, afMap));
			return ret;
		}
		String g = s.substring(0, indexComma);
		String rest = s.substring(indexComma + 1, s.length());
		List<Formula> r = parseArgsFormula(rest, afMap);
		Formula f = parseFormula(g, afMap);
		r.add(0, f);
		return r;
	}

}