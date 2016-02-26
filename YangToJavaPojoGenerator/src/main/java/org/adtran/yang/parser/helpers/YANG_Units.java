package org.adtran.yang.parser.helpers;

public class YANG_Units extends SimpleYangNode {

	private String units = null;

	public YANG_Units(int id) {
		super(id);
	}

	public YANG_Units(yang p, int id) {
		super(p, id);
	}

	public void setUnits(String u) {
		units = unquote(u);
	}

	public String getUnits() {
		return units;
	}

	public String toString() {
		return "units " + units + ";";
	}

}
