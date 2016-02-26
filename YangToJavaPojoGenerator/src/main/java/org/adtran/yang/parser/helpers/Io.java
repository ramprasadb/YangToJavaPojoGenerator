package org.adtran.yang.parser.helpers;

import java.util.Enumeration;
import java.util.Vector;

public abstract class Io extends SimpleYangNode {

	protected Vector<YANG_TypeDef> typedefs = new Vector<YANG_TypeDef>();
	protected Vector<YANG_Grouping> groupings = new Vector<YANG_Grouping>();
	protected Vector<YANG_DataDef> datadefs = new Vector<YANG_DataDef>();

	public Io(int i) {
		super(i);
	}

	public Io(yang p, int i) {
		super(p, i);
	}

	public void addTypeDef(YANG_TypeDef t) {
		typedefs.add(t);
	}

	public Vector<YANG_TypeDef> getTypeDefs() {
		return typedefs;
	}

	public void addGrouping(YANG_Grouping g) {
		groupings.add(g);
	}

	public Vector<YANG_Grouping> getGroupings() {
		return groupings;
	}

	public void addDataDef(YANG_DataDef d) {
		datadefs.add(d);
	}

	public Vector<YANG_DataDef> getDataDefs() {
		return datadefs;
	}

	public String toString() {
		String result = "";
		for (Enumeration<YANG_TypeDef> et = typedefs.elements(); et
				.hasMoreElements();)
			result += et.nextElement().toString() + "\n";
		for (Enumeration<YANG_Grouping> eg = groupings.elements(); eg
				.hasMoreElements();)
			result += eg.nextElement().toString() + "\n";
		for (Enumeration<YANG_DataDef> ed = datadefs.elements(); ed
				.hasMoreElements();)
			result += ed.nextElement().toString() + "\n";
		return result;
	}

}
