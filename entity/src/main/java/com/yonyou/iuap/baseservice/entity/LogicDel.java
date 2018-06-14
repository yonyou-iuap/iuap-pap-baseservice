package com.yonyou.iuap.baseservice.entity;

/**
 * 说明：逻辑删除Model接口
 * @author houlf
 * 2018年6月12日
 */
public interface LogicDel {
	
	public Integer DELETED = 1;
	public Integer NORMAL = 0;

	public Integer getDr();

	public void setDr(Integer dr);

}