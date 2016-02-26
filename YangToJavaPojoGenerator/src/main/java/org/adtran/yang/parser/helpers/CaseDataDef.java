package org.adtran.yang.parser.helpers;

import java.util.Vector;

public class CaseDataDef extends YANG_DataDef implements DataDefsContainer{
	
	YANG_Case ycase;

	public CaseDataDef(int id) {
		super(id);
	}

	public CaseDataDef(yang p, int id) {
		super(p, id);
	}
	
	public CaseDataDef(YANG_Case c){
		super(0);
		ycase = c;
		setLine(c.getLine());
		setCol(c.getCol());
		setFileName(c.getFileName());
		setDescription(c.getDescription());
		setReference(c.getReference());
		setUnknowns(c.getUnknowns());
		setIfFeature(c.getIfFeatures());
		setWhen(c.getWhen());
		setStatus(c.getStatus());
	}

	public YANG_Case getCase() {
		return ycase;
	}
	
	public void addDataDef(YANG_DataDef c) {
		ycase.getDataDefs().add(c);
	}

	public Vector<YANG_DataDef> getDataDefs() {
		return ycase.getDataDefs();
	}



	@Override
	public String getBody() {
		return ycase.getBody();
	}
	
	public CaseDataDef clone(){
		CaseDataDef cddef = new CaseDataDef(getCase());
//		cddef.setFileName(getFileName());
//		cddef.setLine(getLine());
//		cddef.setCol(getCol());
//		cddef.setDescription(getDescription());
//		cddef.setStatus(getStatus());
//		cddef.setWhen(getWhen());
//		cddef.setIfFeature(getIfFeatures());
//		cddef.setUnknowns(getUnknowns());
		return cddef;
	}
	
	public String toString(){
		return ycase.toString();
	}

}
