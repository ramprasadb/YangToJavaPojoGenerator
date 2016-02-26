package org.adtran.yang.parser.helpers;



public class YANG_Default extends SimpleYangNode {

	private String defaultstr = null;

	public YANG_Default(int id) {
		super(id);
	}

	public YANG_Default(yang p, int id) {
		super(p, id);
	}

	public void setDefault(String d) {
		defaultstr = unquote(d);
	}

	public String getDefault() {
		return defaultstr;
	}

	public void check(YangContext context, YANG_RefineLeaf rleaf) {

	}

	public void check(YangContext context, YANG_Choice choice){
		choice.checkDefault(context, this);
	}

	public String toString() {
		return "default " + defaultstr + ";";
	}

}
