package org.adtran.yang.parser.helpers;

public class YANG_Yin extends SimpleYangNode {

	private String yin = null;

	public YANG_Yin(int id) {
		super(id);
	}

	public YANG_Yin(yang p, int id) {
		super(p, id);
	}

	public String getYin() {
		return yin;
	}

	public void setYin(String y) {
		yin = unquote(y);
		if (!(yin.compareTo("true") == 0 || yin.compareTo("false") == 0))

			YangErrorManager.addError(filename, getLine(), getCol(), "yin_exp");
		yin = y;
	}

	public String toString() {
		return "yin-element " + yin + ";";
	}
}
