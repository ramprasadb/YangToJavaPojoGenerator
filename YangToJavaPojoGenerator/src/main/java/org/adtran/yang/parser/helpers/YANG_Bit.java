package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;

public class YANG_Bit extends StatuedNode {

	private String bit = null;
	private YANG_Position position = null;

	private boolean b_position = false;

	public YANG_Bit(int id) {
		super(id);
	}

	public YANG_Bit(yang p, int id) {
		super(p, id);
	}

	public void setBit(String b) {
		bit = unquote(b);
	}

	public String getBit() {
		return bit;
	}

	public void setPosition(YANG_Position p) {
		if (!b_position) {
			position = p;
			b_position = true;
		} else
			YangErrorManager.addError(filename, p.getLine(), p.getCol(), "unex_kw",
					"position");
	}

	public YANG_Position getPosition() {
		return position;
	}

	public boolean isBracked() {
		return b_position || super.isBracked();
	}

	public String toString() {
		String result = "";
		result += "bit " + bit;
		if (isBracked()) {
			result += "{\n";
			if (b_position)
				result += position.toString() + "\n";
			result += super.toString() + "\n";
			result += "}";
		} else
			result += ";";
		return result;
	}
}
