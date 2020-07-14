package com.ilegra.holiexpress.stock;

import com.ilegra.holiexpress.common.BaseMicroserviceVerticle;
import com.ilegra.holiexpress.stock.api.StockRestAPIVerticle;
import com.ilegra.holiexpress.stock.service.StockService;
import com.ilegra.holiexpress.stock.service.StockServiceImpl;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class StockVerticle extends BaseMicroserviceVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        super.start();

        StockService stockService = new StockServiceImpl(vertx, config());

        deployRestService(stockService)
                .onComplete(restDeployed -> startPromise.complete());
    }

    private Future<Void> deployRestService(StockService service) {
        Promise<String> promise = Promise.promise();
        vertx.deployVerticle(new StockRestAPIVerticle(service),
                new DeploymentOptions().setConfig(config()),
                promise);
        return promise.future().map((Void) null);
    }
}
