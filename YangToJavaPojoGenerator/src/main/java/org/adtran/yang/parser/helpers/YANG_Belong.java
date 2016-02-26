package org.adtran.yang.parser.helpers;

public class YANG_Belong extends SimpleYangNode implements YANG_Header {

	private String belong = null;
	private YANG_Prefix prefix = null;

	public YANG_Belong(int id) {
		super(id);
	}

	public YANG_Belong(yang p, int id) {
		super(p, id);
	}

	public void setBelong(String b) {
		belong = unquote(b);
	}

	public void setPrefix(YANG_Prefix p) {
		prefix = p;
	}

	public String getBelong() {
		return belong;
	}

	public YANG_Prefix getPrefix() {
		return prefix;
	}

	public String toString() {
		return "belongs-to " + belong + "\n{ " + prefix + " }";
	}

}
