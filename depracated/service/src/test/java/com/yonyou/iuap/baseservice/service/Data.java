package com.yonyou.iuap.baseservice.service;

import java.math.BigDecimal;
import java.util.Date;

public class Data {
	
	private String id;
	private String code;
	private String name;
	private Date createDate;
	private int intNum;
	private float fltNum;
	private BigDecimal bdmNum;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public int getIntNum() {
		return intNum;
	}
	public void setIntNum(int intNum) {
		this.intNum = intNum;
	}
	public float getFltNum() {
		return fltNum;
	}
	public void setFltNum(float fltNum) {
		this.fltNum = fltNum;
	}
	public BigDecimal getBdmNum() {
		return bdmNum;
	}
	public void setBdmNum(BigDecimal bdmNum) {
		this.bdmNum = bdmNum;
	}
	
}