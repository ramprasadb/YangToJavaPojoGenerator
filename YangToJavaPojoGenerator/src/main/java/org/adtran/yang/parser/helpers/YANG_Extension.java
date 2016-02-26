package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;

public class YANG_Extension extends StatuedBody {

	private String extension = null;
	private YANG_Argument argument = null;

	private boolean b_argument = false;

	public YANG_Extension(int id) {
		super(id);
	}

	public YANG_Extension(yang p, int id) {
		super(p, id);
	}

	public void setExtension(String e) {
		extension = unquote(e);
	}

	public String getBody() {
		return getExtension();
	}

	public String getExtension() {
		return extension;
	}

	public void setArgument(YANG_Argument a) {
		if (!b_argument) {
			b_argument = true;
			argument = a;
		} else
			YangErrorManager.addError(filename, a.getLine(), a.getCol(), "unex_kw",
					"argument");

	}

	public YANG_Argument getArgument() {
		return argument;
	}

	public boolean isBracked() {
		return b_argument || super.isBracked();
	}

	public void check(YangContext context) {
	}

	public String toString() {
		String result = new String();
		result += "extension " + extension;
		if (isBracked()) {
			result += "{\n";
			super.toString();
			result += "}";
		} else
			result += ";";
		return result;
	}

}
