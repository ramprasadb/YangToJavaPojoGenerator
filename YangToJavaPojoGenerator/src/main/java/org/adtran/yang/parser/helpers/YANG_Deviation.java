package org.adtran.yang.parser.helpers;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Vector;

public class YANG_Deviation extends YANG_Body {

	private String deviation = null;
	private YANG_DeviateNotSupported deviateNotSupported = null;

	private Vector<YANG_DeviateAdd> deviateadds = new Vector<YANG_DeviateAdd>();
	private Vector<YANG_DeviateDelete> deviatedeletes = new Vector<YANG_DeviateDelete>();
	private Vector<YANG_DeviateReplace> deviatereplaces = new Vector<YANG_DeviateReplace>();

	private boolean b_deviatenotsupported = false;

	public String getDeviation() {
		return deviation;
	}

	public void setDeviation(String deviation) {
		this.deviation = unquote(deviation);
	}

	public YANG_Deviation(int id) {
		super(id);
	}

	public YANG_Deviation(yang p, int id) {
		super(p, id);
	}


	public String getBody() {
		return getDeviation();
	}

	public YANG_DeviateNotSupported getDeviateNotSupported() {
		return deviateNotSupported;
	}

	public void setDeviateNotSupported(YANG_DeviateNotSupported d) {
		if (!b_deviatenotsupported) {
			this.deviateNotSupported = d;
			b_deviatenotsupported = true;
		} else
			YangErrorManager.addError(filename, d.getLine(), d.getCol(), "unex_kw",
					"deviate-not-supported");
	}

	public void addDeviateAdd(YANG_DeviateAdd da) {
		deviateadds.add(da);
	}

	public Vector<YANG_DeviateAdd> getDeviateAdds() {
		return deviateadds;
	}

	public void addDeviateDelete(YANG_DeviateDelete dd) {
		deviatedeletes.add(dd);
	}

	public Vector<YANG_DeviateDelete> getDeviateDeletes() {
		return deviatedeletes;
	}

	public void addDeviateReplace(YANG_DeviateReplace dr) {
		deviatereplaces.add(dr);
	}

	public Vector<YANG_DeviateReplace> getDeviateReplaces() {
		return deviatereplaces;
	}

	public String toString() {
		String result = "";
		if (b_deviatenotsupported)
			result += deviateNotSupported.toString() + "\n";
		for (Enumeration<YANG_DeviateAdd> eda = deviateadds.elements(); eda
				.hasMoreElements();)
			result += eda.nextElement().toString() + "\n";
		for (Enumeration<YANG_DeviateDelete> eda = deviatedeletes.elements(); eda
				.hasMoreElements();)
			result += eda.nextElement().toString() + "\n";
		for (Enumeration<YANG_DeviateReplace> eda = deviatereplaces.elements(); eda
				.hasMoreElements();)
			result += eda.nextElement().toString() + "\n";
		return result;
	}

	@Override
	public void check(YangContext context) {}

}
