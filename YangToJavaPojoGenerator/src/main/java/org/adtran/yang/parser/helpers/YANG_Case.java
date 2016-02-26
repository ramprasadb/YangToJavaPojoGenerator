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

import java.util.*;


public class YANG_Case extends FeaturedNode {

	private String ycase = null;



	public YANG_Case(int id) {
		super(id);
	}

	public YANG_Case(yang p, int id) {
		super(p, id);
	}

	public void setCase(String c) {
		ycase = unquote(c);
	}

	public String getCase() {
		return ycase;
	}

	public String getBody() {
		return getCase();
	}

	public boolean isBracked() {
		return super.isBracked();
	}

	public String toString() {
		String result = new String();
		result += "case " + ycase;
		if (isBracked()) {
			result += " {\n";
			result += super.toString() + "\n";
			result += "}";
		} else
			result += ";";
		return result;
	}


	public void refines(YANG_RefineCase rl) {
		if (rl.getDescription() != null)
			description = rl.getDescription();
		if (rl.getReference() != null)
			reference = rl.getReference();
	}

}
