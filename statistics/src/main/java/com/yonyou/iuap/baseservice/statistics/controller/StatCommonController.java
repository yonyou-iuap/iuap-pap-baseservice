package com.yonyou.iuap.baseservice.statistics.controller;

import com.yonyou.iuap.base.web.BaseController;
import com.yonyou.iuap.baseservice.persistence.support.PageRequestAndSearchParams;
import com.yonyou.iuap.baseservice.statistics.service.StatCommonService;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/base/statistics")
public class StatCommonController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(StatCommonController.class);

    @Autowired
    private StatCommonService service;


    @RequestMapping(value = {"/page/{modelCode}"}, method = {RequestMethod.POST})
    @ResponseBody
    public Object page(@PathVariable String modelCode, PageRequest pageRequest,@RequestBody SearchParams searchParams) {

        Page  page = this.service.selectAllByPage(pageRequest, searchParams,modelCode);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", page);
        return this.buildMapSuccess(map);
    }

    @RequestMapping(value = "/list/{modelCode}" ,method = RequestMethod.POST )
    @ResponseBody
    public Object list( @PathVariable String modelCode, PageRequest pageRequest,@RequestBody SearchParams searchParams) {
        List list = this.service.findAll(pageRequest, searchParams,modelCode);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", list);
        return this.buildMapSuccess(map);
    }
}
