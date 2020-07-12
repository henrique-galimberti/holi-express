package com.ilegra.holiexpress.order;

import com.ilegra.holiexpress.common.BaseMicroserviceVerticle;
import com.ilegra.holiexpress.order.api.OrderRestAPIVerticle;
import com.ilegra.holiexpress.order.service.OrderService;
import com.ilegra.holiexpress.order.service.OrderServiceImpl;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;


public class OrderVerticle extends BaseMicroserviceVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        super.start();

        OrderService orderService = new OrderServiceImpl(vertx, config());

        orderService.initializePersistence(persistenceInitialized -> deployRestService(orderService)
                .onComplete(restDeployed -> startPromise.complete()));
    }

    private Future<Void> deployRestService(OrderService service) {
        Promise<String> promise = Promise.promise();
        vertx.deployVerticle(new OrderRestAPIVerticle(service),
                new DeploymentOptions().setConfig(config()),
                promise);
        return promise.future().map((Void) null);
    }
}
