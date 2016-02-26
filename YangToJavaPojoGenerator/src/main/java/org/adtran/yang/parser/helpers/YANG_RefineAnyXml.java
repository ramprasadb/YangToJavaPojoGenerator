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

public class YANG_RefineAnyXml extends MustRefineNode {

	private YANG_Mandatory mandatory = null;

	protected boolean b_mandatory = false;

	public YANG_RefineAnyXml(int id) {
		super(id);
	}

	public YANG_RefineAnyXml(yang p, int id) {
		super(p, id);
	}

	public void setMandatory(YANG_Mandatory m) {
		if (!b_mandatory) {
			b_mandatory = true;
			mandatory = m;
		} else
			YangErrorManager.addError(filename, m.getLine(), m.getCol(), "unex_kw",
					"mandatory");
	}

	public YANG_Mandatory getMandatory() {
		return mandatory;
	}
	public void check(YANG_AnyXml ax) {
	}


	public String toString() {
		String result = "";
		result += super.toString() + "\n";
		return result;
	}

}
