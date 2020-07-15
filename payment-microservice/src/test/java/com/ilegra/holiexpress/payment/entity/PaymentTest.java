package com.ilegra.holiexpress.payment.entity;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
public class PaymentTest {

    @Test
    public void gettersAndSetters(TestContext testContext) {
        Payment payment = new Payment();

        payment.setTransactionId("test");
        testContext.assertEquals("test", payment.getTransactionId());

        payment.setOrderId(6);
        testContext.assertEquals(6, payment.getOrderId());

        payment.setStatus(Payment.STATUS_ERROR);
        testContext.assertEquals(Payment.STATUS_ERROR, payment.getStatus());
    }

    @Test
    public void fromJson(TestContext testContext) {
        JsonObject json = new JsonObject()
                .put("transactionId", "test")
                .put("orderId", 66)
                .put("status", Payment.STATUS_FINISHED);

        Payment payment = new Payment(json);

        testContext.assertEquals(payment.getTransactionId(), json.getString("transactionId"));
        testContext.assertEquals(payment.getOrderId(), json.getInteger("orderId"));
        testContext.assertEquals(payment.getStatus(), json.getString("status"));
    }

    @Test
    public void toJson(TestContext testContext) {
        Payment payment = new Payment();

        payment.setTransactionId("test");
        payment.setOrderId(6);
        payment.setStatus(Payment.STATUS_ERROR);

        JsonObject json = payment.toJson();

        testContext.assertEquals(payment.getTransactionId(), json.getString("transactionId"));
        testContext.assertEquals(payment.getOrderId(), json.getInteger("orderId"));
        testContext.assertEquals(payment.getStatus(), json.getString("status"));
    }
}
