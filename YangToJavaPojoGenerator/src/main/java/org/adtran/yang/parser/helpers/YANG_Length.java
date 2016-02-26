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
import java.util.regex.PatternSyntaxException;

public class YANG_Length extends ErrorTagedNode {

	private String length = null;
	private String[][] lengths = null;

	public YANG_Length(int id) {
		super(id);
	}

	public YANG_Length(yang p, int id) {
		super(p, id);
	}

	public void setLength(String l) {
		length = unquote(l);
		setLengths();
	}

	public String getLength() {
		return length;
	}

	private void setLengths() {

		String[] test = null;

		if (length.indexOf('|') == -1) {
			test = new String[1];
			test[0] = length;
		}
		try {
			test = length.split("\\|");
		} catch (PatternSyntaxException pe) {
			// Cannot occurs
		}

		lengths = new String[test.length][2];
		for (int j = 0; j < test.length; j++) {
			try {
				if (test[j].indexOf("..") == -1) {
					lengths[j][0] = test[j];
					lengths[j][1] = test[j];
				} else {
					lengths[j] = test[j].split("\\.\\.");
					if (lengths[j].length != 2) {

						YangErrorManager.addError(filename, getLine(), getCol(),
								"length_exp");
					}
				}
			} catch (PatternSyntaxException pe) {
				// Cannot occurs
			}
		}
		for (int i = 0; i < lengths.length; i++) {
			lengths[i][0] = lengths[i][0].trim();
			lengths[i][1] = lengths[i][1].trim();
		}
	}

	public String[][] getLengthIntervals() {
		return lengths;
	}

	public boolean isBracked() {
		return super.isBracked();
	}

	public String toString() {
		String result = "";
		result += "length " + length;
		if (isBracked()) {
			result += "{\n";
			result += super.toString();
			result += "}";
		} else
			result += ";";
		return result;
	}

}
