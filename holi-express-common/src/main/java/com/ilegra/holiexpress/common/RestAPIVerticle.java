package com.ilegra.holiexpress.common;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RestAPIVerticle extends BaseMicroserviceVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestAPIVerticle.class);

    protected Future<Void> createHttpServer(Router router, String host, int port) {
        Promise<HttpServer> promise = Promise.promise();
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, host, promise);
        return promise.future().map((Void) null);
    }

    protected void requireLogin(RoutingContext context, BiConsumer<RoutingContext, JsonObject> biHandler) {
        Optional<JsonObject> principal = Optional.ofNullable(context.request().getHeader("user-principal"))
                .map(JsonObject::new);
        if (principal.isPresent()) {
            biHandler.accept(context, principal.get());
        } else {
            LOGGER.warn("Unauthenticated Request");
            context.response()
                    .setStatusCode(401)
                    .end(new JsonObject().put("message", "need_auth").encode());
        }
    }

    protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Handler<T> handler) {
        return asyncResult -> {
            if (asyncResult.succeeded()) {
                handler.handle(asyncResult.result());
            } else {
                handleInternalError(context, asyncResult.cause());
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
            }
        };
    }

    protected void handleBadRequest(RoutingContext context, Throwable ex) {
        LOGGER.warn(ex.getMessage(), ex);
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
        LOGGER.error(ex.getMessage(), ex);
        context.response().setStatusCode(500)
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("error", ex.getMessage()).encodePrettily());
    }

    protected void handleBadGateway(Throwable ex, RoutingContext context) {
        LOGGER.error(ex.getMessage(), ex);
        context.response()
                .setStatusCode(502)
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("error", "bad gateway")
                        .encodePrettily());
    }

    protected void logger(RoutingContext context) {
        LOGGER.info("Request received: PATH=[" + context.normalisedPath() + "]");
        context.next();
    }
}
