package com.ilegra.holiexpress.product.api;

import com.ilegra.holiexpress.common.RestAPIVerticle;
import com.ilegra.holiexpress.product.entity.Product;
import com.ilegra.holiexpress.product.service.ProductService;
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

public class ProductRestAPIVerticle extends RestAPIVerticle {

    public static final String SERVICE_NAME = "product";

    private static final String API_ADD = "/add";
    private static final String API_RETRIEVE_ALL = "/retrieveAll";
    private static final String API_RETRIEVE = "/retrieve/:id";
    private static final String API_COMPARE = "/compare";

    private final ProductService service;

    public ProductRestAPIVerticle(ProductService service) {
        this.service = service;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        super.start();
        final Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.route().handler(this::logger);

        router.post(API_ADD).handler(context -> requireLogin(context, this::apiAdd));
        router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
        router.get(API_RETRIEVE).handler(this::apiRetrieve);
        router.get(API_COMPARE).handler(this::apiCompareSimilarProducts);

        String host = config().getString("http.address", "0.0.0.0");
        int port = config().getInteger("http.port", 9000);

        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port).future())
                .onComplete(endpointPublished -> startPromise.complete());
    }

    private void apiAdd(RoutingContext context, JsonObject userPrincipal) {
        try {
            JsonObject body = new JsonObject(context.getBodyAsString());
            Product product = new Product(body.getJsonObject("product"));
            int stock = body.getInteger("stock");

            //TODO verify if sellerId matches userPrincipal

            service.addProduct(product, resultHandler(context, r -> {
                getStockEndpoint().future().onComplete(asyncResult -> {
                    HttpClient client = asyncResult.result();

                    HttpClientRequest request = client.request(HttpMethod.POST, "/increase", retrieveResponse -> {
                        if (retrieveResponse.statusCode() == 200) {
                            String result = new JsonObject().put("message", "product_added")
                                    .put("id", product.getId())
                                    .encodePrettily();
                            context.response().setStatusCode(201)
                                    .putHeader("content-type", "application/json")
                                    .end(result);
                        } else {
                            handleInternalError(context, new Exception("Unable to increase stock"));
                        }

                        ServiceDiscovery.releaseServiceObject(discovery, client);
                    });

                    request.end(new JsonObject()
                            .put("productId", String.valueOf(product.getId()))
                            .put("amount", String.valueOf(stock))
                            .encodePrettily());
                });
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

    private void apiCompareSimilarProducts(RoutingContext context) {
        try {
            Product product = new Product(new JsonObject(context.getBodyAsString()));
            service.compareSimilarProducts(product, resultHandler(context, Json::encodePrettily));
        } catch (DecodeException e) {
            handleBadRequest(context, e);
        }
    }

    private Promise<HttpClient> getStockEndpoint() {
        Promise<HttpClient> promise = Promise.promise();
        HttpEndpoint.getClient(discovery,
                new JsonObject().put("api.name", "stock"),
                promise);
        return promise;
    }
}
