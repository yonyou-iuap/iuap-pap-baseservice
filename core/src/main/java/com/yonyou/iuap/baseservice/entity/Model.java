package com.yonyou.iuap.baseservice.entity;

import java.io.Serializable;

/**
 * 说明：基础Model接口
 * @author houlf
 * 2018年6月12日
 */
public interface Model {
    String DEFALT_DF="yyyy-MM-dd HH:mm:ss SSS";

	public Serializable getId();
	
	public void setId(Serializable id);
	
	public String getCreateTime();
	
	public void setCreateTime(String createTime);
	
	public String getCreateUser();
	
	public void setCreateUser(String createUser);
	
	public String getLastModified();
	
	public void setLastModified(String lastModified);
	
	public String getLastModifyUser();
	
	public void setLastModifyUser(String lastModifyUser);
	
	public String getTs();
	
	public void setTs(String ts);

}