package com.ilegra.holiexpress.order.api;

import com.ilegra.holiexpress.common.RestAPIVerticle;
import com.ilegra.holiexpress.order.entity.Order;
import com.ilegra.holiexpress.order.service.OrderService;
import io.vertx.core.Promise;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class OrderRestAPIVerticle extends RestAPIVerticle {
    public static final String SERVICE_NAME = "order";

    private static final String API_ADD = "/add";
    private static final String API_RETRIEVE_FROM_BUYER = "/retrieveFromBuyer/:id";
    private static final String API_RETRIEVE = "/retrieve/:id";
    private static final String API_DELETE = "/delete/:id";

    private final OrderService service;

    public OrderRestAPIVerticle(OrderService service) {
        this.service = service;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        super.start();
        final Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.post(API_ADD).handler(this::apiAdd);
        router.get(API_RETRIEVE_FROM_BUYER).handler(this::apiRetrieveFromBuyer);
        router.get(API_RETRIEVE).handler(this::apiRetrieve);
        router.delete(API_DELETE).handler(this::apiDelete);

        String host = config().getString("http.address", "0.0.0.0");
        int port = config().getInteger("http.port", 9000);

        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port).future())
                .onComplete(endpointPublished -> startPromise.complete());
    }

    private void apiAdd(RoutingContext context) {
        try {
            Order order = new Order(new JsonObject(context.getBodyAsString()));
            service.addOrder(order, resultHandler(context, r -> {
                String result = new JsonObject().put("message", "order_added")
                        .put("id", order.getId())
                        .encodePrettily();
                context.response().setStatusCode(201)
                        .putHeader("content-type", "application/json")
                        .end(result);
            }));
        } catch (DecodeException e) {
            handleBadRequest(context, e);
        }
    }

    private void apiRetrieve(RoutingContext context) {
        String orderId = context.request().getParam("id");
        service.retrieveOrder(orderId, resultHandlerNonEmpty(context));
    }

    private void apiRetrieveFromBuyer(RoutingContext context) {
        String buyerId = context.request().getParam("id");
        service.retrieveOrders(buyerId, resultHandler(context, Json::encodePrettily));
    }

    private void apiDelete(RoutingContext context) {
        String orderId = context.request().getParam("id");
        service.deleteOrder(orderId, deleteResultHandler(context));
    }
}
