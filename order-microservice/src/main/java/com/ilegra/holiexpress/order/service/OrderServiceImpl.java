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
    private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS orders (\n" +
            "  id SERIAL,\n" +
            "  \"buyerId\" int NOT NULL,\n" +
            "  \"productId\" int NOT NULL,\n" +
            "  \"value\" decimal NOT NULL,\n" +
            "  status varchar(255) NOT NULL,\n" +
            "  PRIMARY KEY (id) )";
    private static final String INSERT_STATEMENT = "INSERT INTO orders (\"buyerId\", \"productId\", \"value\", status) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_STATEMENT = "UPDATE orders SET status = ? WHERE id = ?";
    private static final String FETCH_STATEMENT = "SELECT * FROM orders WHERE id = ?";
    private static final String FETCH_MANY_STATEMENT = "SELECT * FROM orders where \"buyerId\" = ?";

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
                .add(order.getProductId())
                .add(order.getValue())
                .add(Order.STATUS_WAITING_PAYMENT);
        insert(params, INSERT_STATEMENT).onComplete(asyncResult -> {
            order.setId(asyncResult.result().getInteger(0));
            resultHandler.handle(Future.succeededFuture());
        });
    }

    @Override
    public void notification(JsonObject notification, Handler<AsyncResult<Void>> resultHandler) {
        String orderId = getOrderIdFromNotification(notification);
        String newStatus = getNewsStatus(notification);
        execute(new JsonArray()
                        .add(orderId)
                        .add(newStatus),
                UPDATE_STATEMENT,
                asyncResult -> {
                    resultHandler.handle(Future.succeededFuture());

                    if (newStatus.equals(Order.STATUS_CANCELLED)) {
                        //TODO increase stock
                    }
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
        this.fetchMany(new JsonArray().add(buyerId), FETCH_MANY_STATEMENT)
                .map(rawList -> rawList.stream()
                        .map(Order::new)
                        .collect(Collectors.toList())
                )
                .onComplete(resultHandler);
    }

    private String getOrderIdFromNotification(JsonObject notification) {
        //should return based on notification
        return notification.getString("orderId");
    }

    private String getNewsStatus(JsonObject notification) {
        //should return based on notification
        return Order.STATUS_FINISHED;
    }
}
