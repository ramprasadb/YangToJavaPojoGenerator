package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;

public abstract class ListedRefineNode extends MustRefineNode {

	protected YANG_MinElement min = null;
	protected YANG_MaxElement max = null;
	private boolean b_min = false, b_max = false;

	public ListedRefineNode(int id) {
		super(id);
	}

	public ListedRefineNode(yang p, int id) {
		super(p, id);
	}

	public void setMinElement(YANG_MinElement m) {
		if (b_min)
			YangErrorManager.addError(filename, m.getLine(), m.getCol(), "unex_kw",
					"min");
		else {
			b_min = true;
			min = m;
		}
	}

	public YANG_MinElement getMinElement() {
		return min;
	}

	public void setMaxElement(YANG_MaxElement m) {
		if (b_max)
			YangErrorManager.addError(filename, m.getLine(), m.getCol(), "unex_kw",
					"max");
		else {
			b_max = true;
			max = m;
		}
	}

	public YANG_MaxElement getMaxElement() {
		return max;
	}

	public String toString() {
		String result = "";
		if (b_min)
			result += min.toString() + "\n";
		if (b_max)
			result += max.toString() + "\n";
		result += super.toString() + "\n";
		return result;
	}

}
