package org.adtran.yang.parser.helpers;

public class YANG_Must extends ErrorTagedNode {

	private String must = null;

	public YANG_Must(int id) {
		super(id);
	}

	public YANG_Must(yang p, int id) {
		super(p, id);
	}

	public void setMust(String m) {
		must = unquote(m);
	}

	public String getMust() {
		return must;
	}

	public boolean isBracked() {
		return super.isBracked();
	}

	public void check(YangContext context) {
	}

	public String toString() {
		String result = "";
		result += "must " + must;
		if (isBracked()) {
			result += "{\n";
			result += super.toString();
			result += "}";
		} else
			result += ";";
		return result;
	}

}
