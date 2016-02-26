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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class YangTreeNode implements java.io.Serializable {

	private YangTreeNode parent = null;
	private Vector<YangTreeNode> childs = new Vector<YangTreeNode>();
	private YANG_Body node = null;

	private YANG_Case casenode = null;

	private YANG_Uses uses = null;

	public void setUses(YANG_Uses uses) {
		this.uses = uses;
		used = true;
	}

	public YANG_Uses getUses() {
		return uses;
	}

	public YangTreeNode() {
		used = false;
	}

	public void setParent(YangTreeNode p) {
		parent = p;
	}

	public YangTreeNode getParent() {
		return parent;
	}

	public void addChild(YangTreeNode c) {
		childs.add(c);
	}

	public Vector<YangTreeNode> getChilds() {
		return childs;
	}

	public void setNode(YANG_Body n) {
		node = n;
	}

	public YANG_Case getCasenode() {
		return casenode;
	}

	public void setCasenode(YANG_Case casenode) {
		this.casenode = casenode;
	}

	public YANG_Body getNode() {
		return node;
	}

	public YangTreeNode getNodeInTree(YANG_Specification module,
			YangTreeNode root, Hashtable<String, YangTreeNode> importeds,
			String nid) {
		String[] nids = nid.split("/");
		YangTreeNode startnode = null;
		int starting = 0;
		boolean relativeXpath = false;
		if (nids[0].length() == 0)
			starting = 1;
		else
			relativeXpath = true;

		if (nids[starting].indexOf(':') != -1) {
			String prefix = nids[starting].substring(0, nids[starting]
					.indexOf(':'));
			if (prefix.compareTo(module.getPrefix().getPrefix()) == 0) {
				if (relativeXpath)
					startnode = this.getParent();
				else
					startnode = root;
			} else {
				boolean found = false;
				YANG_Import yimport = null;
				for (Enumeration<YANG_Import> ei = module.getImports()
						.elements(); ei.hasMoreElements() && !found;) {
					yimport = ei.nextElement();
					found = yimport.getPrefix().getPrefix().compareTo(prefix) == 0;
				}
				if (found) {
					startnode = this;
				} else {
					System.err
							.println("Panic in isInTree of YangTreeNode, an import disapears !");
					System.exit(-1);
				}
			}
		} else {
			if (relativeXpath)
				startnode = this.getParent();
			else
				startnode = root;
		}

		boolean foundchild = false;
		boolean stop = false;
		for (int i = starting; i < nids.length && !stop; i++) {
			YangTreeNode child = null;
			boolean foundonechild = false;
			for (Enumeration<YangTreeNode> et = startnode.getChilds()
					.elements(); et.hasMoreElements() && !foundonechild;) {
				child = et.nextElement();
				foundonechild = sameNode(nids[i], child);
			}
			if (foundonechild) {
				startnode = child;
				foundchild = true;
			} else {
				foundchild = false;
				stop = true;
			}
		}
		if (foundchild)
			return startnode;
		else
			return null;
	}

	public YANG_Body getBodyInTree(YANG_Specification module,
			YangTreeNode root, Hashtable<String, YangTreeNode> importeds,
			String nid) {
		String[] nids = nid.split("/");
		YangTreeNode startnode = null;
		int starting = 0;
		boolean relativeXpath = false;
		if (nids[0].length() == 0)
			starting = 1;
		else
			relativeXpath = true;

		if (nids[starting].indexOf(':') != -1) {
			String prefix = nids[starting].substring(0, nids[starting]
					.indexOf(':'));
			if (prefix.compareTo(module.getPrefix().getPrefix()) == 0) {
				if (relativeXpath)
					startnode = this.getParent();
				else
					startnode = root;
			} else {
				boolean found = false;
				YANG_Import yimport = null;
				for (Enumeration<YANG_Import> ei = module.getImports()
						.elements(); ei.hasMoreElements() && !found;) {
					yimport = ei.nextElement();
					found = yimport.getPrefix().getPrefix().compareTo(prefix) == 0;
				}
				if (found) {
					startnode = this;
				} else {
					System.err
							.println("Panic in isInTree of YangTreeNode, an import disapears !");
					System.exit(-1);
				}
			}
		} else {
			if (relativeXpath)
				startnode = this.getParent();
			else
				startnode = root;
		}

		if (startnode == null)
			return null;

		boolean foundchild = false;
		boolean stop = false;
		for (int i = starting; i < nids.length && !stop; i++) {
			YangTreeNode child = null;
			boolean foundonechild = false;
			for (Enumeration<YangTreeNode> et = startnode.getChilds()
					.elements(); et.hasMoreElements() && !foundonechild;) {
				child = et.nextElement();
				foundonechild = sameNode(nids[i], child);
			}
			if (foundonechild) {
				startnode = child;
				foundchild = true;
			} else {
				foundchild = false;
				stop = true;
			}
		}
		if (foundchild)
			return startnode.getNode();
		else
			return null;
	}

	private boolean sameNode(String n1, YangTreeNode ytn) {
		if (n1.indexOf(':') != -1) {
			return ytn.getNode().getBody().compareTo(
					n1.substring(n1.indexOf(':') + 1)) == 0;
		} else {
			return ytn.getNode().getBody().compareTo(n1) == 0;
		}

	}

	public void check(YANG_Specification module, YangTreeNode root,
			YangTreeNode subroot, Hashtable<String, YangTreeNode> importeds) {
		checkDupChilds(module, root, subroot, importeds);
		checkConfig();

	}

	private void checkConfig() {

		checkConfigDataDef();
		checkConfigList("true");
	}

	protected void checkConfigList(String cstr) {
		String parentcfg = cstr;
		for (YangTreeNode child : getChilds()) {
			if (child.getNode() instanceof YANG_List) {
				if (!(getNode() instanceof IoDataDef || getNode() instanceof YANG_Notification) ) {
					YANG_List list = (YANG_List) child.getNode();
					if (list.getConfig() == null) {
						if (parentcfg.compareTo("true") == 0) {

							if (list.getKey() == null) {
								if (child.isUsed()) {
									YangErrorManager.addError(child.getUses()
											.getFileName(), child.getUses()
											.getLine(), child.getUses()
											.getCol(),
											"config_list_true_no_key", list
													.getList());
								} else
									YangErrorManager.addError(list.getFileName(),
											list.getLine(), list.getCol(),
											"config_list_true_no_key", list
													.getList());
							}
						}
					} else {
						if (list.getConfig().getConfigStr().compareTo("true") == 0) {

							if (list.getKey() == null) {
								if (child.isUsed()) {
									YangErrorManager.addError(child.getUses()
											.getFileName(), child.getUses()
											.getLine(), child.getUses()
											.getCol(),
											"config_list_true_no_key", list
													.getList());
								} else
									YangErrorManager.addError(list.getFileName(),
											list.getLine(), list.getCol(),
											"config_list_true_no_key", list
													.getList());
							}

						}
					}
				}
			} else if (child.getNode() instanceof ConfigDataDef) {
				if (((ConfigDataDef) child.getNode()).getConfig() != null) {
					child.checkConfigList(((ConfigDataDef) child.getNode())
							.getConfig().getConfigStr());
				} else
					child.checkConfigList(parentcfg);
			} else
				child.checkConfigList(parentcfg);
		}
	}

	private void checkConfigDataDef() {

		if (getChilds().size() == 0)
			bottomUpConfig(this);
		else
			for (YangTreeNode child : getChilds())
				child.checkConfigDataDef();
	}

	private void bottomUpConfig(YangTreeNode n) {

		YANG_Config lastconf = null;
		if (n.getNode() instanceof ConfigDataDef)
			if (((ConfigDataDef) n.getNode()).getConfig() != null) {
				lastconf = ((ConfigDataDef) n.getNode()).getConfig();
			}
		boolean ok = true;

		YangTreeNode iparent = n;
		ConfigDataDef icddef = null;
		while (ok && iparent.getNode() != null) {
			if (lastconf == null) {
				if (iparent.getNode() instanceof ConfigDataDef) {
					if (((ConfigDataDef) iparent.getNode()).getConfig() != null) {
						lastconf = ((ConfigDataDef) iparent.getNode())
								.getConfig();
					}
				}
			} else {
				if (iparent.getNode() instanceof ConfigDataDef) {
					icddef = (ConfigDataDef) iparent.getNode();
					if (icddef.getConfig() != null)
						if (icddef.getConfig().getConfigStr()
								.compareTo("false") == 0
								&& lastconf.getConfigStr().compareTo("true") == 0)
							ok = false;
				}
			}
			iparent = iparent.getParent();
		}
		if (!ok)
			YangErrorManager.addError(icddef.getFileName(), icddef.getLine(),
					icddef.getCol(), "config_parent_true", icddef.getBody());
	}

	public void checkDupChilds(YANG_Specification module, YangTreeNode root,
			YangTreeNode subroot, Hashtable<String, YangTreeNode> importeds) {

		if (getParent() != null)
			for (YangTreeNode ytn : getParent().getChilds()) {
				if (ytn != this) {
					if (ytn.getNode().getBody().compareTo(getNode().getBody()) == 0) {
						if (getNode().getFileName().compareTo(
								ytn.getNode().getFileName()) == 0) {
							int l1, c1, l2;
							if (getNode().getLine() > ytn.getNode().getLine()) {
								l1 = getNode().getLine();
								c1 = getNode().getCol();
								l2 = ytn.getNode().getLine();
							} else {
								l1 = ytn.getNode().getLine();
								c1 = ytn.getNode().getCol();
								l2 = getNode().getLine();
							}
							YangErrorManager.addError(getNode().getFileName(), l1,
									c1, "dup_child", getNode().getBody(),
									getNode().getFileName(), l2);

						} else
							YangErrorManager
									.addError(ytn.getNode().getFileName(), ytn
											.getNode().getLine(), ytn.getNode()
											.getCol(), "dup_child", ytn
											.getNode().getBody(), getNode()
											.getFileName(), getNode().getLine());
					}
				}
			}

		if (isUsed()) {
			YANG_Uses uses = getUses();
			for (YangTreeNode ytn : getParent().getChilds()) {
				if (ytn != this) {
					if (uses.getGrouping() != null) {
						if (getNode() instanceof YANG_Choice) {
							for (YANG_Case ycase : ((YANG_Choice) getNode())
									.getCases()) {
								for (YANG_DataDef cddef : ycase.getDataDefs()) {
									if (cddef.getBody().compareTo(
											ytn.getNode().getBody()) == 0)
										YangErrorManager.addError(uses
												.getFileName(), uses.getLine(),
												uses.getCol(), "dup_child", ytn
														.getNode().getBody(),
												cddef.getFileName(), cddef
														.getLine());
								}
							}
							for (YANG_ShortCase scase : (((YANG_Choice) getNode())
									.getShortCases())) {
								YANG_DataDef sddef = (YANG_DataDef) scase;
								if (sddef.getBody().compareTo(
										ytn.getNode().getBody()) == 0)
									YangErrorManager.addError(uses.getFileName(),
											uses.getLine(), uses.getCol(),
											"dup_child", ytn.getNode()
													.getBody(), sddef
													.getFileName(), sddef
													.getLine());

							}
						}
					}
				}
			}
			Hashtable<String, YANG_Refine> refnds = new Hashtable<String, YANG_Refine>();
			for (YANG_Refine ref : uses.getRefinements()) {
				String refnid = ref.getRefineNodeId();
				if (refnds.containsKey(refnid)) {
					YANG_Refine firstref = refnds.get(refnid);
					YangErrorManager.addError(ref.getFileName(), ref.getLine(), ref
							.getCol(), "dup_child", refnid, firstref
							.getFileName(), firstref.getLine());
				} else
					refnds.put(ref.getRefineNodeId(), ref);
			}
			for (YANG_Refine ref : uses.getRefinements()) {
				String refnode = ref.getRefineNodeId();
				YANG_Body body = getBodyInTree(module, root, importeds, refnode);
				if (body == null) {
					YangErrorManager.addError(ref.getFileName(), ref.getLine(), ref
							.getCol(), "unknown", "node", refnode);
				} else {
					if (body instanceof YANG_Leaf) {
						YANG_Leaf refinedleaf = (YANG_Leaf) body;
						YANG_RefineLeaf refiningleaf = ((YANG_RefineAnyNode) ref)
								.getRefineLeaf();
						refiningleaf.check(refinedleaf);
						refinedleaf.refines(refiningleaf);
					} else if (body instanceof YANG_Container) {
						YANG_Container refinedcontainer = (YANG_Container) body;
						YANG_RefineContainer refiningcontainer = ((YANG_RefineAnyNode) ref)
								.getRefineContainer();
						refiningcontainer.check(refinedcontainer);
						refinedcontainer.refines(refiningcontainer);
					} else if (body instanceof YANG_AnyXml) {
						YANG_AnyXml refinedanyxml = (YANG_AnyXml) body;
						YANG_RefineAnyXml refininganyxml = ((YANG_RefineAnyNode) ref)
								.getRefineAnyXml();
						refininganyxml.check(refinedanyxml);
						refinedanyxml.refines(refininganyxml);
					} else if (body instanceof CaseDataDef) {
						CaseDataDef cddef = (CaseDataDef) body;
						YANG_Case refinedcase = cddef.getCase();
						YANG_RefineCase refiningcase = ((YANG_RefineAnyNode) ref)
								.getRefineCase();
						refiningcase.check(refinedcase);
						refinedcase.refines(refiningcase);
					} else if (body instanceof YANG_Choice) {
						YANG_Choice refinedchoice = (YANG_Choice) body;
						YANG_RefineChoice refiningchoice = ((YANG_RefineAnyNode) ref)
								.getRefineChoice();
						refiningchoice.check(refinedchoice);
						refinedchoice.refines(refiningchoice);
					} else if (body instanceof YANG_List) {
						YANG_List refinedlist = (YANG_List) body;
						YANG_RefineList refininglist = ((YANG_RefineAnyNode) ref)
								.getRefineList();

						refininglist.check(refinedlist);
						refinedlist.refines(refininglist);
					} else if (body instanceof YANG_LeafList) {
						YANG_LeafList refinedleaflist = (YANG_LeafList) body;
						YANG_RefineLeafList refiningleaflist = ((YANG_RefineAnyNode) ref)
								.getRefineLeafList();
						refiningleaflist.check(refinedleaflist);
						refinedleaflist.refines(refiningleaflist);
					}
				}

			}
			Vector<YANG_UsesAugment> vaugs = uses.getUsesAugments();
			String[] taugs = new String[vaugs.size()];
			int iu = 0;
			for (YANG_UsesAugment uaug : vaugs)
				taugs[iu++] = uaug.getUsesAugment();

			for (int i = 0; i < taugs.length; i++)
				for (int j = i + 1; j < taugs.length; j++) {
					String[] ti = taugs[i].split("/");
					String[] tj = taugs[j].split("/");
					if (ti.length > tj.length) {
						String ls = taugs[i];
						YANG_UsesAugment lai = vaugs.get(i);
						YANG_UsesAugment laj = vaugs.get(j);
						taugs[i] = taugs[j];
						taugs[j] = ls;
						vaugs.remove(i);
						vaugs.add(i, laj);
						vaugs.remove(j);
						vaugs.add(j, lai);
					}
				}

			for (int i = 0; i < taugs.length; i++) {
				YANG_Body augmentedbody = getBodyInTree(module, this,
						importeds, taugs[i]);
				if (augmentedbody == null) {
					for (String pref : importeds.keySet()) {
						YangTreeNode ytn = importeds.get(pref);
						augmentedbody = ytn.getBodyInTree(module, this,
								importeds, taugs[i]);
					}
					if (augmentedbody == null)
						YangErrorManager.addError(vaugs.get(i).getFileName(), vaugs
								.get(i).getLine(), vaugs.get(i).getCol(),
								"augmented_not_found", taugs[i]);
				} else {

					YANG_UsesAugment uaug = vaugs.get(i);

					YangTreeNode augmentednode = getNodeInTree(module, this,
							importeds, taugs[i]);
					if (parent.getParent() != null)
						if (augmentednode.getParent() != parent)
							augmentednode.usesaugments(uaug);
						else {
							YangErrorManager.addError(uaug.getFileName(), uaug
									.getLine(), uaug.getCol(),
									"not_augmentable", uaug.getBody(),
									augmentednode.getNode().getFileName(),
									augmentednode.getNode().getLine());
						}
				}
			}

		} else if (node instanceof YANG_Leaf) {
			YANG_Leaf leaf = (YANG_Leaf) node;
			YANG_Type type = leaf.getType();
			if (type != null) {
				/**
				 * The path specification is to evaluate on data tree
				 */
				/*
				 * if (type.getLeafRef() != null) { YANG_LeafRefSpecification
				 * krs = type.getLeafRef(); if (krs.getPath() != null) {
				 * YANG_Path path = krs.getPath(); YangTreeNode referenced =
				 * getNodeInTree(module, root, importeds, path.getPath()); if
				 * (referenced == null)
				 * YangErrorManager.tadd(path.getFileName(), path .getLine(),
				 * path.getCol(), "unknown", "node", path.getPath()); } }
				 */
			}
		} else if (node instanceof YANG_List) {
			YANG_List list = (YANG_List) node;

			String configlist = null;
			if (list.getConfig() == null) {
				YANG_Config pconfig = list.getParentConfig();
				if (pconfig != null)
					configlist = list.getParentConfig().getConfigStr();
				else
					configlist = null;
			} else
				configlist = list.getConfig().getConfigStr();

			YANG_Key k = list.getKey();
			if (k != null) {
				String[] tkstr = k.getKeyLeaves();
				for (int i = 0; i < tkstr.length; i++) {
					String kstr = tkstr[i];
					YANG_Body kbody = getBodyInTree(module, root, importeds,
							list.getBody() + "/" + kstr);
					if (kbody != null) {
						YangContext context = kbody.getContext();
						if (kbody instanceof YANG_Leaf) {
							YANG_Leaf leaf = (YANG_Leaf) kbody;

							if (context.getBuiltInType(leaf.getType()) != null) {
								if (YangBuiltInTypes.empty.compareTo(context
										.getBuiltInType(leaf.getType())) == 0)
									YangErrorManager.addError(node.getFileName(), k
											.getLine(), k.getCol(),
											"key_empty", kstr, list.getList());

								String configkeyleaf = null;
								if (leaf.getConfig() == null)
									configkeyleaf = configlist;
								else
									configkeyleaf = YangBuiltInTypes
											.removeQuotesAndTrim(leaf
													.getConfig().getConfigStr());
								if (configlist != null) {
									if (configlist.compareTo(configkeyleaf) != 0)

										YangErrorManager.addError(kbody
												.getFileName(),
												kbody.getLine(),
												kbody.getCol(), "key_config",
												kstr, list.getList(), list
														.getFileName(), list
														.getLine());
								} else if (configkeyleaf != null)
									YangErrorManager.addError(node.getFileName(), k
											.getLine(), k.getCol(),
											"key_config", kstr, list.getList());
							}

						}

					} else
						YangErrorManager.addError(node.getFileName(), k.getLine(),
								k.getCol(), "key_not_found", kstr, list
										.getList());
				}
			}
			for (YANG_Unique u : list.getUniques()) {
				String[] uniques = u.getUniqueLeaves();
				for (int i = 0; i < uniques.length; i++)
					if (getBodyInTree(module, root, importeds, list.getBody()
							+ "/" + uniques[i].trim()) == null) {
						YangErrorManager.addError(node.getFileName(), u.getLine(),
								u.getCol(), "unique_not_found", uniques[i],
								node.getBody());
					}
			}
		}
		for (Enumeration<YangTreeNode> ey = childs.elements(); ey
				.hasMoreElements();)
			ey.nextElement().checkDupChilds(module, root, subroot, importeds);
	}

	private boolean used = false;

	public boolean isUsed() {
		return used;
	}

	public String toString() {
		String result = "";
		if (getParent() != null) {
			result += node.getBody() + "(";
		} else
			result += "module (";
		int i = childs.size();
		for (YangTreeNode ytn : childs) {
			result += (i == 1) ? ytn.toString() : ytn.toString() + ",";
			i--;
		}
		result += ")";
		return result;
	}

	public void augments(YANG_Augment aug) {
		for (YANG_DataDef ddef : aug.getDataDefs()) {
			for (YangTreeNode child : ddef.groupTreeNode(this)) {
				boolean ok = true;
				for (YangTreeNode son : getChilds())
					if (ddef.getBody().compareTo(son.getNode().getBody()) == 0) {
						int l1, c1, l2;
						if (ddef.getFileName().compareTo(
								son.getNode().getFileName()) == 0) {
							if (ddef.getLine() > son.getNode().getLine()) {
								l1 = ddef.getLine();
								c1 = ddef.getCol();
								l2 = son.getNode().getLine();
							} else {
								l1 = son.getNode().getLine();
								c1 = son.getNode().getCol();
								l2 = ddef.getLine();
							}
							YangErrorManager.addError(ddef.getFileName(), l1, c1,
									"dup_child", ddef.getBody(), son.getNode()
											.getFileName(), l2);

						} else
							YangErrorManager.addError(ddef.getFileName(), ddef
									.getLine(), ddef.getCol(), "dup_child",
									ddef.getBody(),
									son.getNode().getFileName(), son.getNode()
											.getLine());
						ok = false;
					}
				if (ok)
					addChild(child);
			}
		}
	}

	public void usesaugments(YANG_UsesAugment aug) {
		for (YANG_DataDef ddef : aug.getDataDefs()) {
			for (YangTreeNode child : ddef.groupTreeNode(this)) {
				boolean ok = true;
				for (YangTreeNode son : getChilds())
					if (ddef.getBody().compareTo(son.getNode().getBody()) == 0) {
						int l1, c1, l2;
						if (ddef.getFileName().compareTo(
								son.getNode().getFileName()) == 0) {
							if (ddef.getLine() > son.getNode().getLine()) {
								l1 = ddef.getLine();
								c1 = ddef.getCol();
								l2 = son.getNode().getLine();
							} else {
								l1 = son.getNode().getLine();
								c1 = son.getNode().getCol();
								l2 = ddef.getLine();
							}
							YangErrorManager.addError(ddef.getFileName(), l1, c1,
									"dup_child", ddef.getBody(), son.getNode()
											.getFileName(), l2);

						} else
							YangErrorManager.addError(ddef.getFileName(), ddef
									.getLine(), ddef.getCol(), "dup_child",
									ddef.getBody(),
									son.getNode().getFileName(), son.getNode()
											.getLine());
						ok = false;
					}
				if (ok)
					addChild(child);
			}
		}
	}

	public void includeNode(YangTreeNode iytn) {
		boolean found = false;
		for (YangTreeNode child : getChilds()) {
			if (iytn.getNode().getBody().compareTo(child.getNode().getBody()) == 0) {
				found = true;
				for (YangTreeNode ichild : iytn.getChilds())
					child.includeNode(ichild);
			}
		}
		if (!found)
			addChild(iytn);
	}

	public void removeChild(YangTreeNode deviatednode) {
		boolean found = false;
		for (YangTreeNode ytn : getChilds()) {
			if (!found)
				if (deviatednode == ytn) {
					getChilds().remove(ytn);
					found = true;
				}
		}

	}

}
