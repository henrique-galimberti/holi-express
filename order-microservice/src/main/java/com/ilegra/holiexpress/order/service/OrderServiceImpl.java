package com.ilegra.holiexpress.order.service;

import com.ilegra.holiexpress.common.service.BaseJdbcService;
import com.ilegra.holiexpress.order.entity.Order;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class OrderServiceImpl extends BaseJdbcService implements OrderService {
    private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS order (\n" +
            "  id SERIAL,\n" +
            "  \"buyerId\" int NOT NULL,\n" +
            "  \"productId\" int NOT NULL,\n" +
            "  PRIMARY KEY (id) )";
    private static final String INSERT_STATEMENT = "INSERT INTO order (\"buyerId\", \"productId\") VALUES (?, ?)";
    private static final String FETCH_STATEMENT = "SELECT * FROM order WHERE id = ?";
    private static final String FETCH_MANY_STATEMENT = "SELECT * FROM order where \"buyerId\" = ?";
    private static final String DELETE_STATEMENT = "DELETE FROM order WHERE id = ?";

    public OrderServiceImpl(Vertx vertx, JsonObject config) {
        super(vertx, config);
    }

    @Override
    public void initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
        client.getConnection(connHandler(resultHandler, connection -> {
            connection.execute(CREATE_STATEMENT, r -> {
                resultHandler.handle(r);
                connection.close();
            });
        }));
    }

    @Override
    public void addOrder(Order order, Handler<AsyncResult<Void>> resultHandler) {
        JsonArray params = new JsonArray()
                .add(order.getBuyerId())
                .add(order.getProductId());
        insert(params, INSERT_STATEMENT).onComplete(asyncResult -> {
            order.setId(asyncResult.result().getInteger(0));
            resultHandler.handle(Future.succeededFuture());
        });
    }

    @Override
    public void retrieveOrder(String orderId, Handler<AsyncResult<Order>> resultHandler) {
        this.fetchOne(Integer.parseInt(orderId), FETCH_STATEMENT)
                .map(option -> option.map(Order::new).orElse(null))
                .onComplete(resultHandler);
    }

    @Override
    public void retrieveOrders(String buyerId, Handler<AsyncResult<List<Order>>> resultHandler) {
        this.fetchMany(FETCH_MANY_STATEMENT)
                .map(rawList -> rawList.stream()
                        .map(Order::new)
                        .collect(Collectors.toList())
                )
                .onComplete(resultHandler);
    }

    @Override
    public void deleteOrder(String orderId, Handler<AsyncResult<Void>> resultHandler) {
        this.delete(Integer.parseInt(orderId), DELETE_STATEMENT, resultHandler);
    }
}
