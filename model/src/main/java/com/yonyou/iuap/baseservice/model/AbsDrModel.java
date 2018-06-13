package com.yonyou.iuap.baseservice.model;

import java.util.Date;

import javax.persistence.Column;

/**
 * 说明：逻辑删除基础Model
 * @author houlf
 * 2018年6月12日
 */
public abstract class AbsDrModel extends AbsModel implements LogicDel {

	@Column(name="dr")
	protected Integer dr;

	@Column(name="ts")
	protected Date ts;

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public Date getTs() {
		return ts;
	}

	public void setTs(Date ts) {
		this.ts = ts;
	}

}