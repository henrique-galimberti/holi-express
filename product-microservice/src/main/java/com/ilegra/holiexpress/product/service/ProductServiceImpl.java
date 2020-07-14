package com.ilegra.holiexpress.product.service;

import com.ilegra.holiexpress.common.service.BaseJdbcService;
import com.ilegra.holiexpress.product.entity.Product;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;
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
    private static final String COMPARE_STATEMENT = "SELECT * FROM products WHERE type = ? and price < ? and id <> ? limit 500";

    public ProductServiceImpl(Vertx vertx, JsonObject config) {
        super(vertx, config);
    }

    @Override
    public void initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
        client.getConnection(connHandler(resultHandler,
                connection -> connection.execute(CREATE_STATEMENT, r -> {
                    resultHandler.handle(r);
                    connection.close();
                })));
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
    public void compareSimilarProducts(Product product, Handler<AsyncResult<List<Product>>> resultHandler) {
        this.fetchMany(new JsonArray()
                .add(product.getType())
                .add(product.getPrice())
                .add(product.getId()), COMPARE_STATEMENT)
                .map(rawList -> rawList.stream()
                        .map(Product::new)
                        .collect(Collectors.toList())
                )
                .onComplete(asyncResult -> {
                    if (asyncResult.succeeded()) {
                        Promise<List<Product>> promise = Promise.promise();

                        List<String> referenceWords = Arrays.asList(product.getName().split(" "));

                        Map<Integer, Integer> mapCountMatchingWords = asyncResult.result()
                                .stream()
                                .collect(Collectors.toMap(Product::getId, p -> {
                                    List<String> tempReferenceWords = new ArrayList<>(referenceWords);

                                    wordsLoop:
                                    for (String word : p.getName().split(" ")) {
                                        for (Iterator<String> it = tempReferenceWords.iterator(); it.hasNext(); ) {
                                            if (it.next().toLowerCase().equals(word.toLowerCase())) {
                                                it.remove();
                                                continue wordsLoop;
                                            }
                                        }
                                    }

                                    return referenceWords.size() - tempReferenceWords.size();
                                }));

                        List<Product> similarProducts = asyncResult.result()
                                .stream()
                                .sorted(Comparator.comparing(p -> mapCountMatchingWords.get(p.getId())))
                                .limit(10)
                                .collect(Collectors.toList());

                        promise.complete(similarProducts);

                        resultHandler.handle(promise.future());
                    } else {
                        resultHandler.handle(asyncResult);
                    }
                });
    }
}
