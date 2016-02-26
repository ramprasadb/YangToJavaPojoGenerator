package org.adtran.yang.parser.helpers;


public class YANG_Organization extends SimpleYangNode implements YANG_Meta{

    private String organization = null;

  public YANG_Organization(int id) {
    super(id);
  }

  public YANG_Organization(yang p, int id) {
    super(p, id);
  }

    public void setOrganization(String o){
	organization = unquote(o);
    }

    public String getOrganization(){
	return organization;
    }

    public String toString(){
	return "organization " + organization + ";";
    }

}
