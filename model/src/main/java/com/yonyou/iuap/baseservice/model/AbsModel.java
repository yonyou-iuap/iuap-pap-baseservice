package com.yonyou.iuap.baseservice.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Version;

/**
 * 说明：基础Model
 * @author houlf
 * 2018年6月12日
 */
public class AbsModel implements Model {

	@Column(name="id")
	protected String id;

	@Column(name="version")
	@Version
	protected Integer version;

	@Column(name="createTime")
	protected Date createTime;
	
	@Column(name="createUser")
	protected String createUser;
	
	@Column(name="lastModified")
	protected Date lastModified;
	
	@Column(name="lastModifyUser")
	protected String lastModifyUser;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getLastModifyUser() {
		return lastModifyUser;
	}

	public void setLastModifyUser(String lastModifyUser) {
		this.lastModifyUser = lastModifyUser;
	}

}