package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;

public abstract class DeviateAddReplace extends Deviate {

	private YANG_Config config = null;
	private YANG_Mandatory mandatory = null;
	private YANG_MinElement min = null;
	private YANG_MaxElement max = null;

	private boolean b_config = false, b_mandatory = false, b_min = false,
			b_max = false;

	public DeviateAddReplace(int i) {
		super(i);
	}

	public DeviateAddReplace(yang p, int i) {
		super(p, i);
	}

	public YANG_Config getConfig() {
		return config;
	}

	public void setConfig(YANG_Config c) {
		if (!b_config) {
			this.config = c;
			b_config = true;
		} else
			YangErrorManager.addError(filename, c.getLine(), c.getCol(), "unex_kw",
					"config");
	}

	public YANG_Mandatory getMandatory() {
		return mandatory;
	}

	public void setMandatory(YANG_Mandatory m) {
		if (!b_mandatory) {
			this.mandatory = m;
			b_mandatory = true;
		} else
			YangErrorManager.addError(filename, m.getLine(), m.getCol(), "unex_kw",
					"mandatory");
	}

	public YANG_MinElement getMinElement() {
		return min;
	}

	public void setMinElement(YANG_MinElement m) {
		if (!b_min) {
			this.min = m;
			b_min = true;
		} else
			YangErrorManager.addError(filename, m.getLine(), m.getCol(), "unex_kw",
					"min");
	}

	public YANG_MaxElement getMaxElement() {
		return max;
	}

	public void setMaxElement(YANG_MaxElement m) {
		if (!b_max) {
			this.max = m;
			b_max = true;
		} else
			YangErrorManager.addError(filename, m.getLine(), m.getCol(), "unex_kw",
					"max");
	}

	public boolean isBracked() {
		return b_config || b_mandatory || b_min || b_max || super.isBracked();
	}

	public String toString() {
		String result = "";
		if (b_config)
			result += config.toString() + "\n";
		if (b_mandatory)
			result += mandatory.toString() + "\n";
		if (b_min)
			result += min.toString() + "\n";
		if (b_max)
			result += max.toString() + "\n";
		result += super.toString();
		return result;
	}

}
