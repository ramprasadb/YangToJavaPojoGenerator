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

public class YANG_Container extends MustDataDef implements YANG_ShortCase,
		DataDefsContainer {

	private String container = null;

	private YANG_Presence presence = null;
	private Vector<YANG_TypeDef> typedefs = new Vector<YANG_TypeDef>();
	private Vector<YANG_Grouping> groupings = new Vector<YANG_Grouping>();
	private Vector<YANG_DataDef> datadefs = new Vector<YANG_DataDef>();

	private boolean b_presence = false;

	public YANG_Container(int id) {
		super(id);
	}

	public YANG_Container(yang p, int id) {
		super(p, id);
	}

	public void setContainer(String c) {
		container = unquote(c);
	}

	public String getBody() {
		return getContainer();
	}

	public String getContainer() {
		return container;
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
		return super.isBracked() || b_presence || typedefs.size() != 0
				|| groupings.size() != 0 || datadefs.size() != 0;
	}

	public void check(YangContext context) {
			super.check(context);

	}

	public String toString() {
		String result = new String();
		result += "container " + container;
		if (isBracked()) {
			result += " {\n";
			for (Enumeration<YANG_TypeDef> et = typedefs.elements(); et
					.hasMoreElements();)
				result += et.nextElement().toString() + "\n";
			for (Enumeration<YANG_Grouping> eg = groupings.elements(); eg
					.hasMoreElements();)
				result += eg.nextElement().toString() + "\n";
			result += super.toString();
			if (b_presence)
				result += getPresence().toString() + "\n";
			for (Enumeration<YANG_DataDef> ed = datadefs.elements(); ed
					.hasMoreElements();)
				result += ed.nextElement().toString() + "\n";
			result += "}";
		} else
			result += ";";

		return result;
	}

	public YANG_Container clone() {
		YANG_Container cl = new YANG_Container(parser, id);
		cl.setContainer(getContainer());
		cl.setLine(getLine());
		cl.setCol(getCol());
		cl.setFileName(getFileName());
		if (getContext() != null)
			cl.setContext(getContext());
		if (getPresence() != null)
			cl.setPresence(getPresence());
		if (getConfig() != null)
			cl.setConfig(getConfig());
		if (getDescription() != null)
			cl.setDescription(getDescription());
		if (getReference() != null)
			cl.setReference(getReference());
		if (getStatus() != null)
			cl.setStatus(getStatus());
		if (getWhen() != null)
		cl.setWhen(getWhen());
		cl.setIfFeature(getIfFeatures());
		return cl;
	}

	public void refines(YANG_RefineContainer rl) {
		if (rl.getConfig() != null)
			config = rl.getConfig();
		if (rl.getDescription() != null)
			description = rl.getDescription();
		if (rl.getReference() != null)
			reference = rl.getReference();
		if (rl.getPresence() != null)
			presence = rl.getPresence();
		for (YANG_Must must : rl.getMusts())
			addMust(must);

	}

}
