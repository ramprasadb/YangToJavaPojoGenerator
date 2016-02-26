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

public class YANG_Grouping extends StatuedBody {

	private String grouping = null;
	private boolean checked = false;

	private Vector<YANG_TypeDef> typedefs = new Vector<YANG_TypeDef>();
	private Vector<YANG_Grouping> groupings = new Vector<YANG_Grouping>();
	private Vector<YANG_DataDef> datadefs = new Vector<YANG_DataDef>();

	private boolean bracked = false, used = false;

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public YANG_Grouping(int id) {
		super(id);
	}

	public YANG_Grouping(yang p, int id) {
		super(p, id);
	}

	public void setGrouping(String g) {
		grouping = unquote(g);
	}

	public String getBody() {
		return getGrouping();
	}

	public String getGrouping() {
		return grouping;
	}

	public boolean isBracked() {
		return super.isBracked() || bracked;
	}

	public void addTypeDef(YANG_TypeDef t) {
		typedefs.add(t);
		bracked = true;
	}

	public Vector<YANG_TypeDef> getTypeDefs() {
		return typedefs;
	}

	public void addGrouping(YANG_Grouping g) {
		groupings.add(g);
		bracked = true;
	}

	public Vector<YANG_Grouping> getGroupings() {
		return groupings;
	}

	public void addDataDef(YANG_DataDef d) {
		datadefs.add(d);
		bracked = true;
	}

	public Vector<YANG_DataDef> getDataDefs() {
		return datadefs;
	}

	public void check(YangContext context)  {}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String toString() {
		String result = new String();
		result += "grouping " + grouping;
		if (isBracked()) {
			result += " {\n";
			result += super.toString();
			for (Enumeration<YANG_TypeDef> et = typedefs.elements(); et
					.hasMoreElements();)
				result += et.nextElement().toString() + "\n";
			for (Enumeration<YANG_Grouping> eg = groupings.elements(); eg
					.hasMoreElements();)
				result += eg.nextElement().toString() + "\n";
			for (Enumeration<YANG_DataDef> ed = datadefs.elements(); ed
					.hasMoreElements();)
				result += ed.nextElement().toString() + "\n";
			result += "}";
		} else
			result += ";";

		return result;
	}

	public YANG_Grouping clone() {
		YANG_Grouping cg = new YANG_Grouping(parser, id);
		cg.setTypeDefs(getTypeDefs());
		cg.setGroupings(getGroupings());
		cg.setDataDefs(getDataDefs());
		cg.setFileName(filename);
		return cg;
	}

	private void setDataDefs(Vector<YANG_DataDef> dataDefs2) {
		datadefs = dataDefs2;

	}

	private void setGroupings(Vector<YANG_Grouping> groupings2) {
		groupings = groupings2;

	}

	private void setTypeDefs(Vector<YANG_TypeDef> typeDefs2) {
		typedefs = typeDefs2;

	}
}
