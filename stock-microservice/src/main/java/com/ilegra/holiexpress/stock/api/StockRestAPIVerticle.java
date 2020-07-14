package com.ilegra.holiexpress.stock.api;

import com.ilegra.holiexpress.common.RestAPIVerticle;
import com.ilegra.holiexpress.stock.service.StockService;
import io.vertx.core.Promise;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class StockRestAPIVerticle extends RestAPIVerticle {

    public static final String SERVICE_NAME = "stock";

    private static final String API_INCREASE = "/increase";
    private static final String API_DECREASE = "/decrease";
    private static final String API_RETRIEVE = "/retrieve/:id";

    private final StockService service;

    public StockRestAPIVerticle(StockService service) {
        this.service = service;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        super.start();
        final Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.post(API_INCREASE).handler(this::apiIncreaseBy);
        router.post(API_DECREASE).handler(this::apiDecreaseBy);
        router.get(API_RETRIEVE).handler(this::apiRetrieve);

        String host = config().getString("http.address", "0.0.0.0");
        int port = config().getInteger("http.port", 9000);

        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port).future())
                .onComplete(endpointPublished -> startPromise.complete());
    }

    private void apiIncreaseBy(RoutingContext context) {
        try {
            JsonObject body = new JsonObject(context.getBodyAsString());
            service.increaseStock(body.getString("productId"), body.getString("amount"), resultHandler(context, r -> {
                String result = new JsonObject().put("message", "stock_increased")
                        .encodePrettily();
                context.response().setStatusCode(200)
                        .putHeader("content-type", "application/json")
                        .end(result);
            }));
        } catch (DecodeException e) {
            handleBadRequest(context, e);
        }
    }

    private void apiDecreaseBy(RoutingContext context) {
        try {
            JsonObject body = new JsonObject(context.getBodyAsString());
            service.decreaseStock(body.getString("productId"), body.getString("amount"), resultHandler(context, r -> {
                String result = new JsonObject().put("message", "stock_decreased")
                        .encodePrettily();
                context.response().setStatusCode(200)
                        .putHeader("content-type", "application/json")
                        .end(result);
            }));
        } catch (DecodeException e) {
            handleBadRequest(context, e);
        }
    }

    private void apiRetrieve(RoutingContext context) {
        String productId = context.request().getParam("id");
        service.retrieveStock(productId, resultHandler(context, Json::encodePrettily));
    }
}
