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

public class YANG_Range extends ErrorTagedNode implements
		YANG_NumericalRestriction {

	private String range = null;

	private String[][] ranges = null;

	public YANG_Range(int id) {
		super(id);
	}

	public YANG_Range(yang p, int id) {
		super(p, id);
	}

	public void setRange(String r) {
		range = unquote(r);
		setRanges();
	}

	public String getRange() {
		return range;
	}

	public boolean isBracked() {
		return super.isBracked();
	}

	public String toString() {
		String result = new String();
		result += "range " + range;
		if (isBracked()) {
			result += "{\n";
			result += super.toString();
			result += "}";
		} else
			result += ";";
		return result;
	}

	private void setRanges() {

		String[] test = null;
		try {
			test = range.split("\\|");
		} catch (PatternSyntaxException pe) {
			// Cannot occurs
		}

		ranges = new String[test.length][2];

		for (int j = 0; j < test.length; j++) {
			if (test[j].indexOf("..") == -1) {
				ranges[j][0] = test[j];
				ranges[j][1] = test[j];
			} else {
				try {
					ranges[j] = test[j].split("\\.\\.");
					if (ranges[j].length != 2) {
						YangErrorManager.addError(filename, getLine(), getCol(),
								"range_exp");
					}
				} catch (PatternSyntaxException pe) {
					// Cannot occurs
				}
			}
		}
		for (int i = 0; i < ranges.length; i++) {
			String bi = ranges[i][0].trim();
			if (bi.startsWith("+", 0)){
				bi = bi.substring(1);
				bi = bi.trim();
			}
			ranges[i][0] = bi;
			String bs = ranges[i][1].trim();
			if (bs.startsWith("+", 0)){
				bs = bs.substring(1);
				bs = bs.trim();
			}
			ranges[i][1] = bs;
		}

	}

	public String[][] getRangeIntervals() {
		return ranges;
	}

}
