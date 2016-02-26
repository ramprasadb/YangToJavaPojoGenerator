package org.adtran.yang.parser.helpers;

import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YANG_UsesAugment extends FeaturedNode {

	private String usesaugment = null;
	private Vector<YANG_Case> cases = new Vector<YANG_Case>();

	private Pattern dsni = null;

	public YANG_UsesAugment(int id) {
		super(id);

		try {
			dsni = Pattern
					.compile("([_A-Za-z][._\\-A-Za-z0-9]*:)?[_A-Za-z][._\\-A-Za-z0-9]*((/([_A-Za-z][._A-Za-z0-9]*:)?[_A-Za-z][._A-Za-z0-9]*)+)?");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public YANG_UsesAugment(yang p, int id) {
		super(p, id);
	}

	public void setUsesAugment(String ua) {
		String aa = unquote(ua);
		Matcher m = dsni.matcher(aa);
		if (!m.matches())
			YangErrorManager.addError(filename, getLine(), getCol(),
					"uses_augment_exp", aa);
		usesaugment = aa;

	}

	public String getUsesAugment() {
		return usesaugment;
	}

	public String getBody() {
		return getUsesAugment();
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

	public void checkUsesAugment(YANG_Body augmented_node) {

		if (getCases().size() != 0 && !(augmented_node instanceof YANG_Choice)) {
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"bad_choice_aug", augmented_node.getBody(), augmented_node
							.getFileName(), augmented_node.getLine());
		}

		if (augmented_node instanceof YANG_Container) {
			YANG_Container container = (YANG_Container) augmented_node;
			for (YANG_DataDef addef : getDataDefs()) {
				if (container.getConfig() != null
						&& addef instanceof ConfigDataDef) {
					ConfigDataDef cddef = (ConfigDataDef) addef;
					if (cddef.getConfig().getConfigStr().compareTo("true") == 0
							&& container.getConfig().getConfigStr().compareTo(
									"false") == 0)
						YangErrorManager.addError(filename, getLine(), getCol(),
								"config_parent", addef.getFileName(), addef
										.getBody());
				}

			}
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

	private void checkDoubleCase(Vector<YANG_Case> vcases){
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
			YangErrorManager.addError(ayc.getFileName(), ayc.getLine(), ayc
					.getCol(), "dup_child", ayc.getBody(), yc.getFileName(), yc
					.getLine());
	}

	public String toString() {
		String result = "";
		result += "augment " + getUsesAugment();
		if (isBracked()) {
			result += "{\n";
			result += super.toString() + "\n";
			for (Enumeration<YANG_Case> ec = cases.elements(); ec
					.hasMoreElements();)
				result += ec.nextElement().toString() + "\n";
		} else
			result += ";";
		return result;
	}

}
