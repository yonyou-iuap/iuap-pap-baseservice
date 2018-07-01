package com.yonyou.iuap.baseservice.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

import com.yonyou.iuap.baseservice.support.condition.Condition;
import com.yonyou.iuap.baseservice.support.generator.GeneratedValue;
import com.yonyou.iuap.baseservice.support.generator.Strategy;

/**
 * 说明：逻辑删除基础Model
 * @author Aton
 * 2018年6月12日
 */
public abstract class AbsDrModel extends AbsModel implements Model, LogicDel {

	@Id
	@GeneratedValue(strategy=Strategy.UUID, module="order")
	@Column(name="id")
	@Condition
	protected Serializable id;
	
	@Column(name="dr")
	@Condition
	protected Integer dr = 0;
	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

}