package org.adtran.yang.parser.helpers;

public class YANG_When extends DocumentedNode {

	private String when = null;

	public YANG_When(int id) {
		super(id);
	}

	public YANG_When(yang p, int id) {
		super(p, id);
	}

	public void setWhen(String w) {
		when = unquote(w);
	}

	public String getWhen() {
		return when;
	}

	public void check(YangContext context) {
	}

	public String toString() {
		return "when " + when + ";";
	}

}
