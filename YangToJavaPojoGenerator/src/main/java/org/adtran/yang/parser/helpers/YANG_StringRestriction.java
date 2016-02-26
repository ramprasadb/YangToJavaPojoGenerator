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
import java.util.Enumeration;
import java.util.Vector;

public class YANG_StringRestriction extends SimpleYangNode {

	private YANG_Length length = null;
	private Vector<YANG_Pattern> patterns = new Vector<YANG_Pattern>();

	private boolean b_length = false;

	public YANG_StringRestriction(int id) {
		super(id);
	}

	public YANG_StringRestriction(yang p, int id) {
		super(p, id);
	}

	public void setLength(YANG_Length l) {
		if (!b_length) {
			b_length = true;
			length = l;
		} else
			YangErrorManager.addError(filename, l.getLine(), l.getCol(), "length");
	}

	public YANG_Length getLength() {
		return length;
	}

	public void addPattern(YANG_Pattern p) {
		patterns.add(p);
	}

	public Vector<YANG_Pattern> getPatterns() {
		return patterns;
	}

	public String toString() {
		String result = "";
		if (b_length)
			result += length.toString() + "\n";
		for (Enumeration<YANG_Pattern> ep = patterns.elements(); ep
				.hasMoreElements();)
			result += ep.nextElement().toString() + "\n";
		return result;
	}
}
