package org.adtran.yang.parser.helpers;

public class YANG_Description extends SimpleYangNode implements YANG_Meta {

	private String description = null;

	public YANG_Description(int id) {
		super(id);
	}

	public YANG_Description(yang p, int id) {
		super(p, id);
	}

	public void setDescription(String d) {
		description = unquote(d);
	}

	public String getDescription() {
		return description;
	}

	public void check(YangContext context) {
	}

	public String toString() {
		return "description\n\t " + "\"" + description + "\"" + ";";
	}

}
