package org.adtran.yang.parser.helpers;


public class YANG_ErrorAppt extends SimpleYangNode {

	private String errorappt = null;

	public YANG_ErrorAppt(int id) {
		super(id);
	}

	public YANG_ErrorAppt(yang p, int id) {
		super(p, id);
	}

	public void setErrorAppt(String e) {
		errorappt = unquote(e);
	}

	public String getErrorAppt() {
	  return errorappt;
  }
	
	public String toString(){
		return "error-appt-tag " + errorappt + ";";
	}
}
