package org.adtran.yang.parser.helpers;

import java.util.Enumeration;
import java.util.Vector;

public class YANG_DeviateAdd extends DeviateAddReplace {

	private Vector<YANG_Must> musts = new Vector<YANG_Must>();
	private Vector<YANG_Unique> uniques = new Vector<YANG_Unique>();

	public YANG_DeviateAdd(int id) {
		super(id);
	}

	public YANG_DeviateAdd(yang p, int id) {
		super(p, id);
	}

	public Vector<YANG_Must> getMusts() {
		return musts;
	}

	public void addMust(YANG_Must m) {
		this.musts.add(m);
	}

	public Vector<YANG_Unique> getUniques() {
		return uniques;
	}

	public void addUnique(YANG_Unique u) {
		uniques.add(u);
	}

	public boolean isBracked() {
		return musts.size() != 0 || uniques.size() != 0 || super.isBracked();
	}

	public String toString() {
		String result = "deviate add";
		if (isBracked()) {
			result += "{\n";
			result += super.toString() + "\n";
			for (Enumeration<YANG_Must> em = musts.elements(); em
					.hasMoreElements();)
				result += em.nextElement() + "\n";
			for (YANG_Unique u : getUniques())
				result += u.toString() + "\n";
			result += "}";
		} else
			result += ";";
		return result;
	}

