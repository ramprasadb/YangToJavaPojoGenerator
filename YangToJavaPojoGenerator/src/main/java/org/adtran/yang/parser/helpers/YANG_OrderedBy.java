package org.adtran.yang.parser.helpers;

public class YANG_OrderedBy extends SimpleYangNode {

	private String orderedby = null;

	public YANG_OrderedBy(int id) {
		super(id);
	}

	public YANG_OrderedBy(yang p, int id) {
		super(p, id);
	}

	public void setOrderedBy(String o) {

		String ot = unquote(o);
		if (ot.compareTo("system") != 0 && ot.compareTo("user") != 0)
			YangErrorManager.addError(filename, getLine(), getCol(), "ordered_exp");
		orderedby = ot;
	}

	public String getOrderedBy() {
		return orderedby;
	}

	public String toString() {
		return "ordered-by " + orderedby + ";";
	}

}
