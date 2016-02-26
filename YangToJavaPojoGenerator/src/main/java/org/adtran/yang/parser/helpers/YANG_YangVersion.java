package org.adtran.yang.parser.helpers;

public class YANG_YangVersion extends SimpleYangNode implements YANG_Header {

	private String version = null;

	public YANG_YangVersion(int id) {
		super(id);
	}

	public YANG_YangVersion(yang p, int id) {
		super(p, id);
	}

	public void setVersion(String v) {
		version = unquote(v);
	}

	public String getYangVersion() {
		return version;
	}

	public String toString() {
		return "yang-version " + version + ";";
	}

}
