package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;

public abstract class DocumentedNode extends SimpleYangNode {

	public DocumentedNode(int id) {
		super(id);
	}

	public DocumentedNode(yang p, int id) {
		super(p, id);
	}

	protected YANG_Description description = null;
	protected YANG_Reference reference = null;

	private boolean b_description = false, b_reference = false;

	public boolean isBracked() {
		return b_description || b_reference;
	}

	public void setDescription(YANG_Description d) {
		if (!b_description) {
			b_description = true;
			description = d;
		} else
			YangErrorManager.addError(filename, d.getLine(), d.getCol(), "unex_kw",
					"description");
	}

	public YANG_Description getDescription() {
		return description;
	}

	public void setReference(YANG_Reference r) {
		if (!b_reference) {
			b_reference = true;
			reference = r;
		} else
			YangErrorManager.addError(filename, r.getLine(), r.getCol(), "unex_kw",
					"reference");
	}

	public YANG_Reference getReference() {
		return reference;
	}

	public String toString() {
		String result = "";
		if (b_description)
			result +=  getDescription() + "\n";
		if (b_reference)
			result += getReference() + "\n";
		return result;
	}

}
