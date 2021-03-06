package com.ilegra.holiexpress.user.api;

import com.ilegra.holiexpress.common.RestAPIVerticle;
import com.ilegra.holiexpress.user.entity.User;
import com.ilegra.holiexpress.user.service.UserService;
import io.vertx.core.Promise;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class UserRestAPIVerticle extends RestAPIVerticle {

    public static final String SERVICE_NAME = "user";

    private static final String API_ADD = "/add";
    private static final String API_RETRIEVE_ALL = "/retrieveAll";
    private static final String API_RETRIEVE = "/retrieve/:id";

    private final UserService service;

    public UserRestAPIVerticle(UserService service) {
        this.service = service;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        super.start();
        final Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.route().handler(this::logger);

        router.post(API_ADD).handler(this::apiAdd);
        router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
        router.get(API_RETRIEVE).handler(this::apiRetrieve);

        String host = config().getString("http.address", "0.0.0.0");
        int port = config().getInteger("http.port", 9000);

        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port).future())
                .onComplete(endpointPublished -> startPromise.complete());
    }

    private void apiAdd(RoutingContext context) {
        try {
            User user = new User(new JsonObject(context.getBodyAsString()));
            service.addUser(user, resultHandler(context, r -> {
                String result = new JsonObject().put("message", "user_added")
                        .put("id", user.getId())
                        .encodePrettily();
                context.response().setStatusCode(201)
                        .putHeader("content-type", "application/json")
                        .end(result);
            }));
        } catch (DecodeException e) {
            handleBadRequest(context, e);
        }
    }

//    private void apiAdd(RoutingContext context) {
//        try {
//            User user = new User(new JsonObject(context.getBodyAsString()));
//
//            getAuthEndpoint().future().onComplete(asyncResult -> {
//                HttpClient client = asyncResult.result();
//
//                HttpClientRequest request = client.request(HttpMethod.POST, "/hash", response -> {
//                    if (response.statusCode() == 200) {
//                        response.bodyHandler(buffer -> {
//                            JsonObject hashedPassword = new JsonObject(buffer.toString());
//                            user.setPassword(hashedPassword.getString("password"));
//                            user.setPassword_salt(hashedPassword.getString("password_salt"));
//
//                            service.addUser(user, resultHandler(context, r -> {
//                                String result = new JsonObject().put("message", "user_added")
//                                        .put("id", user.getId())
//                                        .encodePrettily();
//                                context.response().setStatusCode(201)
//                                        .putHeader("content-type", "application/json")
//                                        .end(result);
//                            }));
//
//                            ServiceDiscovery.releaseServiceObject(discovery, client);
//                        });
//                    }
//                });
//
//                request.end(new JsonObject().put("password", user.getPassword()).encodePrettily());
//            });
//
//        } catch (DecodeException e) {
//            handleBadRequest(context, e);
//        }
//    }

    private void apiRetrieve(RoutingContext context) {
        String userId = context.request().getParam("id");
        service.retrieveUser(userId, resultHandlerNonEmpty(context));
    }

    private void apiRetrieveAll(RoutingContext context) {
        service.retrieveAllUsers(resultHandler(context, Json::encodePrettily));
    }
}
