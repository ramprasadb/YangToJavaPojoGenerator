package org.adtran.yang.parser.helpers;

import java.util.Vector;

public abstract class SimpleYangNode extends SimpleNode implements YangNode {

	protected yang parser;
	private Vector<YANG_Unknown> unknowns = new Vector<YANG_Unknown>();
	protected String filename;
	private String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	private boolean isRootNode = false;

	private int line, col;

	public SimpleYangNode(int i) {
		super(i);
	}

	public SimpleYangNode(yang p, int i) {
		this(i);
		parser = p;
	}

	public void addUnknown(YANG_Unknown u) {
		unknowns.add(u);
	}

	public void setUnknowns(Vector<YANG_Unknown> u) {
		unknowns = u;
	}

	public Vector<YANG_Unknown> getUnknowns() {
		return unknowns;
	}

	public void setLine(int l) {
		line = l;
	}

	public void setCol(int c) {
		col = c;
	}

	public int getLine() {
		return line;
	}

	public int getCol() {
		return col;
	}

	private SimpleYangNode parent = null;

	protected void setParent(SimpleYangNode b) {
		parent = b;
	}

	/**
	 * return the base filename (without directories or /)
	 * 
	 */
	public String getFileName() {
		if (filename.indexOf('/') != -1)
			return filename.substring(filename.lastIndexOf('/') + 1);
		return filename;
	}

	public void setFileName(String f) {
		filename = f;
	}

	public SimpleYangNode getParent() {
		return parent;
	}

	protected void setRootNode(boolean b) {
		isRootNode = b;
	}

	protected boolean isRootNode() {
		return isRootNode;
	}

	protected YANG_Config getParentConfig() {
		if (this instanceof YANG_Grouping)
			return null;
		if (isRootNode) {
			if (getConfig() == null) {
				YANG_Config c = new YANG_Config(-1);
				c.setConfig(YangBuiltInTypes.config);
				return c;
			} else
				return getConfig();
		} else {
			SimpleYangNode parent = getParent();
			if (parent != null) {
				if (parent.getConfig() != null)
					return parent.getConfig();
				else
					return getParent().getParentConfig();
			}
		}
		return null;
	}

	protected String unquote(String s) {
		if (s.length() > 0) {
			if (s.charAt(0) == '"') {
				s = s.substring(1);
				if (s.charAt(s.length() - 1) == '"')
					s = s.substring(0, s.length() - 1);
			} else if (s.charAt(0) == '\'') {
				s = s.substring(1);
				if (s.charAt(s.length() - 1) == '\'')
					s = s.substring(0, s.length() - 1);
			}
		}
		return s;
	}

	protected YANG_Config getConfig() {
		return null;
	}

}
