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

public class YANG_UnionSpecification extends SimpleYangNode {

	private Vector<YANG_Type> types = new Vector<YANG_Type>();

	public YANG_UnionSpecification(int id) {
		super(id);
	}

	public YANG_UnionSpecification(yang p, int id) {
		super(p, id);
	}

	public void addType(YANG_Type t) {
		types.add(t);
	}

	public Vector<YANG_Type> getTypes() {
		return types;
	}

	public String toString() {
		String result = "";
		for (Enumeration<YANG_Type> et = types.elements(); et.hasMoreElements();)
			result += et.nextElement().toString() + "\n";
		return result;
	}
}
