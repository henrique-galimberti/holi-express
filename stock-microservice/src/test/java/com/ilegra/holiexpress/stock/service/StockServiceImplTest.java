package com.ilegra.holiexpress.stock.service;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
public class StockServiceImplTest {

    private RedisServer server;
    private int port;

    @Before
    public void mockRedis() throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
        serverSocket.close();
        server = new RedisServer(port);
        server.start();
    }

    @Test
    public void operations(TestContext testContext) throws InterruptedException {

        Vertx vertx = Vertx.vertx();

        StockServiceImpl service = new StockServiceImpl(vertx, new JsonObject()
                .put("redis.connection", "redis://localhost:" + port));

        //wait for redis to start
        Thread.sleep(5000);

        Async increaseAsync = testContext.async();
        service.increaseStock("6", "666", asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            testContext.assertEquals(666, asyncResult.result());
            increaseAsync.complete();
        });
        increaseAsync.await(5000);

        Async decreaseAsync = testContext.async();
        service.decreaseStock("6", "600", asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            testContext.assertEquals(66, asyncResult.result());
            decreaseAsync.complete();
        });
        decreaseAsync.await(5000);

        Async retrieveAsync = testContext.async();
        service.retrieveStock("6", asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            testContext.assertEquals(66, asyncResult.result());
            retrieveAsync.complete();
        });
        retrieveAsync.await(5000);
    }

    @After
    public void stopRedis() {
        server.stop();
    }
}
