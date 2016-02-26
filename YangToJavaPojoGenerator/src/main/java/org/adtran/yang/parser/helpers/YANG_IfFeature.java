package org.adtran.yang.parser.helpers;

public class YANG_IfFeature extends SimpleYangNode {

	private String iffeature = null;

	public YANG_IfFeature(int id) {
		super(id);
	}

	public YANG_IfFeature(yang p, int id) {
		super(p, id);
	}

	public String getIfFeature() {
		return iffeature;
	}

	public void setIfFeature(String iffeature) {
		this.iffeature = unquote(iffeature);
	}

	public String toString() {
		return "if-feature " + iffeature;
	}

	public boolean isPrefixed() {
		return iffeature.indexOf(':') != -1;
	}

	public String getPrefix() {
		if (isPrefixed())
			return iffeature.substring(0, iffeature.indexOf(':'));
		return "";
	}

	public String getSuffix() {
		if (isPrefixed())
			return iffeature.substring(iffeature.indexOf(':')+1);
		return iffeature;
	}

}
