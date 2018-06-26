package com.yonyou.iuap.baseservice;

import com.yonyou.iuap.baseservice.entity.SanyOrder;
import com.yonyou.iuap.baseservice.service.SanyOrderService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:t-app-persistence.xml"})
public class LocalTest {
    @Autowired
    SanyOrderService service;

    @Test
    public void doTest() {

        List<SanyOrder> result = service.queryList("order_code", "code10");
        for (SanyOrder so:result){
            System.out.println("=======>"+so.toString());
        }
        Assert.assertNotNull(result);

    }

    @Test
    public void doTestInsert(){

        SanyOrder entity = new SanyOrder();
        entity.setId(UUID.randomUUID().toString());
        entity.setOrderCode("oc-ipb");
        entity.setOrderName("name-ipb");
        entity.setSupplier("sp-ipb");
        entity.setVoucherDate(new Date());
        SanyOrder saved = service.insert(entity);
        Assert.assertNotNull(saved);
    }
}
