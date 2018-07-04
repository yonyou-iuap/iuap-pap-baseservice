package com.yonyou.iuap.baseservice.entity;

/**
 * 说明：乐观锁接口
 * @author Aton
 * 2018年7月4日
 */
public interface VerLock {
	
	public String getTs();
	
	public void setTs(String ts);
	
	public String getNewTs();
	
	public void setNewTs(String ts);

}