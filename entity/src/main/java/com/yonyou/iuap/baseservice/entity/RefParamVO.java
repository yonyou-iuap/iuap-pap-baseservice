package com.yonyou.iuap.baseservice.entity;

import java.util.LinkedHashMap;

public class RefParamVO {

	private String reftype;
	private String refname;

	private RefParamConfig refParamConfigTable;

	private LinkedHashMap<String, String> thead;

	private RefParamConfig refParamConfigTableTree;


	public String getReftype() {
		return reftype;
	}

	public void setReftype(String reftype) {
		this.reftype = reftype;
	}

	public LinkedHashMap<String, String> getThead() {
		return thead;
	}
	public void setThead(LinkedHashMap<String, String> thead) {
		this.thead = thead;
	}

	public RefParamConfig getRefParamConfigTable() {
		return refParamConfigTable;
	}

	public void setRefParamConfigTable(RefParamConfig refParamConfigTable) {
		this.refParamConfigTable = refParamConfigTable;
	}

	public RefParamConfig getRefParamConfigTableTree() {
		return refParamConfigTableTree;
	}

	public String getRefname() {
		return refname;
	}

	public void setRefParamConfigTableTree(RefParamConfig refParamConfigTableTree) {
		this.refParamConfigTableTree = refParamConfigTableTree;
	}

	public void setRefname(String refname) {
		this.refname = refname;
	}
}
