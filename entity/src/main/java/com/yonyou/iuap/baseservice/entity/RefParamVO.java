package com.yonyou.iuap.baseservice.entity;

import java.util.LinkedHashMap;

public class RefParamVO {

	private String reftype;
	private String refname;

	private String refi18n;

	private RefParamConfig refParamConfigTable;

	private LinkedHashMap<String, String> thead;

	private LinkedHashMap<String, String> theadI18n;

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

	public void setRefParamConfigTableTree(RefParamConfig refParamConfigTableTree) {
		this.refParamConfigTableTree = refParamConfigTableTree;
	}

	public String getRefi18n() {
		return refi18n;
	}

	public void setRefi18n(String refi18n) {
		this.refi18n = refi18n;
	}

	public LinkedHashMap<String, String> getTheadI18n() {
		return theadI18n;
	}

	public void setTheadI18n(LinkedHashMap<String, String> theadI18n) {
		this.theadI18n = theadI18n;
	}
	public String getRefname() {
		return refname;
	}

	public void setRefname(String refname) {
		this.refname = refname;
	}
}
