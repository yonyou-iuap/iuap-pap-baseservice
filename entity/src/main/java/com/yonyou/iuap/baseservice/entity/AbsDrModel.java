package com.yonyou.iuap.baseservice.entity;

import javax.persistence.Column;

/**
 * 说明：逻辑删除基础Model
 * @author houlf
 * 2018年6月12日
 */
public abstract class AbsDrModel extends AbsModel implements LogicDel {

	@Column(name="dr")
	protected Integer dr;

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

}