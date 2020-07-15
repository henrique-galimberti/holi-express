package com.ilegra.holiexpress.gateway;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(VertxExtension.class)
public class APIGatewayVerticleTest {

    @Test
    public void start_verticle() throws Throwable {
        Vertx vertx = Vertx.vertx();

        VertxTestContext testContext = new VertxTestContext();

        APIGatewayVerticle verticle = new APIGatewayVerticle();

        vertx.deployVerticle(verticle, testContext.completing());

        assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));

        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }
}

