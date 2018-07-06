package com.yonyou.iuap.baseservice.entity;

import java.util.List;
import java.util.Map;

public class RefParamVO {

	private Map<String, String> showcol;
	private Map<String, String> condition;
	private String idfield;
	private String pidfield;
	private String codefield;
	private String namefield;
	private String tablename;
	private List<String> extcol;
	
	public Map<String, String> getShowcol() {
		return showcol;
	}
	public void setShowcol(Map<String, String> showcol) {
		this.showcol = showcol;
	}
	public Map<String, String> getCondition() {
		return condition;
	}
	public void setCondition(Map<String, String> condition) {
		this.condition = condition;
	}
	public String getIdfield() {
		return idfield;
	}
	public void setIdfield(String idfield) {
		this.idfield = idfield;
	}
	public String getPidfield() {
		return pidfield;
	}
	public void setPidfield(String pidfield) {
		this.pidfield = pidfield;
	}
	public String getCodefield() {
		return codefield;
	}
	public void setCodefield(String codefield) {
		this.codefield = codefield;
	}
	public String getNamefield() {
		return namefield;
	}
	public void setNamefield(String namefield) {
		this.namefield = namefield;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public List<String> getExtcol() {
		return extcol;
	}
	public void setExtcol(List<String> extcol) {
		this.extcol = extcol;
	}
	
}
