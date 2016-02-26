package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;

public abstract class ConfigRefineNode extends YANG_Refine {

	protected YANG_Config config = null;

	protected boolean b_config = false;

	public ConfigRefineNode(int id) {
		super(id);
	}

	public ConfigRefineNode(yang p, int id) {
		super(p, id);
	}

	public void setConfig(YANG_Config c) {
		if (!b_config) {
			b_config = true;
			config = c;
		} else
			YangErrorManager.addError(filename, c.getLine(), c.getCol(), "unex_kw",
					"config");
	}

	public YANG_Config getConfig() {
		return config;
	}

	public String toString() {
		String result = "";
		if (b_config)
			result += getConfig().toString();
		result += super.toString();
		return result;
	}

}
