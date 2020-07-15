package com.ilegra.holiexpress.payment.service;

import com.ilegra.holiexpress.common.service.BaseJdbcService;
import com.ilegra.holiexpress.payment.entity.Payment;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentServiceImpl extends BaseJdbcService implements PaymentService {
    private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS payments (\n" +
            "  \"transactionId\" varchar(255) NOT NULL,\n" +
            "  \"orderId\" int NOT NULL,\n" +
            "  status varchar(255) NOT NULL,\n" +
            "  PRIMARY KEY (\"transactionId\") )";
    private static final String INSERT_STATEMENT = "INSERT INTO payments (\"transactionId\", \"orderId\", status) VALUES (?, ?, ?)";
    private static final String UPDATE_STATEMENT = "UPDATE payments SET status = ? WHERE \"transactionId\" = ?";
    private static final String FETCH_STATEMENT = "SELECT * FROM payments WHERE \"transactionId\" = ?";
    private static final String FETCH_MANY_STATEMENT = "SELECT * FROM payments where \"orderId\" = ?";

    public PaymentServiceImpl(Vertx vertx, JsonObject config) {
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
    public void addPayment(Payment payment, Handler<AsyncResult<Void>> resultHandler) {
        JsonArray params = new JsonArray()
                .add(payment.getId())
                .add(payment.getOrderId())
                .add(Payment.STATUS_PENDING);
        insert(params, INSERT_STATEMENT).onComplete(asyncResult -> {
            resultHandler.handle(Future.succeededFuture());
        });
    }

    @Override
    public void notification(JsonObject notification, Handler<AsyncResult<Void>> resultHandler) {
        //for security reasons it should request this informations from the payment api (integration)
        //ex: get this notification id and then query its stats on payment api (paypal, pagseguro, ...)
        String transactionId = getTransactionIdFromNotification(notification);
        String newStatus = getNewsStatus(notification);

        execute(new JsonArray()
                        .add(transactionId)
                        .add(newStatus),
                UPDATE_STATEMENT,
                asyncResult -> {
                    resultHandler.handle(Future.succeededFuture());

                    //TODO notify order api to change state
                });
    }

    @Override
    public void retrievePayment(String paymentId, Handler<AsyncResult<Payment>> resultHandler) {
        this.fetchOne(paymentId, FETCH_STATEMENT)
                .map(option -> option.map(Payment::new).orElse(null))
                .onComplete(resultHandler);
    }

    @Override
    public void retrievePayments(String orderId, Handler<AsyncResult<List<Payment>>> resultHandler) {
        this.fetchMany(new JsonArray().add(orderId), FETCH_MANY_STATEMENT)
                .map(rawList -> rawList.stream()
                        .map(Payment::new)
                        .collect(Collectors.toList())
                )
                .onComplete(resultHandler);
    }

    private String getTransactionIdFromNotification(JsonObject notification) {
        //should return based on notification
        return notification.getString("transaction");
    }

    private String getNewsStatus(JsonObject notification) {
        //should return based on notification
        return Payment.STATUS_FINISHED;
    }
}
