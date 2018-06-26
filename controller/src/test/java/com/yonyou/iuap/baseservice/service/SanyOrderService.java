package com.yonyou.iuap.baseservice.service;

import com.yonyou.iuap.baseservice.dao.SanyOrderMapper;
import com.yonyou.iuap.baseservice.entity.SanyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SanyOrderService extends GenericService<SanyOrder>{

//	@BusiLogConfig(busiName="orderQueryList", method="测试方法")
    public List<SanyOrder> queryList(String name, Object value){
		return super.queryList(name, value);
	}
	
	/********************************************/
	private SanyOrderMapper sanyOrderMapper;

	@Autowired
	public void setSanyOrderMapper(SanyOrderMapper sanyOrderMapper) {
		this.sanyOrderMapper = sanyOrderMapper;
		super.setGenericMapper(sanyOrderMapper);
	}

}