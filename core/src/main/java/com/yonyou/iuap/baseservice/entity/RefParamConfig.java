package com.yonyou.iuap.baseservice.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RefParamConfig {
	private String              tableName;
	private String              id;
	private String              refpk;
	private String              refcode;
	private String              refname;
	private String              fid;
	private String              pid;
	private List<String>        extension=new ArrayList<>();

	private List<String>             condition=new ArrayList<>();

	private Map<String,String> filters=new HashMap<>();


	private String        sort;
	private String        order;
	private String dataPermission;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRefpk() {
		return refpk;
	}

	public void setRefpk(String refpk) {
		this.refpk = refpk;
	}

	public String getRefcode() {
		return refcode;
	}

	public void setRefcode(String refcode) {
		this.refcode = refcode;
	}

	public String getRefname() {
		return refname;
	}

	public void setRefname(String refname) {
		this.refname = refname;
	}

	public List<String> getExtension() {
		return extension;
	}

	public void setExtension(List<String> extension) {
		this.extension = extension;
	}



	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public List<String> getCondition() {
		return condition;
	}

	public void setCondition(List<String> condition) {
		this.condition = condition;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, String> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, String> filters) {
		this.filters = filters;
	}

	public String getDataPermission() {
		return dataPermission;
	}

	public void setDataPermission(String dataPermission) {
		this.dataPermission = dataPermission;
	}
}
