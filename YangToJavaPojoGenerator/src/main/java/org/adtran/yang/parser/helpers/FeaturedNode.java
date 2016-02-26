package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Vector;

public abstract class FeaturedNode extends StatuedNode implements DataDefsContainer{

	private YANG_When when = null;
	private Vector<YANG_IfFeature> ifFeatures = new Vector<YANG_IfFeature>();
	private Vector<YANG_DataDef> datadefs = new Vector<YANG_DataDef>();

	private boolean b_when = false;

	public FeaturedNode(int id) {
		super(id);
	}

	public FeaturedNode(yang p, int id) {
		super(p, id);
	}

	public YANG_When getWhen() {
		return when;
	}

	public boolean isBracked() {
		return super.isBracked() || b_when || ifFeatures.size() != 0
				|| datadefs.size() != 0;
	}

	public void setWhen(YANG_When w) {
		if (b_when)
			YangErrorManager.addError(filename, w.getLine(), w.getCol(), "unex_kw",
					"when");

		b_when = true;
		this.when = w;
	}

	public Vector<YANG_IfFeature> getIfFeatures() {
		return ifFeatures;
	}

	public void addIfFeature(YANG_IfFeature i) {
		ifFeatures.add(i);
	}

	public void addDataDef(YANG_DataDef d) {
		datadefs.add(d);
	}

	public Vector<YANG_DataDef> getDataDefs() {
		return datadefs;
	}

	public String toString() {
		String result = "";
		if (b_when)
			result += "when " + when.toString() + "\n";
		for (Enumeration<YANG_IfFeature> eifs = ifFeatures.elements(); eifs
				.hasMoreElements();)
			result += eifs.nextElement().toString() + "\n";
		for (Enumeration<YANG_DataDef> eddefs = datadefs.elements(); eddefs
				.hasMoreElements();)
			result += eddefs.nextElement().toString() + "\n";
		result += super.toString() + "\n";
		return result;
	}

}
