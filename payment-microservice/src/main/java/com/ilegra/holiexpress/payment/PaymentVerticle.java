package com.ilegra.holiexpress.payment;

import com.ilegra.holiexpress.common.BaseMicroserviceVerticle;
import com.ilegra.holiexpress.payment.api.PaymentRestAPIVerticle;
import com.ilegra.holiexpress.payment.service.PaymentService;
import com.ilegra.holiexpress.payment.service.PaymentServiceImpl;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class PaymentVerticle extends BaseMicroserviceVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        super.start();

        PaymentService paymentService = new PaymentServiceImpl(vertx, config());

        deployRestService(paymentService)
                .onComplete(restDeployed -> startPromise.complete());
    }

    private Future<Void> deployRestService(PaymentService service) {
        Promise<String> promise = Promise.promise();
        vertx.deployVerticle(new PaymentRestAPIVerticle(service),
                new DeploymentOptions().setConfig(config()),
                promise);
        return promise.future().map((Void) null);
    }
}