package com.ilegra.holiexpress.order.api;

import com.ilegra.holiexpress.common.RestAPIVerticle;
import com.ilegra.holiexpress.order.entity.Order;
import com.ilegra.holiexpress.order.service.OrderService;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

public class OrderRestAPIVerticle extends RestAPIVerticle {
    public static final String SERVICE_NAME = "order";

    private static final String API_ADD = "/add";
    private static final String API_NOTIFICATION = "/notification";
    private static final String API_RETRIEVE_FROM_BUYER = "/retrieveFromBuyer/:buyerId";
    private static final String API_RETRIEVE = "/retrieve/:id";

    private final OrderService service;

    public OrderRestAPIVerticle(OrderService service) {
        this.service = service;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        super.start();
        final Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.post(API_ADD).handler(context -> requireLogin(context, this::apiAdd));
        router.post(API_NOTIFICATION).handler(this::apiNotification);
        router.get(API_RETRIEVE_FROM_BUYER).handler(this::apiRetrieveFromBuyer);
        router.get(API_RETRIEVE).handler(this::apiRetrieve);

        String host = config().getString("http.address", "0.0.0.0");
        int port = config().getInteger("http.port", 9000);

        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port).future())
                .onComplete(endpointPublished -> startPromise.complete());
    }

    private void apiAdd(RoutingContext context, JsonObject userPrincipal) {
        try {
            Order order = new Order(new JsonObject(context.getBodyAsString()));

            //TODO: check if userPrincipal and buyer matches

            getStockEndpoint().future().onComplete(asyncResult -> {
                HttpClient client = asyncResult.result();

                client.request(HttpMethod.GET, "/retrieve/" + order.getProductId(), retrieveResponse -> {
                    if (retrieveResponse.statusCode() == 200) {
                        retrieveResponse.bodyHandler(body -> {
                            int currentStock = Integer.parseInt(body.toString());

                            if (currentStock == 0) {
                                String result = new JsonObject().put("message", "product_out_of_stock")
                                        .encodePrettily();
                                context.response().setStatusCode(200)
                                        .putHeader("content-type", "application/json")
                                        .end(result);

                                ServiceDiscovery.releaseServiceObject(discovery, client);
                            } else {
                                HttpClientRequest request = client.request(HttpMethod.POST, "/decrease", decreseResponse -> {
                                    ServiceDiscovery.releaseServiceObject(discovery, client);

                                    service.addOrder(order, resultHandler(context, r -> {
                                        String result = new JsonObject().put("message", "order_added")
                                                .put("id", order.getId())
                                                .encodePrettily();
                                        context.response().setStatusCode(201)
                                                .putHeader("content-type", "application/json")
                                                .end(result);
                                    }));
                                });

                                request.end(new JsonObject()
                                        .put("productId", String.valueOf(order.getProductId()))
                                        .put("amount", "1")
                                        .encodePrettily());
                            }
                        });
                    }
                }).end();
            });
        } catch (DecodeException e) {
            handleBadRequest(context, e);
        }
    }

    private void apiNotification(RoutingContext context) {
        service.notification(new JsonObject(context.getBodyAsString()), resultHandler(context, handler -> {
            context.response().setStatusCode(200).end();
        }));
    }

    private void apiRetrieve(RoutingContext context) {
        String orderId = context.request().getParam("orderId");
        service.retrieveOrder(orderId, resultHandlerNonEmpty(context));
    }

    private void apiRetrieveFromBuyer(RoutingContext context) {
        String buyerId = context.request().getParam("buyerId");
        service.retrieveOrders(buyerId, resultHandler(context, Json::encodePrettily));
    }

    private Promise<HttpClient> getStockEndpoint() {
        Promise<HttpClient> promise = Promise.promise();
        HttpEndpoint.getClient(discovery,
                new JsonObject().put("api.name", "stock"),
                promise);
        return promise;
    }
}
