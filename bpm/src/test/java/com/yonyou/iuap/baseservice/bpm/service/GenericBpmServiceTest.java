package com.yonyou.iuap.baseservice.bpm.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yonyou.iuap.baseservice.bpm.entity.BpmModel;
import com.yonyou.iuap.baseservice.bpm.model.MockModel;
import com.yonyou.iuap.context.InvocationInfoProxy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import yonyou.bpm.rest.BpmRest;
import yonyou.bpm.rest.exception.RestException;
import yonyou.bpm.rest.response.historic.HistoricTaskInstanceResponse;

import java.util.Date;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:t-app-persistence.xml","classpath*:t-app.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GenericBpmServiceTest {
    @Autowired
    MockBpmService service;
    static MockModel entity = new MockModel();

    @Before
    public void init(){
        InvocationInfoProxy.setUserid("1");//超级管理员
 }

    @Test
    public void bpmRestServices() throws Exception {
        BpmRest rest = service.bpmRestServices("1");//超级管理员
        Assert.assertNotNull(rest);
    }

    /**
     * 启动流程
     * @throws RestException
     */
    @Test
    public void do1() throws RestException {
        entity.setId(null);//模拟新建业务实体,不为空则为更新实体
        entity.setProcessDefineCode("eiap844133");
        entity.setSupplier("mock-sp");
        entity.setSupplierName("mock-sp-name");
        entity.setType(1);
        entity.setTs(new Date().getTime()+"");
        ObjectNode result = (ObjectNode) service.doStartProcess(entity);
        System.out.println(result.toString());
        entity.setProcessInstanceId(result.get("id").toString().replace("\"",""));
        Assert.assertNotNull(result);
    }

    /**
     * 执行提交
     * @throws RestException
     */
    @Test
    public void do2() throws RestException {
//        entity.setId(null);//模拟新建业务实体,不为空则为更新实体
        HistoricTaskInstanceResponse task = service
                .getInstanceNotFinishFirstTask(InvocationInfoProxy.getUserid(),
                        entity.getProcessInstanceId());
        entity.setTaskId(task.getId());
        entity.setTaskKey(task.getTaskDefinitionKey());
        Object result =service.doSubmit(entity,"approval comment XXXX");
        Assert.assertEquals("expecting true from service.doSubmit:",true,result);
    }

    /**
     * 执行撤回
     */
    @Test
    public void do3() throws RestException {
//        HistoricTaskInstanceResponse task = service
//                .getInstanceNotFinishFirstTask(InvocationInfoProxy.getUserid(),
//                        entity.getProcessInstanceId());
//        entity.setTaskId(task.getId());
//        entity.setTaskId("b5702878-5f32-11e8-a24a-40a3cc7964c2");//取自act_ru_task表,已完成的taskID
//        entity.setTaskKey("ApproveUserTask2");                  //取自act_ru_task表,已完成的taskKey
//        entity.setProcessInstanceId("61767730-5f31-11e8-a24a-40a3cc7964c2"); //task所在流程实例ID,用于调试,非必须参数
        boolean result =service.doRevoke(entity);
        Assert.assertEquals("expecting true from service.doRevoke:",true,result);
    }

    /**
     * 执行审批
     */
    @Test
    public void do4() throws RestException {
        HistoricTaskInstanceResponse task = service
                .getInstanceNotFinishFirstTask(InvocationInfoProxy.getUserid(),
                        entity.getProcessInstanceId());
        entity.setTaskId(task.getId());
        entity.setTaskKey(task.getTaskDefinitionKey());
        service.save(entity);
//        service.setMockTaskID(entity.getTaskId());//取自act_ru_task表
        Object result =service.doApprove(entity.getId(),true,"approval comment XXX");
        Assert.assertEquals("expecting true from service.doApprove:",true,result);


    }

    /**
     * 执行驳回
     */
//    @Test
    public void do5() throws RestException {
//        entity.setId(null);//模拟新建业务实体,不为空则为更新实体
        HistoricTaskInstanceResponse task = service
                .getInstanceNotFinishFirstTask(InvocationInfoProxy.getUserid(),
                        entity.getProcessInstanceId());
        entity.setTaskId(task.getId());
//        entity.setTaskKey("ApproveUserTask2");                  //取自act_hi_taskinst表,驳回的目标taskKey
        ObjectNode result = (ObjectNode) service.doReject(entity, "approval comment XXX");
        Assert.assertEquals("expecting ProcessInstanceId from service.doReject:",entity.getProcessInstanceId(),result.get("id").toString().replace("\"",""));
//        System.out.println("======>resut:"+ JSON.toJSONString(result));
    }

    /**
     * 执行流程终止
     */
    @Test
    public void do6() {
        Object result =service.doSuspendProcess(entity.getId());
        Assert.assertNotNull(result);


    }


}