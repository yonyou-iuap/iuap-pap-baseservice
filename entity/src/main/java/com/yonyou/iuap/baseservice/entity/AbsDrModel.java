package com.yonyou.iuap.baseservice.entity;

import javax.persistence.Column;
import com.yonyou.iuap.baseservice.support.condition.Condition;
import com.yonyou.iuap.baseservice.support.condition.Match;

/**
 * 说明：逻辑删除基础Model
 * @author Aton
 * 2018年6月12日
 */
public abstract class AbsDrModel extends AbsModel implements Model, LogicDel {

	@Column(name="dr")
	@Condition(match=Match.EQ)
	protected Integer dr = 0;

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

}