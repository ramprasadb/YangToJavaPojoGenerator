package org.adtran.yang.parser.helpers;

public class YANG_Presence extends SimpleYangNode {

	private String presence = null;

	public YANG_Presence(int id) {
		super(id);
	}

	public YANG_Presence(yang p, int id) {
		super(p, id);
	}

	public void setPresence(String p) {
		presence = unquote(p);
	}

	public String getPresence() {
		return presence;
	}

	public void check(YangContext context) {
	}

	public String toString() {
		return "presence " + presence + ";";
	}

}
