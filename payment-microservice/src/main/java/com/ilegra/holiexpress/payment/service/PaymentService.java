package com.ilegra.holiexpress.payment.service;

import com.ilegra.holiexpress.payment.entity.Payment;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

public interface PaymentService {

    void initializePersistence(Handler<AsyncResult<Void>> resultHandler);

    void addPayment(Payment payment, Handler<AsyncResult<Void>> resultHandler);

    void notification(JsonObject body, Handler<AsyncResult<Void>> resultHandler);

    void retrievePayment(String checkoutId, Handler<AsyncResult<Payment>> resultHandler);

    void retrievePayments(String orderId, Handler<AsyncResult<List<Payment>>> resultHandler);
}
