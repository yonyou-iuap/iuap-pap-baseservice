package com.yonyou.iuap.baseservice;

import com.yonyou.iuap.baseservice.entity.SanyOrder;
import com.yonyou.iuap.baseservice.service.SanyOrderService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:t-app-persistence.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocalTest {
    @Autowired
    SanyOrderService service;
    private static SanyOrder entity;
    static {
        entity = new SanyOrder();
        entity.setId(UUID.randomUUID().toString());
        entity.setVoucherDate(new Date());
        entity.setTs(new Date().getTime()+"");
        entity.setOrderCode("oc-ipb");
        entity.setOrderName("name-ipb");
        entity.setSupplier("sp-ipb");
    }


    @Test
    public void doTest() {

        List<SanyOrder> result = service.queryList("order_code", "code10");
        for (SanyOrder so : result) {
            System.out.println("=======>" + so.toString());
        }
        Assert.assertNotNull(result);

    }

    @Test
    public void doTest1Insert() {
        SanyOrder saved = service.insert(entity);
        Assert.assertNotNull(saved);
    }


    @Test
    public void doTest2Update() {
        entity.setRemark("updated" + Thread.currentThread().getId());
        SanyOrder saved = service.update(entity);
        Assert.assertNotNull(saved);
    }

    @Test
    public void doTest3Delete() {

        int deleted = service.delete(entity);
        Assert.assertEquals(1, deleted);
    }
}

