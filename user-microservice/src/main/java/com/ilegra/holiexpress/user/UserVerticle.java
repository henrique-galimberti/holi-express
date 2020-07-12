package com.ilegra.holiexpress.user;

import com.ilegra.holiexpress.common.BaseMicroserviceVerticle;
import com.ilegra.holiexpress.user.api.UserRestAPIVerticle;
import com.ilegra.holiexpress.user.service.UserService;
import com.ilegra.holiexpress.user.service.UserServiceImpl;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class UserVerticle extends BaseMicroserviceVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        super.start();

        UserService userService = new UserServiceImpl(vertx, config());

        userService.initializePersistence(persistenceInitialized -> deployRestService(userService)
                .onComplete(restDeployed -> startPromise.complete()));
    }

    private Future<Void> deployRestService(UserService service) {
        Promise<String> promise = Promise.promise();
        vertx.deployVerticle(new UserRestAPIVerticle(service),
                new DeploymentOptions().setConfig(config()),
                promise);
        return promise.future().map((Void) null);
    }
}