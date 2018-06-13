package com.yonyou.iuap.baseservice.model;

import java.util.Date;

/**
 * 说明：逻辑删除Model接口
 * @author houlf
 * 2018年6月12日
 */
public interface DeleteModel extends Model{

	public Integer getDr();

	public void setDr(Integer dr);
	
	public Date getTs();
	
	public void setTs(Date ts);

}