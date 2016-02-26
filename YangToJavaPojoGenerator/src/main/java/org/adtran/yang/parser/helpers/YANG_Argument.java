package org.adtran.yang.parser.helpers;


public class YANG_Argument extends SimpleYangNode {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6256309128763772225L;
	
	private String argument = null;
    private YANG_Yin yin = null;
    private String added = "";

    private boolean bracked = false;

  public YANG_Argument(int id) {
    super(id);
  }

  public YANG_Argument(yang p, int id) {
    super(p, id);
  }

    protected void setArgument(String a){
	argument = unquote(a);
    }

    public String getArgument(){
	return argument;
    }

    public void setYin(YANG_Yin y){
	yin = y;
	bracked = true;
    }

    public YANG_Yin getYin(){
	return yin;
    }

    public boolean isBracked(){
	return bracked;
    }
    
    public void addArgument(String a){
    	added += " " + a;
    }
    

    public String toString(){
	String result = new String();
	result += "argument " + argument;
	if(isBracked())
	    result += "{\n" + yin.toString() + "}";
	else
	    result += ";";
	return result;
    }


}