	public void deviates(YangTreeNode deviated) {
		YangContext context = deviated.getNode().getContext();
		if (getDefault() != null) {
			if (deviated.getNode() instanceof YANG_Leaf) {
				YANG_Leaf leaf = (YANG_Leaf) deviated.getNode();
				if (leaf.getDefault() == null) {
					leaf.setDefault(getDefault());
					leaf.check(context);
				} else
					YangErrorManager.addError(getFileName(),
							getDefault().getLine(), getDefault().getCol(),
							"deviate_add", "default", leaf.getBody(), leaf
									.getFileName(), leaf.getLine());
			} else if (deviated.getNode() instanceof YANG_Choice) {
				YANG_Choice choice = (YANG_Choice) deviated.getNode();
				if (choice.getDefault() == null) {
					choice.setDefault(getDefault());
					choice.check(context);
				} else
					YangErrorManager.addError(getFileName(),
							getDefault().getLine(), getDefault().getCol(),
							"deviate_add", "default", choice.getBody(), choice
									.getFileName(), choice.getLine());
			} else
				YangErrorManager.addError(getFileName(), getDefault().getLine(),
						getDefault().getCol(), "bad_deviate_add", "default",
						deviated.getNode().getBody(), deviated.getNode()
								.getFileName(), deviated.getNode().getLine());
		}
		if (getMandatory() != null) {
			if (deviated.getNode() instanceof YANG_Leaf) {
				YANG_Leaf leaf = (YANG_Leaf) deviated.getNode();
				if (leaf.getMandatory() == null)
					leaf.setMandatory(getMandatory());
				else
					YangErrorManager.addError(getFileName(), getMandatory()
							.getLine(), getMandatory().getCol(), "deviate_add",
							"mandatory", leaf.getBody(), leaf.getFileName(),
							leaf.getLine());
			} else if (deviated.getNode() instanceof YANG_AnyXml) {
				YANG_AnyXml anyxml = (YANG_AnyXml) deviated.getNode();
				if (anyxml.getMandatory() == null)
					anyxml.setMandatory(getMandatory());
				else
					YangErrorManager.addError(getFileName(), getMandatory()
							.getLine(), getMandatory().getCol(), "deviate_add",
							"mandatory", anyxml.getBody(),
							anyxml.getFileName(), anyxml.getLine());
			} else
				YangErrorManager.addError(getFileName(), getMandatory().getLine(),
						getMandatory().getCol(), "bad_deviate_add",
						"mandatory", deviated.getNode().getBody(), deviated
								.getNode().getFileName(), deviated.getNode()
								.getLine());
		}
		if (getMinElement() != null) {
			if (deviated.getNode() instanceof ListedDataDef) {
				ListedDataDef lddef = (ListedDataDef) deviated.getNode();
				if (lddef.getMinElement() == null)
					lddef.setMinElement(getMinElement());
				else
					YangErrorManager.addError(getFileName(), getMinElement()
							.getLine(), getMinElement().getCol(),
							"deviate_add", "mandatory", lddef.getBody(), lddef
									.getFileName(), lddef.getLine());
			} else
				YangErrorManager.addError(getFileName(), getMinElement().getLine(),
						getMinElement().getCol(), "bad_deviate_add",
						"min-element", deviated.getNode().getBody(), deviated
								.getNode().getFileName(), deviated.getNode()
								.getLine());
		}
		if (getMaxElement() != null) {
			if (deviated.getNode() instanceof ListedDataDef) {
				ListedDataDef lddef = (ListedDataDef) deviated.getNode();
				if (lddef.getMaxElement() == null)
					lddef.setMaxElement(getMaxElement());
				else
					YangErrorManager.addError(getFileName(), getMaxElement()
							.getLine(), getMaxElement().getCol(),
							"deviate_add", "mandatory", lddef.getBody(), lddef
									.getFileName(), lddef.getLine());
			} else
				YangErrorManager.addError(getFileName(), getMaxElement().getLine(),
						getMaxElement().getCol(), "bad_deviate_add",
						"max-element", deviated.getNode().getBody(), deviated
								.getNode().getFileName(), deviated.getNode()
								.getLine());
		}
		if (getUnits() != null) {
			if (deviated.getNode() instanceof YANG_Leaf) {
				YANG_Leaf leaf = (YANG_Leaf) deviated.getNode();
				if (leaf.getUnits() == null)
					leaf.setUnits(getUnits());
				else
					YangErrorManager.addError(getFileName(), getUnits().getLine(),
							getUnits().getCol(), "deviate_add", "units", leaf
									.getBody(), leaf.getFileName(), leaf
									.getLine());
			} else if (deviated.getNode() instanceof YANG_LeafList) {
				YANG_LeafList leaflist = (YANG_LeafList) deviated.getNode();
				if (leaflist.getUnits() == null)
					leaflist.setUnits(getUnits());
				else
					YangErrorManager.addError(getFileName(), getUnits().getLine(),
							getUnits().getCol(), "deviate_add", "units",
							leaflist.getBody(), leaflist.getFileName(),
							leaflist.getLine());

			} else
				YangErrorManager.addError(getFileName(), getUnits().getLine(),
						getUnits().getCol(), "bad_deviate_add", "units",
						deviated.getNode().getBody(), deviated.getNode()
								.getFileName(), deviated.getNode().getLine());
		}
		if (getConfig() != null) {
			if (deviated.getNode() instanceof ConfigDataDef) {
				ConfigDataDef cddef = (ConfigDataDef) deviated.getNode();
				if (cddef.getConfig() == null)
					cddef.setConfig(getConfig());
				else
					YangErrorManager.addError(getFileName(), getConfig().getLine(),
							getConfig().getCol(), "deviate_add", "config",
							cddef.getBody(), cddef.getFileName(), cddef
									.getLine());
			} else
				YangErrorManager.addError(getFileName(), getConfig().getLine(),
						getConfig().getCol(), "bad_deviate_add", "config",
						deviated.getNode().getBody(), deviated.getNode()
								.getFileName(), deviated.getNode().getLine());
		}
		if (getMusts().size() != 0) {
			int l = getMusts().get(0).getLine();
			int c = getMusts().get(0).getCol();
			if (deviated.getNode() instanceof MustDataDef) {
				MustDataDef mddef = (MustDataDef) deviated.getNode();
				if (mddef.getMusts().size() == 0)
					mddef.setMusts(getMusts());
				else
					YangErrorManager.addError(getFileName(), l, c, "deviate_add",
							"must", mddef.getBody(), mddef.getFileName(), mddef
									.getLine());
			} else
				YangErrorManager.addError(getFileName(), l, c, "bad_deviate_add",
						"must", deviated.getNode().getBody(), deviated
								.getNode().getFileName(), deviated.getNode()
								.getLine());
		}
		if (getUniques().size() != 0) {
			int l = getUniques().get(0).getLine();
			int c = getUniques().get(0).getCol();
			if (deviated.getNode() instanceof YANG_List) {
				YANG_List list = (YANG_List) deviated.getNode();
				if (list.getUniques() == null)
					for (YANG_Unique u : getUniques())
						list.addUnique(u);
				else
					YangErrorManager.addError(getFileName(), l, c, "deviate_add",
							"unique", list.getBody(), list.getFileName(), list
									.getLine());
			} else
				YangErrorManager.addError(getFileName(), l, c, "bad_deviate_add",
						"unique", deviated.getNode().getBody(), deviated
								.getNode().getFileName(), deviated.getNode()
								.getLine());
		}
	}

}
