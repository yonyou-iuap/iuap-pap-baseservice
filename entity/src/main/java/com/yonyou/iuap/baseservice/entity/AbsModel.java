package com.yonyou.iuap.baseservice.entity;

import javax.persistence.Column;

/**
 * 说明：基础Model
 * @author houlf
 * 2018年6月12日
 */
public class AbsModel implements Model {

	@Column(name="id")
	protected String id;

	@Column(name="createTime")
	protected String createTime;
	
	@Column(name="createUser")
	protected String createUser;
	
	@Column(name="lastModified")
	protected String lastModified;
	
	@Column(name="lastModifyUser")
	protected String lastModifyUser;
	
	@Column(name="ts")
	protected String ts;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getLastModifyUser() {
		return lastModifyUser;
	}

	public void setLastModifyUser(String lastModifyUser) {
		this.lastModifyUser = lastModifyUser;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

}