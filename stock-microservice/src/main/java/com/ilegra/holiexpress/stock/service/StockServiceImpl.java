package com.ilegra.holiexpress.stock.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.*;

public class StockServiceImpl implements StockService {

    private static final String PREFIX = "stock:";

    private RedisConnection conn;
    private RedisAPI redis;

    public StockServiceImpl(Vertx vertx, JsonObject config) {
        RedisOptions redisOptions = new RedisOptions()
                .setConnectionString(config.getString("redis.connection", "redis://localhost:6379"));
        Redis.createClient(vertx, redisOptions).connect(onConnect -> {
            conn = onConnect.result();
            redis = RedisAPI.api(conn);
        });
    }

    public void increaseStock(String productId, String amount, Handler<AsyncResult<Integer>> resultHandler) {
        redis.incrby(PREFIX + productId, amount, asyncResult -> {
            resultHandler.handle(asyncResult.map(Response::toInteger));
        });
    }

    public void decreaseStock(String productId, String amount, Handler<AsyncResult<Integer>> resultHandler) {
        redis.decrby(PREFIX + productId, amount, asyncResult -> {
            resultHandler.handle(asyncResult.map(Response::toInteger));
        });
    }

    public void retrieveStock(String productId, Handler<AsyncResult<Integer>> resultHandler) {
        redis.get(PREFIX + productId, asyncResult -> {
            resultHandler.handle(asyncResult.map(Response::toInteger));
        });
    }
}
