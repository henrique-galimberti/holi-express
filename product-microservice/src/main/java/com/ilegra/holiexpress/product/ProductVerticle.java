package com.ilegra.holiexpress.product;

import com.ilegra.holiexpress.common.BaseMicroserviceVerticle;
import com.ilegra.holiexpress.product.api.RestProductAPIVerticle;
import com.ilegra.holiexpress.product.service.ProductService;
import com.ilegra.holiexpress.product.service.ProductServiceImpl;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;


public class ProductVerticle extends BaseMicroserviceVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        super.start();

        ProductService productService = new ProductServiceImpl(vertx, config());

        productService.initializePersistence(persistenceInitialized -> deployRestService(productService)
                .onComplete(restDeployed -> startPromise.complete()));
    }

    private Future<Void> deployRestService(ProductService service) {
        Promise<String> promise = Promise.promise();
        vertx.deployVerticle(new RestProductAPIVerticle(service),
                new DeploymentOptions().setConfig(config()),
                promise);
        return promise.future().map((Void) null);
    }
}
