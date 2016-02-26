
package org.adtran.yang.parser.helpers;


public class YANG_DeviateNotSupported extends SimpleYangNode {
  public YANG_DeviateNotSupported(int id) {
    super(id);
  }

  public YANG_DeviateNotSupported(yang p, int id) {
    super(p, id);
  }
  
  public String toString() {
	  String result = "";
	  result += "deviate not-supported";
	  result += ";";
	  return result;
  }

}
