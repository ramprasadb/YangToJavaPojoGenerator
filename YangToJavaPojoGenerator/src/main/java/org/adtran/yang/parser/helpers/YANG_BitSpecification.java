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

public class YANG_BitSpecification extends SimpleYangNode {

	private Vector<YANG_Bit> bits = new Vector<YANG_Bit>();

	public YANG_BitSpecification(int id) {
		super(id);
	}

	public YANG_BitSpecification(yang p, int id) {
		super(p, id);
	}

	public void addBit(YANG_Bit b) {
		bits.add(b);
	}

	public Vector<YANG_Bit> getBits() {
		return bits;
	}

	public String toString() {
		String result = "";
		for (Enumeration<YANG_Bit> eb = bits.elements(); eb.hasMoreElements();)
			result += eb.nextElement().toString() + "\n";
		return result;
	}

}
