package org.adtran.yang.parser.helpers;

public class YANG_Reference extends SimpleYangNode implements YANG_Meta {

	private String reference = null;

	public YANG_Reference(int id) {
		super(id);
	}

	public YANG_Reference(yang p, int id) {
		super(p, id);
	}

	public void setReference(String r) {
		reference = unquote(r);
	}

	public String getReference() {
		return reference;
	}

	public void check(YangContext context) {
	}

	public String toString() {
		return "reference " + reference + ";";
	}

}
