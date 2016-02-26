package org.adtran.yang.parser.helpers;

public class IoDataDef extends YANG_Body {
	
	private Io io = null;
	
	public IoDataDef(Io io){
		super(0);
		this.io = io;
	}
	
	public int getLine(){
		return io.getLine();
	}
	
	public int getCol(){
		return io.getCol();
	}
	
	public String getFileName(){
		return io.getFileName();
	}
	
	public String getBody(){
		if (io instanceof YANG_Input)
			return "input";
		else if (io instanceof YANG_Output)
			return "output";
		return null;
	}

	public void check(YangContext context) {}

}
