package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;
import java.util.Vector;

public abstract class YANG_DataDef extends FeaturedBody {

	private YANG_When when = null;

	private boolean b_when = false;

	public YANG_DataDef(int id) {
		super(id);
	}

	public YANG_DataDef(yang p, int id) {
		super(p, id);
	}

	public boolean isBracked() {
		return super.isBracked() || b_when;
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

	public Vector<YangTreeNode> groupTreeNode(YangTreeNode parent) {

		Vector<YangTreeNode> result = new Vector<YangTreeNode>();
		if (this instanceof YANG_Leaf || this instanceof YANG_LeafList
				|| this instanceof YANG_AnyXml) {
			YangTreeNode child = new YangTreeNode();
			child.setNode(this.clone());
			child.setParent(parent);
			result.add(child);
		} else if (this instanceof DataDefsContainer) {
			YangTreeNode child = new YangTreeNode();
			child.setNode(this.clone());
			child.setParent(parent);
			DataDefsContainer ddefcont = (DataDefsContainer) this;
			for (YANG_DataDef ddef : ddefcont.getDataDefs()) {
				Vector<YangTreeNode> sons = ddef.groupTreeNode(child);
				for (YangTreeNode son : sons) {
					child.addChild(son);
				}
			}
			result.add(child);
		} else if (this instanceof YANG_Choice) {
			YangTreeNode child = new YangTreeNode();
			child.setNode(this.clone());
			child.setParent(parent);
			YANG_Choice choice = (YANG_Choice) this;
			for (YANG_Case ycase : choice.getCases()) {
				CaseDataDef cddef = new CaseDataDef(ycase);
				cddef.setContext(choice.getContext());
				Vector<YangTreeNode> sons = cddef.groupTreeNode(child);
				for (YangTreeNode son : sons) {
					child.addChild(son);
				}
			}
			for (YANG_ShortCase scase : choice.getShortCases()) {
				YANG_DataDef ddef = (YANG_DataDef) scase;
				Vector<YangTreeNode> sons = ddef.groupTreeNode(child);
				for (YangTreeNode son : sons) {
					child.addChild(son);
				}

			}
			result.add(child);
		} else if (this instanceof YANG_Uses) {
			YANG_Uses uses = (YANG_Uses) this;
			YANG_Grouping grouping = uses.getGrouping();
			if (grouping != null && !uses.isRecursive()) {
				for (YANG_DataDef ddef : grouping.getDataDefs()) {
					for (YangTreeNode son : ddef.groupTreeNode(parent)) {
						son.setUses(uses);
						result.add(son);
						for (YANG_UsesAugment aug : uses.getUsesAugments()) {
							if (aug.getUsesAugment().compareTo(
									son.getNode().getBody()) == 0) {
								for (YANG_DataDef addef : aug.getDataDefs()) {
									for (YangTreeNode sonson : addef
											.groupTreeNode(son))
										son.addChild(sonson);
								}
							}
						}

					}
				}
				for (YangTreeNode ytn : result)
					for (YangTreeNode ytn2 : result)
						if (ytn != ytn2
								&& ytn.getNode().getBody().compareTo(
										ytn2.getNode().getBody()) == 0)
							YangErrorManager.addError(uses.getFileName(), uses
									.getLine(), uses.getCol(), "dup_child", ytn
									.getNode().getBody(), ytn.getNode()
									.getFileName(), ytn.getNode().getLine());

			}
		}
		return result;
	}

	public abstract YANG_Body clone();

	public String toString() {
		String result = "";
		if (when != null)
			result += "when " + when + ";";
		result += super.toString();
		return result;
	}

}
