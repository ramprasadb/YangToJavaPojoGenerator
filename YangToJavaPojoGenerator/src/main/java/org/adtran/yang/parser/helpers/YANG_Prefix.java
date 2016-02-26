package org.adtran.yang.parser.helpers;

/*
 * Copyright 2008 Emmanuel Nataf, Olivier Festor
 * 
 * This file is part of jyang.

 jyang is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 jyang is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with jyang.  If not, see <http://www.gnu.org/licenses/>.

 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YANG_Prefix extends SimpleYangNode implements YANG_Header {

	private String prefix = null;

	public YANG_Prefix(int id) {
		super(id);
	}

	public YANG_Prefix(yang p, int id) {
		super(p, id);
	}

	public void setPrefix(String p){
		checkPrefix(p);
	}

	public String getPrefix() {
		return prefix;
	}

	public String toString() {
		return "prefix " + prefix + ";";
	}

	private void checkPrefix(String p){
		String lp = unquote(p);
		lp = lp.trim();
		if (lp.length() > YangBuiltInTypes.idlength)
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"prefix_too_long", lp);
		Pattern pat = Pattern.compile("[a-zA-Z](\\w|\\-)*");
		Matcher m = pat.matcher(lp);
		if (!m.matches())
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"prefix_exp", lp);
		prefix = lp;

	}

}
