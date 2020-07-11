package com.ilegra.holiexpress.gateway;

import com.ilegra.holiexpress.common.RestAPIVerticle;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

import java.util.List;
import java.util.Optional;

public class APIGatewayVerticle extends RestAPIVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        super.start();

        JsonObject circuitBreakerConfig = config().getJsonObject("circuit-breaker") != null ?
                config().getJsonObject("circuit-breaker") : new JsonObject();

        circuitBreaker = CircuitBreaker.create(circuitBreakerConfig.getString("name", "circuit-breaker"), vertx,
                new CircuitBreakerOptions()
                        .setMaxFailures(circuitBreakerConfig.getInteger("max-failures", 5))
                        .setTimeout(circuitBreakerConfig.getLong("timeout", 2000L))
                        .setFallbackOnFailure(circuitBreakerConfig.getBoolean("fallback-on-failure", true))
                        .setResetTimeout(circuitBreakerConfig.getLong("reset-timeout", 10000L))
        );

        String host = config().getString("http.address", "localhost");
        int port = config().getInteger("http.port", 9000);

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.route("/api/*").handler(this::dispatchRequests);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, host, asyncResult -> {
                    if (asyncResult.succeeded()) {
                        publishApiGateway(host, port).future().onComplete(ar -> startPromise.complete());

                    } else {
                        startPromise.fail(asyncResult.cause());
                    }
                });
    }

    private void dispatchRequests(RoutingContext context) {
        int initialOffset = 5; // = "/api/".length();

        circuitBreaker.execute(command -> getAvailableEndpoints().onComplete(asyncResult -> {
            if (asyncResult.succeeded()) {
                List<Record> recordList = asyncResult.result();

                String path = context.request().uri();

                if (path.length() <= initialOffset) {
                    handleNotFound(context);
                    command.complete();
                    return;
                }
                String prefix = (path.substring(initialOffset)
                        .split("/"))[0];

                String newPath = path.substring(initialOffset + prefix.length());

                Optional<Record> client = recordList.stream()
                        .filter(record -> record.getMetadata().getString("api.name") != null)
                        .filter(record -> record.getMetadata().getString("api.name").equals(prefix))
                        .findAny();

                if (client.isPresent()) {
                    doDispatch(context, newPath, discovery.getReference(client.get()).get(), command);
                } else {
                    handleNotFound(context);
                    command.complete();
                }
            } else {
                command.fail(asyncResult.cause());
            }
        })).onComplete(asyncResult -> {
            if (asyncResult.failed()) {
                handleBadGateway(asyncResult.cause(), context);
            }
        });
    }

    private void doDispatch(RoutingContext context, String path, HttpClient endpointClient, Promise<Object> circuitBreakerCommand) {
        HttpClientRequest endpointRequest = endpointClient
                .request(context.request().method(), path, response -> response.bodyHandler(body -> {
                    if (response.statusCode() >= 500) {
                        circuitBreakerCommand.fail(response.statusCode() + ": " + body.toString());
                    } else {
                        HttpServerResponse clientResponse = context.response()
                                .setStatusCode(response.statusCode());
                        response.headers().forEach(header -> clientResponse.putHeader(header.getKey(), header.getValue()));

                        clientResponse.end(body);
                        circuitBreakerCommand.complete();
                    }
                    ServiceDiscovery.releaseServiceObject(discovery, endpointClient);
                }));

        context.request().headers().forEach(header -> endpointRequest.putHeader(header.getKey(), header.getValue()));

        if (context.getBody() == null) {
            endpointRequest.end();
        } else {
            endpointRequest.end(context.getBody());
        }
    }

    private Future<List<Record>> getAvailableEndpoints() {
        Promise<List<Record>> promise = Promise.promise();
        discovery.getRecords(record -> record.getType().equals(HttpEndpoint.TYPE),
                promise.future());
        return promise.future();
    }

    private Promise<Void> publishApiGateway(String host, int port) {
        Record record = HttpEndpoint.createRecord("api-gateway", false, host, port, "/", null)
                .setType("api-gateway");
        return publish(record);
    }
}
