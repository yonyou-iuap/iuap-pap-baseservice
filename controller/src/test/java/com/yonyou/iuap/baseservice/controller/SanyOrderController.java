package com.yonyou.iuap.baseservice.controller;

import com.yonyou.iuap.baseservice.entity.SanyOrder;
import com.yonyou.iuap.baseservice.service.SanyOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/example_sany_order")
public class SanyOrderController extends GenericController<SanyOrder>{

	
	/*******************************************/
	private SanyOrderService sanyOrderService;

	@Autowired
	public void setSanyOrderService(SanyOrderService sanyOrderService) {
		this.sanyOrderService = sanyOrderService;
		super.setService(sanyOrderService);
	}
	
}