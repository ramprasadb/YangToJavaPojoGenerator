package org.adtran.yang.parser.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YANG_Revision extends DocumentedNode {

	private String date = null;

	public YANG_Revision(int id) {
		super(id);
	}

	public YANG_Revision(yang p, int id) {
		super(p, id);
	}

	public void setDate(String d) {
		date = unquote(d);
	}

	public String getDate() {
		return date;
	}

	public String toString() {
		String result = new String();
		result += "revision " + date;
		result += super.toString();
		return result;
	}

	public void check() {
		Pattern p = Pattern
				.compile("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]");
		Matcher m = p.matcher(date);
		if (!m.matches())
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"date_exp", date);
		String[] dates = date.split("-");
		try {
			if (Integer.parseInt(dates[0]) < 1800
					|| Integer.parseInt(dates[1]) > 12
					|| Integer.parseInt(dates[2]) > 31)
				YangErrorManager.addError(getFileName(), getLine(), getCol(),
						"date_exp", date);
		} catch (NumberFormatException n) {
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"date_exp", date);
		}

	}
}
