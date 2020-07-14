package com.ilegra.holiexpress.product.api;

import com.ilegra.holiexpress.common.RestAPIVerticle;
import com.ilegra.holiexpress.product.entity.Product;
import com.ilegra.holiexpress.product.service.ProductService;
import io.vertx.core.Promise;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ProductRestAPIVerticle extends RestAPIVerticle {

    public static final String SERVICE_NAME = "product";

    private static final String API_ADD = "/add";
    private static final String API_RETRIEVE_ALL = "/retrieveAll";
    private static final String API_RETRIEVE = "/retrieve/:id";

    private final ProductService service;

    public ProductRestAPIVerticle(ProductService service) {
        this.service = service;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        super.start();
        final Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.post(API_ADD).handler(context -> requireLogin(context, this::apiAdd));
        router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
        router.get(API_RETRIEVE).handler(this::apiRetrieve);

        String host = config().getString("http.address", "0.0.0.0");
        int port = config().getInteger("http.port", 9000);

        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port).future())
                .onComplete(endpointPublished -> startPromise.complete());
    }

    private void apiAdd(RoutingContext context, JsonObject userPrincipal) {
        try {
            Product product = new Product(new JsonObject(context.getBodyAsString()));
            service.addProduct(product, resultHandler(context, r -> {
                String result = new JsonObject().put("message", "product_added")
                        .put("id", product.getId())
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
        String productId = context.request().getParam("id");
        service.retrieveProduct(productId, resultHandlerNonEmpty(context));
    }

    private void apiRetrieveAll(RoutingContext context) {
        service.retrieveAllProducts(resultHandler(context, Json::encodePrettily));
    }
}
