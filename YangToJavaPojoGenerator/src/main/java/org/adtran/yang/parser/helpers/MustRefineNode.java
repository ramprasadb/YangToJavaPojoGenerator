package org.adtran.yang.parser.helpers;

import java.util.Enumeration;
import java.util.Vector;

public abstract class MustRefineNode extends ConfigRefineNode {
	
	private Vector<YANG_Must> musts = new Vector<YANG_Must>();


	public MustRefineNode(int id) {
		super(id);
	}
	
	public MustRefineNode(yang p, int id) {
		super(p, id);
	}

	public void addMust(YANG_Must m) {
		musts.add(m);
	}

	public Vector<YANG_Must> getMusts() {
		return musts;
	}
	
	public String toString() {
		String result = "";
		for (Enumeration<YANG_Must> em = musts.elements();em.hasMoreElements();)
			result += em.nextElement() + "\n";
		result += super.toString();
		return result;
	}
	

}
