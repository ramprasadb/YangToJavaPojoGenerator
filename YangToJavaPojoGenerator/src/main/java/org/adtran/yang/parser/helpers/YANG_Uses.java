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

public class YANG_Uses extends YANG_DataDef {

	private String uses = null;
	private Vector<YANG_RefineAnyNode> refinements = new Vector<YANG_RefineAnyNode>();
	private Vector<YANG_UsesAugment> usesaugments = new Vector<YANG_UsesAugment>();

	/**
	 * Grouping used reference
	 */
	private YANG_Grouping grouping = null;

	public YANG_Grouping getGrouping() {
		return grouping;
	}

	public void setGrouping(YANG_Grouping g) {
		this.grouping = g;
		if (grouping != null)
			grouping.setUsed(true);
	}

	public YANG_Uses(int id) {
		super(id);
	}

	public YANG_Uses(yang p, int id) {
		super(p, id);
	}

	public void setUses(String u) {
		uses = unquote(u);
	}

	public String getBody() {
		return getUses();
	}

	public String getUses() {
		return uses;
	}

	public boolean isBracked() {
		return super.isBracked() || refinements.size() != 0
				|| usesaugments.size() != 0;
	}

	public void addRefinement(YANG_RefineAnyNode r) {
		refinements.add(r);
	}

	public Vector<YANG_RefineAnyNode> getRefinements() {
		return refinements;
	}

	public void addUsesAugment(YANG_UsesAugment ua) {
		usesaugments.add(ua);
	}

	public Vector<YANG_UsesAugment> getUsesAugments() {
		return usesaugments;
	}

	private boolean checked = false;
	private boolean recursive = false;

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public void check(YangContext context) {

		super.check(context);
		if (!context.isGroupingDefined(this)) {
			YangErrorManager.addError(filename, getLine(), getCol(), "unknown",
					"grouping", uses);
			return;
		} else {

			YANG_Grouping grouping = context.getUsedGrouping(this);

			setGrouping(grouping);

			String gping = getGrouping().getGrouping();

			if (!grouping.isChecked()) {

				Vector<YANG_DataDef> datadefs = grouping.getDataDefs();

				for (Enumeration<YANG_DataDef> ed = datadefs.elements(); ed
						.hasMoreElements();) {
					YANG_Body body = (YANG_Body) ed.nextElement();

					body.setParent(getParent());
					YangContext clcts = context.clone();
					if (body instanceof YANG_Uses) {
						YANG_Uses used = (YANG_Uses) body;
						Set<YANG_Grouping> s = new HashSet<YANG_Grouping>();
						s.add(grouping);
						if (!checkRecursiveUses(context, s, used)) {
							YangErrorManager.addError(getFileName(), getLine(),
									getCol(), "rec_grouping", uses);
							setGrouping(null);
							setRecursive(true);
						}
					}
				}
			}

			checked = true;
		}
	}

	public boolean isChecked() {
		return checked;
	}

	private boolean checkRecursiveUses(YangContext context,
			Set<YANG_Grouping> setG, YANG_Uses used) {
		YANG_Grouping g = context.getUsedGrouping(used);
		if (g == null) // the uses is not good so it is not recursive
			return true;
		if (setG.contains(g)) {
			return false;
		} else {
			setG.add(g);
			for (YANG_DataDef ddef : g.getDataDefs()) {
				if (ddef instanceof YANG_Uses)
					return checkRecursiveUses(context, setG, (YANG_Uses) ddef);
			}
		}
		return true;
	}

	public YANG_Uses clone() {
		YANG_Uses cl = new YANG_Uses(parser, id);
		cl.setContext(getContext());
		cl.setUses(getUses());
		cl.setFileName(getFileName());
		cl.setParent(getParent());
		cl.setCol(getCol());
		cl.setLine(getLine());
		for (YANG_UsesAugment u : getUsesAugments())
			cl.addUsesAugment(u);
		for (YANG_RefineAnyNode r : getRefinements())
			cl.addRefinement(r);
		if (getDescription() != null)
			cl.setDescription(getDescription());
		cl.setIfFeature(getIfFeatures());
		cl.setUnknowns(getUnknowns());
		if (getReference() != null)
			cl.setReference(getReference());
		if (getStatus() != null)
			cl.setStatus(getStatus());
		if (getWhen() != null)
			cl.setWhen(getWhen());
		return cl;
	}

	public String toString() {
		String result = new String();
		result += "uses " + uses;
		// if (isBracked()) {
		// result += " {\n";
		result += super.toString() + "\n";
		for (YANG_UsesAugment er : usesaugments)
			result += er.toString() + "\n";
		for (YANG_Refine er : refinements)
			result += er.toString() + "\n";
		// } else
		result += ";";

		return result;
	}

}
