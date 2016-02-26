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

public class YANG_RefineContainer extends MustRefineNode {

	private YANG_Presence presence = null;

	private boolean b_presence = false;

	public YANG_RefineContainer(int id) {
		super(id);
	}

	public YANG_RefineContainer(yang p, int id) {
		super(p, id);
	}

	public void setPresence(YANG_Presence p) {
		if (!b_presence) {
			b_presence = true;
			presence = p;
		} else
			YangErrorManager.addError(filename, p.getLine(), p.getCol(), "unex_kw",
					"presence");
	}

	public YANG_Presence getPresence() {
		return presence;
	}

	public void check(YANG_Container container){
		
	}
	

	public String toString() {
		String result = "";
		if (presence != null)
			result += presence.toString() + "\n";
		result += super.toString() + "\n";
		return result;
	}

}
