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

public class YANG_Leaf extends MustDataDef implements YANG_ShortCase {

	private String leaf = null;
	private YANG_Type type = null;
	private YANG_Units units = null;
	private YANG_Mandatory mandatory = null;
	private YANG_Default ydefault = null;

	private boolean b_type = false, b_units = false, b_default = false,
			b_mandatory = false;

	public YANG_Leaf(int id) {
		super(id);
	}

	public YANG_Leaf(yang p, int id) {
		super(p, id);
	}

	public void setLeaf(String l) {
		leaf = unquote(l);
	}

	public String getBody() {
		return getLeaf();
	}

	public String getLeaf() {
		return leaf;
	}

	public void setType(YANG_Type t) {
		if (!b_type) {
			b_type = true;
			type = t;
		} else
			YangErrorManager.addError(filename, t.getLine(), t.getCol(), "unex_kw",
					"type");
	}

	public YANG_Type getType() {
		return type;
	}

	public void setUnits(YANG_Units u) {
		if (!b_units) {
			b_units = true;
			units = u;
		} else
			YangErrorManager.addError(filename, u.getLine(), u.getCol(), "unex_kw",
					"units");
	}

	public YANG_Units getUnits() {
		return units;
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

	public boolean isBracked() {
		return super.isBracked() || b_default || b_type || b_units
				|| b_mandatory;
	}

	public void check(YangContext context) {
		super.check(context);
		//setContext(context);
		if (!b_type)
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"expected_kw", "type");
		else {
			if (!YangBuiltInTypes.isBuiltIn(getType().getType()))
				if (!context.isTypeDefined(getType())) {
					YangErrorManager.addError(getFileName(), getType().getLine(),
							getType().getCol(), "unknown_type", getType()
									.getType());
				} else {
					getType().check(context);
					if (!context.getTypeDef(getType()).isChecked())
						context.getTypeDef(getType()).check(context);
				}
		}
		
		if (b_mandatory) {
			if (getMandatory().getMandatory().compareTo("true") == 0
					&& b_default)
				YangErrorManager.addError(getFileName(), getLine(), getCol(),
						"mand_def_val", getLeaf(), getDefault().getFileName(),
						getDefault().getLine());
		}
		if (b_default)
			if (getType() != null)
				getType().checkDefaultValue(context, this, getDefault());
			else {
				if (getType() != null) {
					YANG_TypeDef defining = context.getTypeDef(getType());
					while (defining != null) {
						if (defining.getDefault() != null) {
								getType().checkDefaultValue(context, this,
										defining.getDefault());
								defining = null;
						} else {
							defining = context.getBaseTypeDef(defining);
						}
					}
				}
			}

	}

	public void refines(YANG_RefineLeaf rl) {
		if (rl.getConfig() != null)
			config = rl.getConfig();
		if (rl.getDefault() != null)
			ydefault = rl.getDefault();
		if (rl.getDescription() != null)
			description = rl.getDescription();
		if (rl.getMandatory() != null)
			mandatory = rl.getMandatory();
		if (rl.getReference() != null)
			reference = rl.getReference();
		for (YANG_Must must : rl.getMusts())
			addMust(must);
	}

	public String toString() {
		String result = new String();
		result += "leaf " + leaf + "{\n";
		if (type != null)
			result += type.toString() + "\n";
		if (units != null)
			result += units.toString() + "\n";
		if (ydefault != null)
			result += ydefault.toString() + "\n";
		result += super.toString() + "\n";
		result += "}";

		return result;
	}

	public void deleteUnits() {
		units = null;
		b_units = false;
	}

	public void deleteDefault() {
		ydefault = null;
		b_default = false;
	}

	public void deleteMandatory() {
		mandatory = null;
		b_mandatory = false;
	}

	public void deleteType() {
		type = null;
		b_type = false;

	}

	public YANG_Leaf clone() {
		YANG_Leaf cl = new YANG_Leaf(parser, id);
		cl.setContext(getContext());
		cl.setLeaf(getLeaf());
		cl.setType(getType());
		if (getUnits() != null)
			cl.setUnits(getUnits());
		cl.setFileName(getFileName());
		cl.setCol(getCol());
		cl.setLine(getLine());
		if (getConfig() != null)
			cl.setConfig(getConfig());
		if (getDefault() != null)
			cl.setDefault(getDefault());
		if (getDescription() != null)
			cl.setDescription(getDescription());
		if (getMandatory() != null)
			cl.setMandatory(getMandatory());
		cl.setIfFeature(getIfFeatures());
		cl.setMusts(getMusts());
		cl.setUnknowns(getUnknowns());
		if (getReference() != null)
			cl.setReference(getReference());
		if (getStatus() != null)
			cl.setStatus(getStatus());
		if (getWhen() != null)
			cl.setWhen(getWhen());
		return cl;
	}

}
