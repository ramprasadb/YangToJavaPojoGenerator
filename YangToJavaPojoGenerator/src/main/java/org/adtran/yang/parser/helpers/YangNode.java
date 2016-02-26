package org.adtran.yang.parser.helpers;

import java.util.Vector;

public interface YangNode extends Node {
	public void addUnknown(YANG_Unknown u);
	//public void addUnknowns(Vector<YANG_Unknown> uns);
	public Vector<YANG_Unknown> getUnknowns();
    /** Return the line of the node **/
    public int getLine();

    /** Return the column of the node **/
    public int getCol();
    
    public String getFileName();
    
    public void setFileName(String f);
    
    public void setLabel(String s);
    
    public String getLabel();


}
