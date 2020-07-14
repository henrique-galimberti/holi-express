package com.ilegra.holiexpress.product.service;

import com.ilegra.holiexpress.common.service.BaseJdbcService;
import com.ilegra.holiexpress.product.entity.Product;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class ProductServiceImpl extends BaseJdbcService implements ProductService {

    private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS products (\n" +
            "  id SERIAL,\n" +
            "  \"sellerId\" int NOT NULL,\n" +
            "  name varchar(255) NOT NULL,\n" +
            "  price decimal NOT NULL,\n" +
            "  image text NOT NULL,\n" +
            "  type varchar(45) NOT NULL,\n" +
            "  PRIMARY KEY (id) )";
    private static final String INSERT_STATEMENT = "INSERT INTO products (\"sellerId\", name, price, image, type) VALUES (?, ?, ?, ?, ?)";
    private static final String FETCH_STATEMENT = "SELECT * FROM products WHERE id = ?";
    private static final String FETCH_ALL_STATEMENT = "SELECT * FROM products";
    private static final String DELETE_STATEMENT = "DELETE FROM products WHERE id = ?";

    public ProductServiceImpl(Vertx vertx, JsonObject config) {
        super(vertx, config);
    }

    @Override
    public void initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
        client.getConnection(connHandler(resultHandler, connection -> {
            connection.execute(CREATE_STATEMENT, r -> {
                resultHandler.handle(r);
                connection.close();
            });
        }));
    }

    @Override
    public void addProduct(Product product, Handler<AsyncResult<Void>> resultHandler) {
        JsonArray params = new JsonArray()
                .add(product.getSellerId())
                .add(product.getName())
                .add(product.getPrice())
                .add(product.getImage())
                .add(product.getType());
        insert(params, INSERT_STATEMENT).onComplete(asyncResult -> {
            product.setId(asyncResult.result().getInteger(0));
            resultHandler.handle(Future.succeededFuture());
        });
    }

    @Override
    public void retrieveProduct(String productId, Handler<AsyncResult<Product>> resultHandler) {
        this.fetchOne(Integer.parseInt(productId), FETCH_STATEMENT)
                .map(option -> option.map(Product::new).orElse(null))
                .onComplete(resultHandler);
    }

    @Override
    public void retrieveAllProducts(Handler<AsyncResult<List<Product>>> resultHandler) {
        this.fetchMany(FETCH_ALL_STATEMENT)
                .map(rawList -> rawList.stream()
                        .map(Product::new)
                        .collect(Collectors.toList())
                )
                .onComplete(resultHandler);
    }

    @Override
    public void deleteProduct(String productId, Handler<AsyncResult<Void>> resultHandler) {
        this.delete(Integer.parseInt(productId), DELETE_STATEMENT, resultHandler);
    }
}
