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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YANG_Augment extends DataDefBody {

	private String augment = null;
	private Vector<YANG_Case> cases = new Vector<YANG_Case>();
	private YANG_When when = null;

	private boolean b_when = false;

	private Pattern nid = null;

	public YANG_Augment(int id) {
		super(id);
		try {
			nid = Pattern
					.compile("((/([_A-Za-z][._\\-A-Za-z0-9]*:)?[_A-Za-z][._\\-A-Za-z0-9]*)+)|(([_A-Za-z][._\\-A-Za-z0-9]*:)?[_A-Za-z][._\\-A-Za-z0-9]*((/([_A-Za-z][._\\-A-Za-z0-9]*:)?[_A-Za-z][._\\-A-Za-z0-9]*)+)?)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public YANG_Augment(yang p, int id) {
		super(p, id);
	}

	public void setAugment(String a) {
		String aa = unquote(a);
		Matcher m = nid.matcher(aa);
		if (m.matches())
			augment = aa;
		else
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"bad_aug_expr", aa);
	}

	public String getBody() {
		return getAugment();
	}

	public String getAugment() {
		return augment;
	}

	public YANG_When getWhen() {
		return when;
	}

	public void setWhen(YANG_When w) {
		if (!b_when) {
			b_when = true;
			this.when = w;
		} else
			YangErrorManager.addError(filename, w.getLine(), w.getCol(), "unex_kw",
					"when");
	}

	public boolean isAbsoluteSchemaNodeId() {
		return augment.charAt(0) == '/';
	}

	public void addCase(YANG_Case c) {
		cases.add(c);
	}

	public Vector<YANG_Case> getCases() {
		return cases;
	}

	public boolean isBracked() {
		return super.isBracked() || cases.size() != 0;

	}

	public void check(YangContext context) {
			super.check(context);

		String nids[] = getAugment().split("/");
		int start = 0;
		if (nids[0].length() == 0)
			start = 1;
		if (nids[start].indexOf(':') != -1) {
			String prefix = nids[start].substring(0, nids[start].indexOf(':'));
			boolean found = false, localprefix = false;

			localprefix = prefix.compareTo(context.getSpec().getPrefix()
					.getPrefix()) == 0;
			found = localprefix;
			for (Enumeration<YANG_Import> ei = context.getImports().elements(); ei
					.hasMoreElements()
					&& !found;) {
				YANG_Import yimport = ei.nextElement();
				found = yimport.getPrefix().getPrefix().compareTo(prefix) == 0;
			}
			if (!found) {
				YangErrorManager.addError(context.getSpec().getName(), getLine(),
						getCol(), "ipnf", prefix);
				return;
			}

			for (int i = start; i < nids.length; i++) {
				if (nids[i].indexOf(':') != -1) {
					if (prefix.compareTo(nids[i].substring(0, nids[i]
							.indexOf(':'))) != 0) {
						YangErrorManager.addError(context.getSpec().getName(),
								getLine(), getCol(), "cp", getBody());
						return;
					}

				} else {
					if (!localprefix) {
						YangErrorManager.addError(context.getSpec().getName(),
								getLine(), getCol(), "cp", getBody());
						return;
					}
				}
			}
		}

		Vector<String> caseids = new Vector<String>();
		for (Enumeration<YANG_Case> ec = getCases().elements(); ec
				.hasMoreElements();) {
			YANG_Case ycase = ec.nextElement();
			if (caseids.contains(ycase.getCase())) {
				YangErrorManager.addError(context.getSpec().getName(), ycase
						.getLine(), ycase.getCol(), "ad", ycase.getBody());
			} else
				caseids.add(ycase.getCase());
		}

	}

	public void checkAugment(YANG_Body augmented_node) {

		if (getCases().size() != 0 && !(augmented_node instanceof YANG_Choice)) {
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"bad_choice_aug", augmented_node.getBody(), augmented_node
							.getFileName(), augmented_node.getLine());
		}

		if (augmented_node instanceof YANG_Container) {
			YANG_Container container = (YANG_Container) augmented_node;
			/*
			for (YANG_DataDef addef : getDataDefs()) {
				if (container.getConfig() != null
						&& addef instanceof ConfigDataDef) {
					ConfigDataDef cddef = (ConfigDataDef) addef;
					if (cddef.getConfig().getConfigStr().compareTo("true") == 0
							&& container.getConfig().getConfigStr().compareTo(
									"false") == 0)
						YangErrorManager.tadd(filename, getLine(), getCol(),
								"config_parent", addef.getFileName(), addef
										.getBody());
				}

			}
			*/
			checkDouble(container.getDataDefs());
		} else if (augmented_node instanceof YANG_List) {
			YANG_List list = (YANG_List) augmented_node;
			checkDouble(list.getDataDefs());
		} else if (augmented_node instanceof YANG_Choice) {
			YANG_Choice choice = (YANG_Choice) augmented_node;

			checkDoubleCase(choice.getCases());

		} else if (augmented_node instanceof YANG_Rpc) {
			YANG_Rpc rpc = (YANG_Rpc) augmented_node;
			Vector<YANG_DataDef> vdef = new Vector<YANG_DataDef>();
			if (rpc.getInput() != null)
				vdef.addAll(rpc.getInput().getDataDefs());
			if (rpc.getOutput() != null)
				vdef.addAll(rpc.getOutput().getDataDefs());
			checkDouble(vdef);
		} else if (augmented_node instanceof YANG_Notification) {
			YANG_Notification notif = (YANG_Notification) augmented_node;
			checkDouble(notif.getDataDefs());
		} else if (augmented_node instanceof IoDataDef) {
		} else {

			YangErrorManager.addError(filename, getLine(), getCol(),
					"not_augmentable", augmented_node.getBody(), augmented_node
							.getFileName(), augmented_node.getLine());
		}
	}

	private void checkDouble(Vector<YANG_DataDef> vddef) {
		boolean found = false;
		YANG_DataDef augddef = null;
		YANG_DataDef targddef = null;
		for (Enumeration<YANG_DataDef> eda = getDataDefs().elements(); eda
				.hasMoreElements()
				&& !found;) {
			augddef = eda.nextElement();
			for (Enumeration<YANG_DataDef> eddef = vddef.elements(); eddef
					.hasMoreElements()
					&& !found;) {
				targddef = eddef.nextElement();
				found = augddef.getBody().compareTo(targddef.getBody()) == 0;
			}
		}
		if (found)
			YangErrorManager.addError(augddef.getFileName(), augddef.getLine(),
					augddef.getCol(), "dup_child", augddef.getBody(), targddef
							.getFileName(), targddef.getLine());
	}

	private void checkDoubleCase(Vector<YANG_Case> vcases) {
		boolean found = false;
		YANG_Case ayc = null;
		YANG_Case yc = null;

		for (Enumeration<YANG_Case> ec = getCases().elements(); ec
				.hasMoreElements()
				&& !found;) {
			yc = ec.nextElement();

			for (Enumeration<YANG_Case> eayc = vcases.elements(); eayc
					.hasMoreElements()
					&& !found;) {
				ayc = eayc.nextElement();
				found = ayc.getBody().compareTo(yc.getBody()) == 0;
			}
		}
		if (found)
			YangErrorManager.addError(ayc.getFileName(), ayc.getLine(), ayc.getCol(),
					"dup_child", ayc.getBody(), yc.getFileName(), yc.getLine());
	}

	public String toString() {
		String result = new String();
		result += "augment " + augment + "{\n";
		result += super.toString() + "\n";
		for (YANG_Case ec : cases)
			result += ec.toString() + "\n";
		result += "}";
		return result;
	}
}
