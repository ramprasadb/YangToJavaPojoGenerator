package org.adtran.yang.parser.helpers;

import java.util.Vector;

public abstract class FeaturedBody extends StatuedBody {

	protected Vector<YANG_IfFeature> ifFeatures = new Vector<YANG_IfFeature>();

	public FeaturedBody(int id) {
		super(id);
	}

	public FeaturedBody(yang p, int id) {
		super(p, id);
	}

	public Vector<YANG_IfFeature> getIfFeatures() {
		return ifFeatures;
	}

	public void addIfFeature(YANG_IfFeature i) {
		ifFeatures.add(i);
	}

	public void setIfFeature(Vector<YANG_IfFeature> ifs) {
		ifFeatures = ifs;
	}

	public void check(YangContext context) {
		for (YANG_IfFeature iff : ifFeatures) {
			YANG_Body b = context.getFeature(iff);
			if (b == null)
				YangErrorManager.addError(iff.getFileName(), iff.getLine(), iff
						.getCol(), "feature_not_found", iff.getIfFeature());
			else if (!(b instanceof YANG_Feature))
				YangErrorManager.addError(iff.getFileName(), iff.getLine(), iff
						.getCol(), "feature_not_found", iff.getIfFeature());
		}
	}

	public boolean isBracked() {
		return super.isBracked() || ifFeatures.size() != 0;
	}

	public String toString() {
		String result = "";
		for (YANG_IfFeature ifs : ifFeatures)
			result += ifs.toString() + "\n";
		return result;
	}

}
