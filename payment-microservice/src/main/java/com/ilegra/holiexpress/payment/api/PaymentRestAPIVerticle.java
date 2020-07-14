package com.ilegra.holiexpress.payment.api;

import com.ilegra.holiexpress.common.RestAPIVerticle;
import com.ilegra.holiexpress.payment.entity.Payment;
import com.ilegra.holiexpress.payment.service.PaymentService;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.servicediscovery.types.HttpEndpoint;

public class PaymentRestAPIVerticle extends RestAPIVerticle {
    public static final String SERVICE_NAME = "payment";

    private static final String API_ADD = "/add";
    private static final String API_NOTIFICATION = "/notification";
    private static final String API_RETRIEVE = "/retrieve/:checkoutId";
    private static final String API_RETRIEVE_FROM_ORDER = "/retrieveFromOrder/:orderId";


    private final PaymentService service;

    public PaymentRestAPIVerticle(PaymentService service) {
        this.service = service;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        super.start();
        final Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.post(API_ADD).handler(this::apiAdd);
        router.post(API_NOTIFICATION).handler(this::apiNotification);
        router.get(API_RETRIEVE).handler(this::apiRetrieve);
        router.get(API_RETRIEVE_FROM_ORDER).handler(this::apiRetrieveFromOrder);

        String host = config().getString("http.address", "0.0.0.0");
        int port = config().getInteger("http.port", 9000);

        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port).future())
                .onComplete(endpointPublished -> startPromise.complete());
    }

    private void apiAdd(RoutingContext context) {
        try {
            Payment payment = new Payment(new JsonObject(context.getBodyAsString()));
            service.addPayment(payment, resultHandler(context, r -> {
                String result = new JsonObject().put("message", "payment_added")
                        .encodePrettily();
                context.response().setStatusCode(201)
                        .putHeader("content-type", "application/json")
                        .end(result);
            }));
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
        String checkoutId = context.request().getParam("checkoutId");
        service.retrievePayment(checkoutId, resultHandlerNonEmpty(context));
    }

    private void apiRetrieveFromOrder(RoutingContext context) {
        String orderId = context.request().getParam("orderId");
        service.retrievePayments(orderId, resultHandler(context, Json::encodePrettily));
    }

    private Promise<HttpClient> getOrderEndpoint() {
        Promise<HttpClient> promise = Promise.promise();
        HttpEndpoint.getClient(discovery,
                new JsonObject().put("api.name", "order"),
                promise);
        return promise;
    }
}
