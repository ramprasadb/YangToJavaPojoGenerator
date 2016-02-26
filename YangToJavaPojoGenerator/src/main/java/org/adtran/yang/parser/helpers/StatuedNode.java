package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;
import java.util.Vector;

public abstract class StatuedNode extends DocumentedNode {

	private YANG_Status status = null;
	private boolean b_status = false;

	public StatuedNode(int i) {
		super(i);
	}

	public StatuedNode(yang p, int id) {
		super(p, id);
	}

	public void setStatus(YANG_Status s) {
		if (!b_status) {
			b_status = true;
			status = s;
		} else
			YangErrorManager.addError(filename, s.getLine(), s.getCol(), "unex_kw",
					"status");
	}

	public YANG_Status getStatus() {
		return status;
	}


	public String toString() {
		String result = "";
		if (b_status)
			result += "status " + status + ";";
		result += super.toString() + "\n";
		return result;
	}

}
