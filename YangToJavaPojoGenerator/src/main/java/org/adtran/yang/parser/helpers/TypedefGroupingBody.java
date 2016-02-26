package org.adtran.yang.parser.helpers;

import java.util.Enumeration;
import java.util.Vector;

public abstract class TypedefGroupingBody extends FeaturedBody {
	
	private Vector<YANG_TypeDef> typedefs = new Vector<YANG_TypeDef>();
	private Vector<YANG_Grouping> groupings = new Vector<YANG_Grouping>();

	public TypedefGroupingBody(int id) {
		super(id);
	}

	public TypedefGroupingBody(yang p, int id) {
		super(p, id);
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
	
	public boolean isBracked() {
		return typedefs.size() != 0 || groupings.size() != 0 || super.isBracked();
	}
	
	
	public String toString() {
		String result = "";
		result += super.toString() + "\n";
		for (YANG_TypeDef etd : typedefs)
			result += etd.toString() + "\n";
		for (YANG_Grouping eg : groupings)
			result += eg.toString() + "\n";
		return result;
	}
	

}
