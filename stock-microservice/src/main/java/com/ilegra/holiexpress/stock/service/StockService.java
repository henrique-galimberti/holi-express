package com.ilegra.holiexpress.stock.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface StockService {

    void increaseStock(String productId, String amount, Handler<AsyncResult<Integer>> resultHandler);

    void decreaseStock(String productId, String amount, Handler<AsyncResult<Integer>> resultHandler);

    void retrieveStock(String productId, Handler<AsyncResult<Integer>> resultHandler);
}
