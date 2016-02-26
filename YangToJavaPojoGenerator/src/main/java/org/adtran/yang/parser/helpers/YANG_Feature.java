/* Generated By:JJTree: Do not edit this line. YANG_Feature.java */

package org.adtran.yang.parser.helpers;

import java.util.Vector;

public class YANG_Feature extends FeaturedBody {

	private String feature = null;

	public YANG_Feature(int id) {
		super(id);
	}

	public YANG_Feature(yang p, int id) {
		super(p, id);
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = unquote(feature);
	}

	public void check(YangContext context) {
		super.check(context);
		for (YANG_IfFeature iff : ifFeatures) {
			YANG_Body depif = context.getFeature(iff);
			if (depif != null) {
				if (depif instanceof YANG_Feature) {
					YANG_Feature ff = (YANG_Feature) depif;
					checkRecursion(ff, context);
				}
			}
		}
	}

	private boolean checkRecursion(YANG_Feature f, YangContext context) {
		if (getFeature().compareTo(f.getFeature()) == 0)
			return false;
		else
			for (YANG_IfFeature iff : f.getIfFeatures()) {
				YANG_Body depif = context.getFeature(iff);
				if (depif != null) {
					if (depif instanceof YANG_Feature) {
						YANG_Feature ff = (YANG_Feature) depif;
						if (!checkRecursion(ff, context))
							YangErrorManager.addError(getFileName(), getLine(),
									getCol(), "rec_ifs", getBody(), f
											.getFileName(), f.getLine());
					}
				}
			}
		return true;
	}

	@Override
	public String getBody() {
		return feature;
	}

	public String toString() {
		String result = "";
		result += "feature " + feature;
		if (isBracked()) {
			result += "\n{";
			result += super.toString() + "\n";
			result += "}";
		} else
			result += ";";
		return result;

	}

}
