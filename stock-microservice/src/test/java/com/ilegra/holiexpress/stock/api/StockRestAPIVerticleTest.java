//package com.ilegra.holiexpress.stock.api;
//
//import io.vertx.core.DeploymentOptions;
//import io.vertx.core.Vertx;
//import io.vertx.core.json.JsonObject;
//import io.vertx.ext.unit.Async;
//import io.vertx.ext.unit.TestContext;
//import io.vertx.ext.unit.junit.VertxUnitRunner;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.powermock.modules.junit4.PowerMockRunner;
//import org.powermock.modules.junit4.PowerMockRunnerDelegate;
//import redis.embedded.RedisServer;
//
//import java.io.IOException;
//import java.net.ServerSocket;
//
//@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(VertxUnitRunner.class)
//public class StockRestAPIVerticleTest {
//
//    private RedisServer server;
//    private String deploymentId;
//    private int port;
//
//    @Before
//    public void mockRedis() throws IOException {
//        ServerSocket serverSocket = new ServerSocket(0);
//        port = serverSocket.getLocalPort();
//        serverSocket.close();
//        server = new RedisServer(port);
//        server.start();
//    }
//
//    @Test
//    public void start_stop(TestContext testContext) {
//        Vertx vertx = Vertx.vertx();
//
//        StockRestAPIVerticle verticle = new StockRestAPIVerticle(null);
//
//        Async asyncStart = testContext.async();
//        vertx.deployVerticle(verticle, new DeploymentOptions()
//                .setConfig(new JsonObject()
//                        .put("redis.connection", "redis://localhost:" + port)), asyncResult -> {
//            testContext.assertTrue(asyncResult.succeeded());
//            deploymentId = asyncResult.result();
//            asyncStart.complete();
//        });
//        asyncStart.await(5000);
//
//        Async asyncStop = testContext.async();
//        vertx.undeploy(deploymentId, asyncResult -> {
//            testContext.assertTrue(asyncResult.succeeded());
//            asyncStop.complete();
//        });
//        asyncStop.await(5000);
//    }
//
//    @After
//    public void stopRedis() {
//        server.stop();
//    }
//}