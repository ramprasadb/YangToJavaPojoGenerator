package org.adtran.yang.parser.helpers;


public class YANG_Base extends SimpleYangNode {

	private String base = null;

	public YANG_Base(int id) {
		super(id);
	}

	public YANG_Base(yang p, int id) {
		super(p, id);
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = unquote(base);
	}
	
	public boolean isPrefixed() {
		return base.indexOf(':') != -1;
	}

	public String getPrefix() {
		if (isPrefixed())
			return base.substring(0, base.indexOf(':'));
		return "";
	}

	public String getSuffix() {
		if (isPrefixed())
			return base.substring(base.indexOf(':')+1);
		return base;
	}
	
	public String toString() {
		return "base " + base;
	}
}