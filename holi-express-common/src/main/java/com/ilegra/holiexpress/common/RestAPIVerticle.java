package com.ilegra.holiexpress.common;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.function.Function;

public class RestAPIVerticle extends BaseMicroserviceVerticle {

    protected Future<Void> createHttpServer(Router router, String host, int port) {
        Promise<HttpServer> promise = Promise.promise();
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, host, promise);
        return promise.future().map((Void) null);
    }

    protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Handler<T> handler) {
        return asyncResult -> {
            if (asyncResult.succeeded()) {
                handler.handle(asyncResult.result());
            } else {
                handleInternalError(context, asyncResult.cause());
                asyncResult.cause().printStackTrace();
            }
        };
    }

    protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Function<T, String> converter) {
        return asyncResult -> {
            if (asyncResult.succeeded()) {
                T res = asyncResult.result();
                if (res == null) {
                    handleInternalError(context, new Exception("Invalid result"));
                } else {
                    context.response()
                            .putHeader("content-type", "application/json")
                            .end(converter.apply(res));
                }
            } else {
                handleInternalError(context, asyncResult.cause());
                asyncResult.cause().printStackTrace();
            }
        };
    }

    protected <T> Handler<AsyncResult<T>> resultHandlerNonEmpty(RoutingContext context) {
        return asyncResult -> {
            if (asyncResult.succeeded()) {
                T res = asyncResult.result();
                if (res == null) {
                    handleNotFound(context);
                } else {
                    context.response()
                            .putHeader("content-type", "application/json")
                            .end(res.toString());
                }
            } else {
                handleInternalError(context, asyncResult.cause());
                asyncResult.cause().printStackTrace();
            }
        };
    }

    protected Handler<AsyncResult<Void>> deleteResultHandler(RoutingContext context) {
        return asyncResult -> {
            if (asyncResult.succeeded()) {
                context.response().setStatusCode(204)
                        .putHeader("content-type", "application/json")
                        .end(new JsonObject().put("message", "delete_success").encodePrettily());
            } else {
                handleInternalError(context, asyncResult.cause());
                asyncResult.cause().printStackTrace();
            }
        };
    }

    protected void handleBadRequest(RoutingContext context, Throwable ex) {
        context.response().setStatusCode(400)
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("error", ex.getMessage()).encodePrettily());
    }

    protected void handleNotFound(RoutingContext context) {
        context.response().setStatusCode(404)
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("message", "not found").encodePrettily());
    }

    protected void handleInternalError(RoutingContext context, Throwable ex) {
        context.response().setStatusCode(500)
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("error", ex.getMessage()).encodePrettily());
    }

    protected void handleBadGateway(Throwable ex, RoutingContext context) {
        context.response()
                .setStatusCode(502)
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("error", "bad gateway")
                        .encodePrettily());
    }
}
