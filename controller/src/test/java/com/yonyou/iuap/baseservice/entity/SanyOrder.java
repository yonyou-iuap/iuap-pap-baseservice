package com.yonyou.iuap.baseservice.entity;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.annotation.Condition;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Match;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;


@Table(name = "example_sany_order")
public class SanyOrder extends AbsDrModel {

	@Condition(match=Match.GT)
	@Column(name="order_code")
	private String orderCode;

	@Condition(match=Match.LIKE)
	private String orderName;
	
	private String supplier;
	private String supplierName;
	private Integer type;
	private String purchasing;
	private String purchasingGroup;
	private Date voucherDate;
	private Integer approvalState;
	private Integer confirmState;
	private Integer closeState;
	private String remark;
	
	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getPurchasing() {
		return purchasing;
	}

	public void setPurchasing(String purchasing) {
		this.purchasing = purchasing;
	}

	public String getPurchasingGroup() {
		return purchasingGroup;
	}

	public void setPurchasingGroup(String purchasingGroup) {
		this.purchasingGroup = purchasingGroup;
	}

	public Date getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(Date voucherDate) {
		this.voucherDate = voucherDate;
	}

	public Integer getApprovalState() {
		return approvalState;
	}

	public void setApprovalState(Integer approvalState) {
		this.approvalState = approvalState;
	}

	public Integer getConfirmState() {
		return confirmState;
	}

	public void setConfirmState(Integer confirmState) {
		this.confirmState = confirmState;
	}

	public Integer getCloseState() {
		return closeState;
	}

	public void setCloseState(Integer closeState) {
		this.closeState = closeState;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "SanyOrder{" +
				"orderCode='" + orderCode + '\'' +
				", orderName='" + orderName + '\'' +
				", supplier='" + supplier + '\'' +
				", supplierName='" + supplierName + '\'' +
				", type=" + type +
				", purchasing='" + purchasing + '\'' +
				", purchasingGroup='" + purchasingGroup + '\'' +
				", voucherDate=" + voucherDate +
				", approvalState=" + approvalState +
				", confirmState=" + confirmState +
				", closeState=" + closeState +
				", remark='" + remark + '\'' +
				", dr=" + dr +
				", id='" + id + '\'' +
				", createTime='" + createTime + '\'' +
				", createUser='" + createUser + '\'' +
				", lastModified='" + lastModified + '\'' +
				", lastModifyUser='" + lastModifyUser + '\'' +
				", ts='" + ts + '\'' +
				'}';
	}
}