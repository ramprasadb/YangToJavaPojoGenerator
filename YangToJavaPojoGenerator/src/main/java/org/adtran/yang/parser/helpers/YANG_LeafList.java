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

public class YANG_LeafList extends ListedDataDef {

	private String leaflist = null;
	private YANG_Type type = null;
	private YANG_Units units = null;

	private boolean b_type = false, b_units = false;

	public YANG_LeafList(int id) {
		super(id);
	}

	public YANG_LeafList(yang p, int id) {
		super(p, id);
	}

	public void setLeafList(String l) {
		leaflist = unquote(l);
	}

	public String getBody() {
		return getLeafList();
	}

	public String getLeafList() {
		return leaflist;
	}

	public void setType(YANG_Type t) {
		if (!b_type) {
			b_type = true;
			type = t;
		} else
			YangErrorManager
					.addError(filename, t.getLine(), t.getCol(), "type");
	}

	public YANG_Type getType() {
		return type;
	}

	public void setUnits(YANG_Units u) {
		if (!b_units) {
			b_units = true;
			units = u;
		} else
			YangErrorManager.addError(filename, u.getLine(), u.getCol(),
					"unex_kw", "units");
	}

	public YANG_Units getUnits() {
		return units;
	}

	public boolean isBracked() {
		return super.isBracked() || b_type || b_units;
	}

	public void check(YangContext context) {
		super.check(context);
		if (!b_type)
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"expected", "type");
		else if (!YangBuiltInTypes.isBuiltIn(getType().getType()))
			if (!context.isTypeDefined(getType())) {
				YangErrorManager
						.addError(getFileName(), getType().getLine(), getType()
								.getCol(), "unknown_type", getType().getType());
			} else
				context.getTypeDef(getType()).check(context);
	}

	public YANG_LeafList clone() {
		YANG_LeafList cl = new YANG_LeafList(parser, id);
		cl.setFileName(getFileName());
		cl.setCol(getCol());
		cl.setLine(getLine());
		cl.setLeafList(getLeafList());
		cl.setContext(getContext());
		cl.setType(getType());
		if (getUnits() != null)
			cl.setUnits(getUnits());
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

	public void deleteUnits() {
		units = null;
		b_units = false;
	}

	public String toString() {
		String result = new String();
		result += "leaf-list " + leaflist + "{\n";
		if (b_type)
			result += type.toString() + "\n";
		if (b_units)
			result += units.toString() + "\n";
		result += super.toString() + "\n";
		result += "}\n";

		return result;
	}

	public void refines(YANG_RefineLeafList rl) {
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

	public void deleteType() {
		type = null;
		b_type = false;

	}

}
