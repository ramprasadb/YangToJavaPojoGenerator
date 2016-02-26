package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;

public abstract class ListedDataDef extends MustDataDef implements
		YANG_ShortCase {

	protected YANG_MinElement min = null;
	protected YANG_MaxElement max = null;
	protected YANG_OrderedBy ordered = null;
	private boolean b_min = false, b_max = false, b_ordered = false;

	public ListedDataDef(int id) {
		super(id);
	}

	public ListedDataDef(yang p, int id) {
		super(p, id);
	}

	public void setMinElement(YANG_MinElement m) {
		if (b_min)
			YangErrorManager.addError(filename, m.getLine(), m.getCol(), "unex_kw",
					"min");
		b_min = true;
		min = m;
	}

	public YANG_MinElement getMinElement() {
		return min;
	}

	public void setMaxElement(YANG_MaxElement m) {
		if (!b_max) {
			b_max = true;
			max = m;
		} else
			YangErrorManager.addError(filename, m.getLine(), m.getCol(), "unex_kw",
					"max");
	}

	public YANG_MaxElement getMaxElement() {
		return max;
	}

	public void setOrderedBy(YANG_OrderedBy o) {
		if (!b_ordered) {
			b_ordered = true;
			ordered = o;
		} else
			YangErrorManager.addError(filename, o.getLine(), o.getCol(), "unex_kw",
					"ordered");
	}

	public YANG_OrderedBy getOrderedBy() {
		return ordered;
	}

	public boolean isBracked() {
		return super.isBracked() || b_ordered || b_min || b_max;
	}

	public String toString() {
		String result = "";
		result += super.toString() + "\n";
		if (b_ordered)
			result += ordered.toString() + "\n";
		if (b_min)
			result += min.toString() + "\n";
		if (b_max)
			result += max.toString() + "\n";
		return result;
	}

	public void deleteMinElement() {
		min = null;
		b_min = false;

	}

	public void deleteMaxElement() {
		max = null;
		b_max = false;

	}

}
