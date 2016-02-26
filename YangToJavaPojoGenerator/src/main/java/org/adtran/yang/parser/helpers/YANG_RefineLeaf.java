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

import java.text.MessageFormat;
import java.util.*;

public class YANG_RefineLeaf extends MustRefineNode {

	private YANG_Default ydefault = null;
	private YANG_Mandatory mandatory = null;

	private boolean b_default = false, b_mandatory = false;

	public YANG_RefineLeaf(int id) {
		super(id);
	}

	public YANG_RefineLeaf(yang p, int id) {
		super(p, id);
	}

	public void setDefault(YANG_Default d) {
		if (!b_default) {
			b_default = true;
			ydefault = d;
		} else
			YangErrorManager.addError(filename, d.getLine(), d.getCol(), "unex_kw",
					"default");
	}

	public YANG_Default getDefault() {
		return ydefault;
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
	
	public void check(YANG_Leaf leaf) {
		YangContext context = leaf.getContext();
		if (getDefault() != null)
			leaf.getType().checkDefaultValue(context, this, getDefault());

	}


	public String toString() {
		String result = "";
		if (b_default)
			result += ydefault.toString() + "\n";
		if (b_mandatory)
			result += mandatory.toString() + "\n";
		result += super.toString() + "\n";
		return result;
	}

}
