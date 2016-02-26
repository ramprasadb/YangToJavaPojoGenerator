package org.adtran.yang.parser.helpers;

import java.util.Enumeration;
import java.util.Vector;

public class YANG_DeviateDelete extends Deviate {

	private Vector<YANG_Must> musts = new Vector<YANG_Must>();
	private Vector<YANG_Unique> uniques = new Vector<YANG_Unique>();

	public YANG_DeviateDelete(int id) {
		super(id);
	}

	public YANG_DeviateDelete(yang p, int id) {
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

	public void setUnique(YANG_Unique u) {
		uniques.add(u);
	}

	public boolean isBracked() {
		return musts.size() != 0 || uniques.size() != 0 || super.isBracked();
	}

	public void deviates(YangTreeNode deviated) {

		if (getUnits() != null) {
			if (deviated.getNode() instanceof YANG_Leaf) {
				YANG_Leaf leaf = (YANG_Leaf) deviated.getNode();
				if (leaf.getUnits() != null) {
					if (leaf.getUnits().getUnits().compareTo(
							getUnits().getUnits()) == 0)
						leaf.deleteUnits();
					else
						YangErrorManager.addError(getFileName(), getUnits()
								.getLine(), getUnits().getCol(),
								"deviate_del_diff", "units", leaf.getBody(),
								leaf.getFileName(), leaf.getLine());
				} else
					YangErrorManager.addError(getFileName(), getUnits().getLine(),
							getUnits().getCol(), "deviate_del", "units", leaf
									.getBody(), leaf.getFileName(), leaf
									.getLine());
			} else if (deviated.getNode() instanceof YANG_LeafList) {
				YANG_LeafList leaflist = (YANG_LeafList) deviated.getNode();
				if (leaflist.getUnits() != null) {
					if (leaflist.getUnits().getUnits().compareTo(
							getUnits().getUnits()) == 0)
						leaflist.deleteUnits();
					else
						YangErrorManager.addError(getFileName(), getUnits()
								.getLine(), getUnits().getCol(),
								"deviate_del_diff", "units",
								leaflist.getBody(), leaflist.getFileName(),
								leaflist.getLine());
				} else
					YangErrorManager.addError(getFileName(), getUnits().getLine(),
							getUnits().getCol(), "deviate_del", "units",
							leaflist.getBody(), leaflist.getFileName(),
							leaflist.getLine());
			} else
				YangErrorManager.addError(getFileName(), getUnits().getLine(),
						getUnits().getCol(), "bad_deviate_del", "units",
						deviated.getNode().getBody(), deviated.getNode()
								.getFileName(), deviated.getNode().getLine());
		}
		if (getDefault() != null) {
			if (deviated.getNode() instanceof YANG_Leaf) {
				YANG_Leaf leaf = (YANG_Leaf) deviated.getNode();
				if (leaf.getDefault() != null) {
					if (leaf.getDefault().getDefault().compareTo(
							getDefault().getDefault()) == 0)
						leaf.deleteDefault();
					else
						YangErrorManager.addError(getFileName(), getDefault()
								.getLine(), getDefault().getCol(),
								"deviate_del_diff", "default", leaf.getBody(),
								leaf.getFileName(), leaf.getLine());
				} else
					YangErrorManager.addError(getFileName(),
							getDefault().getLine(), getDefault().getCol(),
							"deviate_del", "default", leaf.getBody(), leaf
									.getFileName(), leaf.getLine());

			} else if (deviated.getNode() instanceof YANG_Choice) {
				YANG_Choice choice = (YANG_Choice) deviated.getNode();
				if (choice.getDefault() != null) {
					if (choice.getDefault().getDefault().compareTo(
							getDefault().getDefault()) == 0)
						choice.deleteDefault();
					else
						YangErrorManager.addError(getFileName(), getDefault()
								.getLine(), getDefault().getCol(),
								"deviate_del_diff", "default",
								choice.getBody(), choice.getFileName(), choice
										.getLine());
				} else
					YangErrorManager.addError(getFileName(),
							getDefault().getLine(), getDefault().getCol(),
							"deviate_del", "default", choice.getBody(), choice
									.getFileName(), choice.getLine());

			} else
				YangErrorManager.addError(getFileName(), getDefault().getLine(),
						getDefault().getCol(), "bad_deviate_del", "default",
						deviated.getNode().getBody(), deviated.getNode()
								.getFileName(), deviated.getNode().getLine());
		}
		if (getMusts().size() != 0) {
			int l = getMusts().get(0).getLine();
			int c = getMusts().get(0).getCol();
			if (deviated.getNode() instanceof MustDataDef) {
				MustDataDef mddef = (MustDataDef) deviated.getNode();
				if (mddef.getMusts().size() != 0) {
					Vector<YANG_Must> vmusts = new Vector<YANG_Must>();
					for (YANG_Must md : getMusts()) {
						for (YANG_Must m : mddef.getMusts())
							if (m.getMust().compareTo(md.getMust()) == 0)
								vmusts.add(md);
					}
					if (vmusts.size() == getMusts().size())
						mddef.deleteMusts(vmusts);
					else
						YangErrorManager.addError(getFileName(), l, c,
								"deviate_del_diff", "must", mddef.getBody(),
								mddef.getFileName(), mddef.getLine());
				} else
					YangErrorManager.addError(getFileName(), l, c, "deviate_del",
							"must", mddef.getBody(), mddef.getFileName(), mddef
									.getLine());
			} else
				YangErrorManager.addError(getFileName(), l, c, "bad_deviate_del",
						"must", deviated.getNode().getBody(), deviated
								.getNode().getFileName(), deviated.getNode()
								.getLine());
		}
		
		if (getUniques().size() != 0) {
			int l = getUniques().get(0).getLine();
			int c = getUniques().get(0).getCol();
			if (deviated.getNode() instanceof YANG_List) {
				YANG_List list = (YANG_List) deviated.getNode();
				if (list.getUniques().size() != 0) {
					Vector<YANG_Unique> vuniques = new Vector<YANG_Unique>();
					for (YANG_Unique ud : getUniques()) {
						for (YANG_Unique u : list.getUniques())
							if (u.getUnique().compareTo(ud.getUnique()) == 0)
								vuniques.add(ud);
					}
					if (vuniques.size() == getUniques().size())
						list.deleteUniques(vuniques);
					else
						YangErrorManager.addError(getFileName(), l, c,
								"deviate_del_diff", "unique", list.getBody(),
								list.getFileName(), list.getLine());
				} else
					YangErrorManager.addError(getFileName(), l, c, "deviate_del",
							"unique", list.getBody(), list.getFileName(), list
									.getLine());
			} else
				YangErrorManager.addError(getFileName(), l, c, "bad_deviate_del",
						"unique", deviated.getNode().getBody(), deviated
								.getNode().getFileName(), deviated.getNode()
								.getLine());
		}

	}

	public String toString() {
		String result = "deviate delete";
		if (isBracked()) {
			result += "{\n";
			result += super.toString() + "\n";
			for (Enumeration<YANG_Must> em = musts.elements(); em
					.hasMoreElements();)
				result += em.nextElement() + "\n";
			for(YANG_Unique u : getUniques())
				result += u.toString() + "\n";
			result += "}";
		} else
			result += ";";
		return result;
	}
}
