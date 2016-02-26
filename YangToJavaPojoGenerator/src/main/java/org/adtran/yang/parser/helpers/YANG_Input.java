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

public class YANG_Input extends Io {

	public YANG_Input(int id) {
		super(id);
	}

	public YANG_Input(yang p, int id) {
		super(p, id);
	}

	public void check(YangContext context) {
		if (datadefs.size() == 0)
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"empty_input");
		/*
		 * for (Enumeration<YANG_TypeDef> et = typedefs.elements(); et
		 * .hasMoreElements();) context.addNode(et.nextElement()); for
		 * (Enumeration<YANG_Grouping> eg = groupings.elements(); eg
		 * .hasMoreElements();) context.addNode(eg.nextElement()); for
		 * (Enumeration<YANG_DataDef> ed = datadefs.elements(); ed
		 * .hasMoreElements();) context.addNode(ed.nextElement());
		 * 
		 * for (Enumeration<YANG_TypeDef> et = typedefs.elements(); et
		 * .hasMoreElements();) { YangContext cl = context.clone();
		 * et.nextElement().check(cl); } for (Enumeration<YANG_Grouping> eg =
		 * groupings.elements(); eg .hasMoreElements();) { YangContext cl =
		 * context.clone(); eg.nextElement().checkBody(cl); } for
		 * (Enumeration<YANG_DataDef> ed = datadefs.elements(); ed
		 * .hasMoreElements();) { YangContext cl = context.clone();
		 * ed.nextElement().checkBody(cl); }
		 */

	}

	public String toString() {
		String result = "";
		result += "input {\n";
		result += super.toString();
		result += "}";
		return result;
	}

}
