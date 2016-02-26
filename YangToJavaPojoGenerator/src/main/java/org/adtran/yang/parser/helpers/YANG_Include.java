package org.adtran.yang.parser.helpers;

public class YANG_Include extends ImportIncludeNode implements YANG_Linkage {

	private String include = null;

	private YANG_Specification includedsubmodule = null;

	public YANG_Include(int id) {
		super(id);
	}

	public YANG_Specification getIncludedsubmodule() {
		return includedsubmodule;
	}

	public void setIncludedsubmodule(YANG_Specification includedsubmodule) {
		this.includedsubmodule = includedsubmodule;
	}

	public YANG_Include(yang p, int id) {
		super(p, id);
	}

	public void setIdentifier(String s) {
		include = unquote(s);
	}

	public String getIncludedModule() {
		return include;
	}
	
	public String getName(){
		return getIncludedModule();
	}
	
	public boolean isBracked() {
		return super.isBracked();
	}

	public String toString() {
		String result = "";
		result += " include " + include;
		if (isBracked())
			result += "{\n" + super.toString() + "\n}";
		else
			result += ";";
		return result;
	}

}
