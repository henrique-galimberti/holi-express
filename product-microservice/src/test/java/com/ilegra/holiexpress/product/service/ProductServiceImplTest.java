package com.ilegra.holiexpress.product.service;

import com.ilegra.holiexpress.product.entity.Product;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({JDBCClient.class})
public class ProductServiceImplTest {

    private JDBCClient mockedJDBCClient;
    private SQLConnection sqlConnection;

    @Before
    public void mockJDBC() {
        mockedJDBCClient = Mockito.mock(JDBCClient.class);
        PowerMockito.mockStatic(JDBCClient.class);
        PowerMockito.when(JDBCClient.create(Mockito.any(), (JsonObject) Mockito.any())).thenReturn(mockedJDBCClient);

        sqlConnection = Mockito.mock(SQLConnection.class);

        AsyncResult<SQLConnection> sqlConnectionResult = Mockito.mock(AsyncResult.class);
        Mockito.when(sqlConnectionResult.succeeded()).thenReturn(true);
        Mockito.when(sqlConnectionResult.result()).thenReturn(sqlConnection);
        Mockito.doAnswer((Answer<AsyncResult<SQLConnection>>) arg0 -> {
            ((Handler<AsyncResult<SQLConnection>>) arg0.getArgument(0)).handle(sqlConnectionResult);
            return null;
        }).when(mockedJDBCClient).getConnection(Mockito.any());

        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        AsyncResult<UpdateResult> asyncResultUpdateResult = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultUpdateResult.succeeded()).thenReturn(true);
        Mockito.when(asyncResultUpdateResult.result()).thenReturn(updateResult);
        Mockito.doAnswer((Answer<AsyncResult<UpdateResult>>) arg0 -> {
            ((Handler<AsyncResult<UpdateResult>>) arg0.getArgument(1)).handle(asyncResultUpdateResult);
            return null;
        }).when(sqlConnection).execute(Mockito.any(), Mockito.any());
    }

    @Test
    public void addProduct(TestContext testContext) {
        JsonArray expected = new JsonArray().add(666);

        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        AsyncResult<UpdateResult> asyncResultUpdateResult = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultUpdateResult.succeeded()).thenReturn(true);
        Mockito.when(asyncResultUpdateResult.result()).thenReturn(updateResult);
        Mockito.when(updateResult.getKeys()).thenReturn(expected);
        Mockito.doAnswer((Answer<AsyncResult<UpdateResult>>) arg0 -> {
            ((Handler<AsyncResult<UpdateResult>>) arg0.getArgument(2)).handle(asyncResultUpdateResult);
            return null;
        }).when(sqlConnection).updateWithParams(Mockito.any(), Mockito.any(), Mockito.any());

        Vertx vertx = Vertx.vertx();

        ProductServiceImpl service = new ProductServiceImpl(vertx, new JsonObject());

        Product product = new Product();
        product.setImage("image_content");
        product.setName("product");
        product.setPrice(999.99);
        product.setSellerId(66);
        product.setType("type");

        Async async = testContext.async();
        service.addProduct(product, asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            testContext.assertEquals(product.getId(), expected.getInteger(0));
            async.complete();
        });
        async.await(5000);
    }

    @Test
    public void retrieveProduct(TestContext testContext) {
        List<JsonObject> expected = new ArrayList<>();

        Product product = new Product();
        product.setId(6);
        product.setImage("image_content");
        product.setName("product");
        product.setPrice(999.99);
        product.setSellerId(66);
        product.setType("type");

        expected.add(product.toJson());

        ResultSet resultSet = Mockito.mock(ResultSet.class);
        AsyncResult<ResultSet> asyncResultResultSet = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultResultSet.succeeded()).thenReturn(true);
        Mockito.when(asyncResultResultSet.result()).thenReturn(resultSet);
        Mockito.when(resultSet.getRows()).thenReturn(expected);
        Mockito.doAnswer((Answer<AsyncResult<ResultSet>>) arg0 -> {
            ((Handler<AsyncResult<ResultSet>>) arg0.getArgument(2)).handle(asyncResultResultSet);
            return null;
        }).when(sqlConnection).queryWithParams(Mockito.any(), Mockito.any(), Mockito.any());

        Vertx vertx = Vertx.vertx();

        ProductServiceImpl service = new ProductServiceImpl(vertx, new JsonObject());

        Async async = testContext.async();
        service.retrieveProduct("6", asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            Product result = asyncResult.result();
            testContext.assertEquals(product.toString(), result.toString());
            async.complete();
        });
        async.await(5000);
    }

    @Test
    public void retrieveProducts(TestContext testContext) {
        List<JsonObject> expected = new ArrayList<>();

        Product product = new Product();
        product.setId(6);
        product.setImage("image_content");
        product.setName("product");
        product.setPrice(999.99);
        product.setSellerId(66);
        product.setType("type");

        expected.add(product.toJson());
        expected.add(product.toJson());
        expected.add(product.toJson());

        ResultSet resultSet = Mockito.mock(ResultSet.class);
        AsyncResult<ResultSet> asyncResultResultSet = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultResultSet.succeeded()).thenReturn(true);
        Mockito.when(asyncResultResultSet.result()).thenReturn(resultSet);
        Mockito.when(resultSet.getRows()).thenReturn(expected);
        Mockito.doAnswer((Answer<AsyncResult<ResultSet>>) arg0 -> {
            ((Handler<AsyncResult<ResultSet>>) arg0.getArgument(1)).handle(asyncResultResultSet);
            return null;
        }).when(sqlConnection).query(Mockito.any(), Mockito.any());

        Vertx vertx = Vertx.vertx();

        ProductServiceImpl service = new ProductServiceImpl(vertx, new JsonObject());

        Async async = testContext.async();
        service.retrieveAllProducts(asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            List<Product> result = asyncResult.result();
            testContext.assertEquals(result.size(), expected.size());
            testContext.assertEquals(result.get(0).toString(), product.toString());
            async.complete();
        });
        async.await(5000);
    }

    @Test
    public void compareSimilarProducts(TestContext testContext) {
        Product product = new Product();
        product.setId(6);
        product.setImage("some_image");
        product.setName("Lorem ipsum dolor sit amet");
        product.setPrice(999.99);
        product.setSellerId(66);
        product.setType("type");

        Product mostSimilarProduct = new Product(product);
        mostSimilarProduct.setName("Lorem ipsum dolor sit");

        Product secondMostSimilarProduct = new Product(product);
        secondMostSimilarProduct.setName("Lorem ipsum dolor");

        Product randomProduct = new Product(product);
        randomProduct.setName("random product");

        List<Product> products = new ArrayList<>();

        products.add(randomProduct);
        products.add(randomProduct);
        products.add(randomProduct);
        products.add(randomProduct);
        products.add(randomProduct);
        products.add(randomProduct);
        products.add(randomProduct);
        products.add(mostSimilarProduct);
        products.add(randomProduct);
        products.add(randomProduct);
        products.add(randomProduct);
        products.add(secondMostSimilarProduct);

        AtomicInteger id = new AtomicInteger(1);

        List<JsonObject> expected = products.stream().map(p -> {
            p.setId(id.getAndIncrement());
            return p.toJson();
        }).collect(Collectors.toList());

        ResultSet resultSet = Mockito.mock(ResultSet.class);
        AsyncResult<ResultSet> asyncResultResultSet = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultResultSet.succeeded()).thenReturn(true);
        Mockito.when(asyncResultResultSet.result()).thenReturn(resultSet);
        Mockito.when(resultSet.getRows()).thenReturn(expected);
        Mockito.doAnswer((Answer<AsyncResult<ResultSet>>) arg0 -> {
            ((Handler<AsyncResult<ResultSet>>) arg0.getArgument(2)).handle(asyncResultResultSet);
            return null;
        }).when(sqlConnection).queryWithParams(Mockito.any(), Mockito.any(), Mockito.any());

        Vertx vertx = Vertx.vertx();

        ProductServiceImpl service = new ProductServiceImpl(vertx, new JsonObject());

        Async async = testContext.async();
        service.compareSimilarProducts(product, asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            List<Product> result = asyncResult.result();
            testContext.assertEquals(result.size(), 10);
            testContext.assertEquals(result.get(0).toString(), mostSimilarProduct.toString());
            testContext.assertEquals(result.get(1).toString(), secondMostSimilarProduct.toString());
            async.complete();
        });
        async.await(5000);
    }
}
