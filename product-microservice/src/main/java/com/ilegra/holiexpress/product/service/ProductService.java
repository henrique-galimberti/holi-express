package com.ilegra.holiexpress.product.service;

import com.ilegra.holiexpress.product.entity.Product;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

public interface ProductService {

    String SERVICE_ADDRESS = "service.product";

    void initializePersistence(Handler<AsyncResult<Void>> resultHandler);

    void addProduct(Product product, Handler<AsyncResult<Void>> resultHandler);

    void retrieveProduct(String productId, Handler<AsyncResult<Product>> resultHandler);

    void retrieveAllProducts(Handler<AsyncResult<List<Product>>> resultHandler);

    void deleteProduct(String productId, Handler<AsyncResult<Void>> resultHandler);
}
