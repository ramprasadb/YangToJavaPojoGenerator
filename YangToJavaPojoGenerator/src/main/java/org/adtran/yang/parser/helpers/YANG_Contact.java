package org.adtran.yang.parser.helpers;


public class YANG_Contact extends SimpleYangNode implements YANG_Meta{

    private String contact = null;

  public YANG_Contact(int id) {
    super(id);
  }

  public YANG_Contact(yang p, int id) {
    super(p, id);
  }

    public void setContact(String c){
	contact = unquote(c);
    }

    public String getContact(){
	return contact;
    }

    public String toString(){
	return "contact " + contact + ";";
    }

}
