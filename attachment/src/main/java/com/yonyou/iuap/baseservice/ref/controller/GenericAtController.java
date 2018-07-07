package com.yonyou.iuap.baseservice.ref.controller;

import com.yonyou.iuap.base.web.BaseController;
import com.yonyou.iuap.baseservice.ref.entity.Attachmentable;
import com.yonyou.iuap.baseservice.ref.service.GenericAtService;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 附件特性基类
 * @author leon
 * @param <T>
 */

public class GenericAtController<T extends Attachmentable> extends BaseController {
    private Logger logger = LoggerFactory.getLogger(GenericAtController.class);

    protected GenericAtService service;

    public void setAtService(GenericAtService service) {
        this.service = service;
    }
    @RequestMapping(value = "/saveWithAttach", method = RequestMethod.POST)
    @ResponseBody
    public Object saveWithAttach(@RequestBody T entity ){
        service.saveWithAttachment(entity);
        return this.buildSuccess(entity);
    }

    @RequestMapping(value = "/getListWithAttach", method = RequestMethod.GET)
    @ResponseBody
    public Object getListWithAttach(PageRequest pageRequest,
                             SearchParams searchParams){
        Page<T> page = service.getListWithAttach(pageRequest,searchParams);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", page);
        return this.buildMapSuccess(map);
    }

}
