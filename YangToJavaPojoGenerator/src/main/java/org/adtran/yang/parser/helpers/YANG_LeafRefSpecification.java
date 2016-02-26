package org.adtran.yang.parser.helpers;

public class YANG_LeafRefSpecification extends SimpleYangNode {

	private YANG_Path path = null;

	public void setRequireInstance(String requireInstance) {
		this.requireInstance = requireInstance;
	}

	public YANG_LeafRefSpecification(int id) {
		super(id);
	}

	public YANG_Path getPath() {
		return path;
	}

	public void setPath(YANG_Path path) {
		this.path = path;
	}

	private String requireInstance = null;

	public String getRequireInstance() {
		return requireInstance;
	}

	public String toString() {
		String result = "";
		result += path.toString();
		if (requireInstance != null)
			result += "\n" + requireInstance;
		return result;
	}

}
