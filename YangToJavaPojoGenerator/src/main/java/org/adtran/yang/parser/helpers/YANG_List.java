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

public class YANG_List extends ListedDataDef implements DataDefsContainer {

	private String list = null;
	private YANG_Key key = null;
	private Vector<YANG_Unique> uniques = new Vector<YANG_Unique>();
	private Vector<YANG_TypeDef> typedefs = new Vector<YANG_TypeDef>();
	private Vector<YANG_Grouping> groupings = new Vector<YANG_Grouping>();
	private Vector<YANG_DataDef> datadefs = new Vector<YANG_DataDef>();

	private boolean b_key = false;

	public YANG_List(int id) {
		super(id);
	}

	public YANG_List(yang p, int id) {
		super(p, id);
	}

	public void setList(String l) {
		list = unquote(l);
	}

	public String getBody() {
		return getList();
	}

	public String getList() {
		return list;
	}

	public void setKey(YANG_Key t) {
		if (!b_key) {
			b_key = true;
			key = t;
		} else
			YangErrorManager.addError(filename, t.getLine(), t.getCol(), "unex_kw",
					"key");
	}

	public YANG_Key getKey() {
		return key;
	}

	public void addUnique(YANG_Unique u) {
		uniques.add(u);
	}

	public Vector<YANG_Unique> getUniques() {
		return uniques;
	}

	public void addTypeDef(YANG_TypeDef t) {
		typedefs.add(t);
	}

	public Vector<YANG_TypeDef> getTypeDefs() {
		return typedefs;
	}

	public void addGrouping(YANG_Grouping g) {
		groupings.add(g);
	}

	public Vector<YANG_Grouping> getGroupings() {
		return groupings;
	}

	public void addDataDef(YANG_DataDef d) {
		datadefs.add(d);
	}

	public Vector<YANG_DataDef> getDataDefs() {
		return datadefs;
	}

	public boolean isBracked() {
		return super.isBracked() || b_key || datadefs.size() != 0
				|| groupings.size() != 0;
	}

	public void check(YangContext context) {
		super.check(context);
		if (datadefs.size() == 0)
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"expected", "data definition");

		setContext(context);
	}

	private boolean findKey(YangContext context, String k, YANG_DataDef dd) {

		if (dd.getBody().compareTo(k) == 0)
			return true;
		if (dd instanceof YANG_Uses) {
			YANG_Uses uses = (YANG_Uses) dd;
			YANG_Grouping grouping = context.getUsedGrouping(uses);
			for (Enumeration<YANG_DataDef> edd = grouping.getDataDefs()
					.elements(); edd.hasMoreElements();) {
				YANG_DataDef gdd = edd.nextElement();
				if (findKey(context, k, gdd))
					return true;
			}
			return false;
		}
		if (dd instanceof YANG_Choice) {
			YANG_Choice c = (YANG_Choice) dd;
			for (Enumeration<YANG_Case> ecases = c.getCases().elements(); ecases
					.hasMoreElements();) {
				YANG_Case ca = ecases.nextElement();
				for (Enumeration<YANG_DataDef> ecdef = ca.getDataDefs()
						.elements(); ecdef.hasMoreElements();) {
					YANG_DataDef cdef = ecdef.nextElement();
					if (cdef instanceof YANG_DataDef)
						if (findKey(context, k, (YANG_DataDef) cdef))
							return true;
				}
			}
			return false;
		}
		return false;
	}

	public void deleteUniques(Vector<YANG_Unique> u) {
		uniques.removeAll(u);

	}

	public YANG_List clone() {
		YANG_List cl = new YANG_List(parser, id);
		cl.setContext(getContext());
		cl.setList(getList());
		cl.setFileName(getFileName());
		cl.setParent(getParent());
		cl.setCol(getCol());
		cl.setLine(getLine());
		if (getKey() != null)
			cl.setKey(getKey());
		for (YANG_Unique u : getUniques())
			cl.addUnique(u);
		if (getConfig() != null)
			cl.setConfig(getConfig());
		if (getDescription() != null)
			cl.setDescription(getDescription());
		cl.setIfFeature(getIfFeatures());
		cl.setMusts(getMusts());
		cl.setUnknowns(getUnknowns());
		if (getReference() != null)
			cl.setReference(getReference());
		if (getStatus() != null)
			cl.setStatus(getStatus());
		if (getWhen() != null)
			cl.setWhen(getWhen());
		if (getMaxElement() != null)
			cl.setMaxElement(getMaxElement());
		if (getMinElement() != null)
			cl.setMinElement(getMinElement());
		if (getOrderedBy() != null)
			cl.setOrderedBy(getOrderedBy());
		return cl;
	}

	public String toString() {
		String result = new String();
		result += "list " + list + "{\n";
		if (b_key)
			result += key.toString() + "\n";
		for (YANG_Unique u : getUniques())
			result += u.toString() + "\n";
		result += super.toString() + "\n";
		for (Enumeration<YANG_TypeDef> et = typedefs.elements(); et
				.hasMoreElements();)
			result += et.nextElement().toString() + "\n";
		for (Enumeration<YANG_Grouping> eg = groupings.elements(); eg
				.hasMoreElements();)
			result += eg.nextElement().toString() + "\n";
		for (Enumeration<YANG_DataDef> ed = datadefs.elements(); ed
				.hasMoreElements();)
			result += ed.nextElement().toString() + "\n";
		result += super.toString() + "\n";
		result += "}\n";

		return result;
	}

	public void refines(YANG_RefineList rl) {
		if (rl.getConfig() != null)
			config = rl.getConfig();
		if (rl.getDescription() != null)
			description = rl.getDescription();
		if (rl.getReference() != null)
			reference = rl.getReference();
		for (YANG_Must must : rl.getMusts())
			addMust(must);
		if (rl.getMinElement() != null)
			min = rl.getMinElement();
		if (rl.getMaxElement() != null)
			max = rl.getMaxElement();
	}

}
