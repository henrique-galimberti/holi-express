package com.ilegra.holiexpress.common;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.io.IOException;
import java.net.ServerSocket;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
public class RestAPIVerticleTest {

    private String deploymentId;

    @Test
    public void start_stop(TestContext testContext) throws IOException {
        Vertx vertx = Vertx.vertx();

        RestAPIVerticle verticle = new RestAPIVerticle();

        Async asyncStart = testContext.async();
        vertx.deployVerticle(verticle, asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            deploymentId = asyncResult.result();
            asyncStart.complete();
        });
        asyncStart.await(5000);

        final Router router = Router.router(vertx);
        router.get("/400").handler(routingContext -> verticle.handleBadRequest(routingContext, new Exception()));
        router.get("/404").handler(verticle::handleNotFound);
        router.get("/500").handler(routingContext -> verticle.handleInternalError(routingContext, new Exception()));
        router.get("/502").handler(routingContext -> verticle.handleBadGateway(new Exception(), routingContext));
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        serverSocket.close();
        Async asyncCreateHttpServer = testContext.async();
        verticle.createHttpServer(router, "localhost", port)
                .onComplete(asyncResult -> {
                    testContext.assertTrue(asyncResult.succeeded());
                    asyncCreateHttpServer.complete();
                });
        asyncCreateHttpServer.await(5000);

        WebClient client = WebClient.create(vertx);

        Async async400 = testContext.async();
        client.get(port, "localhost", "/400").send(ar -> {
            testContext.assertEquals(400, ar.result().statusCode());
            async400.complete();
        });
        async400.await(5000);

        Async async404 = testContext.async();
        client.get(port, "localhost", "/404").send(ar -> {
            testContext.assertEquals(404, ar.result().statusCode());
            async404.complete();
        });
        async404.await(5000);

        Async async500 = testContext.async();
        client.get(port, "localhost", "/500").send(ar -> {
            testContext.assertEquals(500, ar.result().statusCode());
            async500.complete();
        });
        async500.await(5000);

        Async async502 = testContext.async();
        client.get(port, "localhost", "/502").send(ar -> {
            testContext.assertEquals(502, ar.result().statusCode());
            async502.complete();
        });
        async502.await(5000);

        Async asyncStop = testContext.async();
        vertx.undeploy(deploymentId, asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            asyncStop.complete();
        });
        asyncStop.await(5000);
    }
}
