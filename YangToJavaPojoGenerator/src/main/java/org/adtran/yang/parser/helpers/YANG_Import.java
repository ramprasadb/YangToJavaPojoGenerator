package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;

public class YANG_Import extends ImportIncludeNode implements YANG_Linkage {

	private String importstr = null;
	private YANG_Prefix prefix = null;
	
	private YANG_Specification importedmodule = null;

	private boolean b_prefix = false;
	
	private boolean used = false;

	public YANG_Specification getImportedmodule() {
		return importedmodule;
	}

	public void setImportedmodule(YANG_Specification importedmodule) {
		this.importedmodule = importedmodule;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public YANG_Import(int id) {
		super(id);
	}

	public YANG_Import(yang p, int id) {
		super(p, id);
	}

	public void setIdentifier(String s) {
		importstr = unquote(s);
	}

	public String getImportedModule() {
		return importstr;
	}

	public void setPrefix(YANG_Prefix p) {
		if (!b_prefix) {
			prefix = p;
			b_prefix = true;
		} else
			YangErrorManager.addError(filename, p.getLine(), p.getCol(), "unex_kw",
					"prefix");
	}

	public YANG_Prefix getPrefix() {
		return prefix;
	}
	
	public String getName(){
		return getImportedModule();
	}

	public String toString() {
		String result = " import " + importstr + " {" + prefix.toString();
		result += super.toString() + "\n";
		result += "}";
		return result;

	}

}
