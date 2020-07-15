package com.ilegra.holiexpress.order.entity;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
public class OrderTest {

    @Test
    public void gettersAndSetters(TestContext testContext) {
        Order order = new Order();

        order.setId(6);
        testContext.assertEquals(6, order.getId());

        order.setBuyerId(66);
        testContext.assertEquals(66, order.getBuyerId());

        order.setProductId(666);
        testContext.assertEquals(666, order.getProductId());

        order.setStatus(Order.STATUS_FINISHED);
        testContext.assertEquals(Order.STATUS_FINISHED, order.getStatus());

        order.setValue(666.666);
        testContext.assertEquals(666.666, order.getValue());
    }

    @Test
    public void fromJson(TestContext testContext) {
        JsonObject json = new JsonObject()
                .put("id", 6)
                .put("buyerId", 66)
                .put("productId", 666)
                .put("status", Order.STATUS_FINISHED)
                .put("value", 666.666);

        Order order = new Order(json);

        testContext.assertEquals(order.getId(), json.getInteger("id"));
        testContext.assertEquals(order.getBuyerId(), json.getInteger("buyerId"));
        testContext.assertEquals(order.getProductId(), json.getInteger("productId"));
        testContext.assertEquals(order.getStatus(), json.getString("status"));
        testContext.assertEquals(order.getValue(), json.getDouble("value"));
    }

    @Test
    public void toJson(TestContext testContext) {
        Order order = new Order();

        order.setId(6);
        order.setBuyerId(66);
        order.setProductId(666);
        order.setStatus(Order.STATUS_FINISHED);
        order.setValue(666.666);

        JsonObject json = order.toJson();

        testContext.assertEquals(order.getId(), json.getInteger("id"));
        testContext.assertEquals(order.getBuyerId(), json.getInteger("buyerId"));
        testContext.assertEquals(order.getProductId(), json.getInteger("productId"));
        testContext.assertEquals(order.getStatus(), json.getString("status"));
        testContext.assertEquals(order.getValue(), json.getDouble("value"));
    }
}
