package com.yonyou.iuap.baseservice.entity;

import java.util.Date;

/**
 * 说明：基础Model接口
 * @author houlf
 * 2018年6月12日
 */
public interface Model {

	public String getId();
	
	public void setId(String id);
	
	public Integer getVersion();
	
	public void setVersion(Integer version);
	
	public Date getCreateTime();
	
	public void setCreateTime(Date createTime);
	
	public String getCreateUser();
	
	public void setCreateUser(String createUser);
	
	public Date getLastModified();
	
	public void setLastModified(Date lastModified);
	
	public String getLastModifyUser();
	
	public void setLastModifyUser(String lastModifyUser);

}