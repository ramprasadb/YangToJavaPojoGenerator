package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;

public class YANG_Enum extends StatuedNode {

	private String enumid = null;
	private YANG_Value value = null;

	private boolean b_value = false;

	public YANG_Enum(int id) {
		super(id);
	}

	public YANG_Enum(yang p, int id) {
		super(p, id);
	}

	public void setEnum(String e) {
		if (e.indexOf('\"') == -1)
			enumid = e.trim();
		else {
			if (e.charAt(e.indexOf('\"') + 1) == ' ')
				YangErrorManager.addError(filename, getLine(), getCol(),
						"bad_value", "enum", e, "leading whitespace");
			else if (e.charAt(e.lastIndexOf('\"') - 1) == ' ')
				YangErrorManager.addError(filename, getLine(), getCol(),
						"bad_value", "enum", e, "trailing whitespace");
			else if (e.equals("\"\""))
				YangErrorManager.addError(filename, getLine(), getCol(),
						"bad_value", "enum", e, "empty string");
			// even its a bad value
			// to provide something if asked
			enumid = unquote(e);
		}
	}

	public String getEnum() {
		return enumid;
	}

	public void setValue(YANG_Value v) {
		if (!b_value) {
			value = v;
			b_value = true;
		} else
			YangErrorManager.addError(filename, v.getLine(), v.getCol(), "unex_kw",
					"value");
	}

	public YANG_Value getValue() {
		return value;
	}

	public boolean isBracked() {
		return b_value || super.isBracked();
	}

	public String toString() {
		String result = "";
		result += "enum " + enumid;
		if (isBracked()) {
			result += " {\n";
			if (value != null)
				result += value.toString() + "\n";
			result += super.toString() + "\n";
			result += " } ";
		} else
			result += ";";
		return result;
	}

}
