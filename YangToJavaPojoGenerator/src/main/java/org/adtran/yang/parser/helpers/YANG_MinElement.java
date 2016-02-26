package org.adtran.yang.parser.helpers;

public class YANG_MinElement extends SimpleYangNode {

	private String min = null;

	public YANG_MinElement(int id) {
		super(id);
	}

	public YANG_MinElement(yang p, int id) {
		super(p, id);
	}

	public void setMinElement(String m) {
		min = unquote(m);
	}

	public String getMinElement() {
		return min;
	}
	
	public int getMinElementInt(){
		return Integer.parseInt(min);
	}

	public void check(YangContext context) {
	}

	public String toString() {
		return "min-elements " + min + ";";
	}

}
