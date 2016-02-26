package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class YANG_Pattern extends ErrorTagedNode {

	private String pattern = null;

	private Pattern regexp = null;

	public YANG_Pattern(int id) {
		super(id);
	}

	public YANG_Pattern(yang p, int id) {
		super(p, id);
	}

	public void setPattern(String p) {

		pattern = p;

		String canopattern = pattern.replaceAll("IsBasicLatin", "InBasicLatin");
		pattern = canopattern;
		//		
		// try {
		// regexp = Pattern.compile(pattern);
		// } catch (PatternSyntaxException pse) {
		// YangErrorManager.addWarning(filename, getLine(), getCol(),
		// "pattern_exp",
		// pse.getMessage());
		// }
	}

	public String getPattern() {
		return pattern;
	}

	public boolean checkExp(String exp) {
		try {
			regexp = Pattern.compile(pattern);
			Matcher m = regexp.matcher(exp);
			if (!m.matches())
				return false;
			else
				return true;
		} catch (PatternSyntaxException pse) {
			YangErrorManager.addWarning(filename, getLine(), getCol(),
					"pattern_exp", pse.getMessage());
			return true;
		}

	}

	public boolean isBracked() {
		return super.isBracked();
	}

	public String toString() {
		String result = new String();
		result += "pattern " + pattern;
		if (isBracked()) {
			result += "{\n";
			result += super.toString();
			result += "}";
		} else
			result += ";";
		return result;
	}

}
