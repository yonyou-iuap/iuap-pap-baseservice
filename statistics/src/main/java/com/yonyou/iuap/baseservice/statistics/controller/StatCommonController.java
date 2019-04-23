package com.yonyou.iuap.baseservice.statistics.controller;

import com.yonyou.iuap.baseservice.controller.util.BaseController;
import com.yonyou.iuap.baseservice.statistics.service.StatCommonService;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/base/stats")
public class StatCommonController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(StatCommonController.class);

    @Autowired
    private StatCommonService service;


    @RequestMapping(value = {"/page/{modelCode}"}, method = {RequestMethod.POST})
    @ResponseBody
    public Object page(@PathVariable String modelCode, PageRequest pageRequest,@RequestBody Map<String,Object> searchMap) {
        logger.debug("StatCommonController receiving searchParams:"+searchMap);
        SearchParams searchParams = new SearchParams();
        searchParams.setSearchMap(searchMap);
        Page  page = this.service.selectAllByPage(pageRequest, searchParams,modelCode);
        return this.buildSuccess(page);
    }

    @RequestMapping(value = "/list/{modelCode}" ,method = RequestMethod.POST )
    @ResponseBody
    public Object list( @PathVariable String modelCode,@RequestBody Map<String,Object> searchMap) {
        logger.debug("StatCommonController receiving searchParams:"+searchMap);
        SearchParams searchParams = new SearchParams();
        searchParams.setSearchMap(searchMap);
        List list = this.service.findAll( searchParams,modelCode);
        return this.buildSuccess(list);
    }

    @RequestMapping(value = "/fields/{modelCode}" ,method = RequestMethod.POST )
    @ResponseBody
    public Object fields( @PathVariable String modelCode, PageRequest pageRequest,@RequestBody Map<String,Object> searchMap) {
        logger.debug("StatCommonController receiving searchParams:"+searchMap);
        SearchParams searchParams = new SearchParams();
        searchParams.setSearchMap(searchMap);
        Page<Map> page = this.service.selectFieldsByPage(pageRequest, searchParams, modelCode);
        return this.buildSuccess(page);
    }

    @RequestMapping(value = "/distinct/{modelCode}" ,method = RequestMethod.POST )
    @ResponseBody
    public Object distinct( @PathVariable String modelCode,@RequestBody Map<String,Object> searchMap) {
        logger.debug("StatCommonController receiving searchParams:"+searchMap);
        SearchParams searchParams = new SearchParams();
        searchParams.setSearchMap(searchMap);
        List list = this.service.findDistinct ( searchParams,modelCode);
        return this.buildSuccess(list);
    }
}
