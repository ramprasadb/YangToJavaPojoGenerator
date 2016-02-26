package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;

public class YANG_AnyXml extends MustDataDef implements YANG_ShortCase {

	private String anyxml = null;
	private YANG_Mandatory mandatory = null;
	private boolean b_mandatory = false;

	protected YANG_AnyXml(int id) {
		super(id);
	}

	protected YANG_AnyXml(yang p, int id) {
		super(p, id);
	}

	protected void setAnyXml(String a) {
		anyxml = unquote(a);
	}

	public String getBody() {
		return getAnyXml();
	}

	/**
	 * Get the name of this any-xml statement
	 * 
	 * @return the name of the statement
	 */
	public String getAnyXml() {
		return anyxml;
	}

	protected void setMandatory(YANG_Mandatory m) {
		if (!b_mandatory) {
			b_mandatory = true;
			mandatory = m;
		} else
			YangErrorManager.addError(filename, m.getLine(), m.getCol(),
					"unex_kw", "mandatory");
	}

	/**
	 * Get the mandatory statement of this any-xml statement
	 * 
	 * @return a mandatory object or null if not mandatory
	 */
	public YANG_Mandatory getMandatory() {
		return mandatory;
	}

	public boolean isBracked() {
		return super.isBracked() || b_mandatory;
	}

	public void check(YangContext context) {
		super.check(context);

	}

	protected void deleteMandatory() {
		mandatory = null;
		b_mandatory = false;
	}

	public String toString() {
		String result = new String();
		result += "anyxml " + anyxml;
		if (isBracked()) {
			result += " {\n";
			if (b_mandatory)
				result += mandatory.toString() + "\n";
			result += super.toString() + "\n";
			result += "\n}";
		} else
			result += ";";

		return result;
	}

	public YANG_AnyXml clone() {
		YANG_AnyXml cl = new YANG_AnyXml(parser, id);
		cl.setAnyXml(getAnyXml());
		cl.setFileName(getFileName());
		cl.setCol(getCol());
		cl.setLine(getLine());
		cl.setContext(getContext());
		if (getConfig() != null)
			cl.setConfig(getConfig());
		if (getDescription() != null)
			cl.setDescription(getDescription());
		if (getMandatory() != null)
			cl.setMandatory(getMandatory());
		cl.setIfFeature(getIfFeatures());
		cl.setMusts(getMusts());
		cl.setUnknowns(getUnknowns());
		if (getReference() != null)
			cl.setReference(getReference());
		if (getStatus() != null)
			cl.setStatus(getStatus());
		if (getWhen() != null)
			cl.setWhen(getWhen());
		return cl;
	}

	protected void refines(YANG_RefineAnyXml rl) {
		if (rl.getConfig() != null)
			config = rl.getConfig();
		if (rl.getDescription() != null)
			description = rl.getDescription();
		if (rl.getMandatory() != null)
			mandatory = rl.getMandatory();
		if (rl.getReference() != null)
			reference = rl.getReference();
		for (YANG_Must must : rl.getMusts())
			addMust(must);
	}

}
