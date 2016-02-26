package org.adtran.yang.parser.helpers;

public class YANG_NameSpace extends SimpleYangNode implements YANG_Header {

	private String namespace = null;

	public YANG_NameSpace(int id) {
		super(id);
	}

	public YANG_NameSpace(yang p, int id) {
		super(p, id);
	}

	public void setNameSpace(String n) {
		namespace = unquote(n);
	}

	public String getNameSpace() {
		return namespace;
	}

	public String toString() {
		return "namespace " + namespace + ";";
	}

}
