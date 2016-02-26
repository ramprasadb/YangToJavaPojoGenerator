package org.adtran.yang.parser.helpers;

import java.util.Vector;

public abstract class MustDataDef extends ConfigDataDef {
	
	private Vector<YANG_Must> musts = new Vector<YANG_Must>();

	public MustDataDef(int id) {
		super(id);
	}

	public MustDataDef(yang p, int id) {
		super(p, id);
	}
	
	public void addMust(YANG_Must m) {
		musts.add(m);
	}
	
	public void setMusts(Vector<YANG_Must> m) {
		musts = m;
	}

	public Vector<YANG_Must> getMusts() {
		return musts;
	}
	
	public void deleteMusts(Vector<YANG_Must> vmusts) {
		getMusts().removeAll(vmusts);
		
	}
	

	public boolean isBracked() {
		return musts.size() != 0 || super.isBracked();
	}
	
	public String toString() {
		String result = "";
		result += super.toString() + "\n";
		for (YANG_Must m : musts)
			result += m.toString() + "\n";
		return result;
	}

}
