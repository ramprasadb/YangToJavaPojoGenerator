package org.adtran.yang.parser.helpers;

import java.util.Vector;

public abstract class DataDefBody extends FeaturedBody {
	
	private Vector<YANG_DataDef> datadefs = new Vector<YANG_DataDef>();

	public DataDefBody(int id) {
		super(id);
	}

	public DataDefBody(yang p, int id) {
		super(p, id);
	}
	
	public void addDataDef(YANG_DataDef d) {
		datadefs.add(d);
	}

	public Vector<YANG_DataDef> getDataDefs() {
		return datadefs;
	}

	public boolean isBracked() {
		return datadefs.size() != 0 || super.isBracked();
	}
	
	public String toString() {
		String result = "";
		result += super.toString() + "\n";
		for (YANG_DataDef ddef : datadefs)
			result += ddef.toString() + "\n";
		return result;
	}

}
