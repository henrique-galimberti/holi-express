package com.ilegra.holiexpress.order.service;

import com.ilegra.holiexpress.order.entity.Order;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

public interface OrderService {

    void initializePersistence(Handler<AsyncResult<Void>> resultHandler);

    void addOrder(Order order, Handler<AsyncResult<Void>> resultHandler);

    void notification(JsonObject notification, Handler<AsyncResult<Void>> resultHandler);

    void retrieveOrder(String orderId, Handler<AsyncResult<Order>> resultHandler);

    void retrieveOrders(String buyerId, Handler<AsyncResult<List<Order>>> resultHandler);
}
