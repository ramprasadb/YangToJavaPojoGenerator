package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;

public class YANG_Config extends SimpleYangNode {

	private String config = null;

	public YANG_Config(int id) {
		super(id);
	}

	public YANG_Config(yang p, int id) {
		super(p, id);
	}

	public void setConfig(String c) {
		String ct = unquote(c);
		if (ct.compareTo(YangBuiltInTypes.ytrue) != 0
				&& ct.compareTo(YangBuiltInTypes.yfalse) != 0)
			YangErrorManager.addError(filename, getLine(), getCol(), "config_expr",
					ct);
		config = c;
	}

	public String getConfigStr() {
		return config;
	}

	public void check(YangContext context) {
	}

	public String toString() {
		return "config " + config + ";";
	}
}
