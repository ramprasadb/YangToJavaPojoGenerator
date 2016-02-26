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



public class YANG_Notification extends TypedefGroupingBody implements DataDefsContainer {

	private String notification = null;
	private Vector<YANG_DataDef> datadefs = new Vector<YANG_DataDef>();


	public YANG_Notification(int id) {
		super(id);
	}

	public YANG_Notification(yang p, int id) {
		super(p, id);
	}

	public void setNotification(String n) {
		notification = unquote(n);
	}

	public String getBody() {
		return getNotification();
	}

	public String getNotification() {
		return notification;
	}



	public void addDataDef(YANG_DataDef d) {
		datadefs.add(d);
	}

	public Vector<YANG_DataDef> getDataDefs() {
		return datadefs;
	}

	public boolean isBracked() {
		return super.isBracked() ||  datadefs.size() != 0;
	}

	public void check(YangContext context)  {
		super.check(context);
	}

	public String toString() {
		String result = new String();
		result += "notification " + notification;
		if (isBracked()) {
			result += "{\n";
			result += super.toString();
			for (YANG_DataDef ed : datadefs)
				result += ed.toString() + "\n";
			result += "}";
		} else
			result += ";";
		return result;
	}

}
