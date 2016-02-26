package org.adtran.yang.parser.helpers;

public class YANG_Mandatory extends SimpleYangNode {

	private String mandatory = null;

	public YANG_Mandatory(int id) {
		super(id);
	}

	public YANG_Mandatory(yang p, int id) {
		super(p, id);
	}

	public void setMandatory(String m) {
		if (m.compareTo(YangBuiltInTypes.ytrue) == 0
				|| m.compareTo(YangBuiltInTypes.yfalse) == 0)
			mandatory = unquote(m);
		else {
			YangErrorManager.addError(getFileName(), getLine(), getCol(), "mand_exp", m);
			mandatory = m;
		}
	}

	public String getMandatory() {
		return mandatory;
	}

	public String toString() {
		return "mandatory " + mandatory + ";";
	}

}
