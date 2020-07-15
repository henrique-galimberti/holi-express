package com.ilegra.holiexpress.gateway;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
public class APIGatewayVerticleTest {

    private String deploymentId;

    @Test
    public void start_stop(TestContext testContext) {
        Vertx vertx = Vertx.vertx();

        APIGatewayVerticle verticle = new APIGatewayVerticle();

        Async asyncStart = testContext.async();
        vertx.deployVerticle(verticle, asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            deploymentId = asyncResult.result();
            asyncStart.complete();
        });
        asyncStart.await(5000);

        Async asyncStop = testContext.async();
        vertx.undeploy(deploymentId, asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            asyncStop.complete();
        });
        asyncStop.await(5000);
    }
}

