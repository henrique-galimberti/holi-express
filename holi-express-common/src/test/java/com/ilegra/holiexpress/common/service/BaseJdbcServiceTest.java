package com.ilegra.holiexpress.common.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
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
import java.util.Optional;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({JDBCClient.class})
public class BaseJdbcServiceTest {

    private JDBCClient mockedJDBCClient;
    private BaseJdbcService service;
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

        service = new BaseJdbcService(null, new JsonObject());
    }

    @Test
    public void fetch(TestContext ctx) {
        JsonObject expected = new JsonObject()
                .put("foo1", 1)
                .put("foo2", 2);

        List<JsonObject> expectedList = new ArrayList<>();
        expectedList.add(expected);
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.getRows()).thenReturn(expectedList);
        AsyncResult<ResultSet> asyncResultResultSet = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultResultSet.succeeded()).thenReturn(true);
        Mockito.when(asyncResultResultSet.result()).thenReturn(resultSet);
        Mockito.doAnswer((Answer<AsyncResult<ResultSet>>) arg0 -> {
            ((Handler<AsyncResult<ResultSet>>) arg0.getArgument(2)).handle(asyncResultResultSet);
            return null;
        }).when(sqlConnection).queryWithParams(Mockito.any(), Mockito.any(), Mockito.any());

        Async fetchOneAsync = ctx.async();

        service.fetchOne(new JsonArray(), "select * from foo").onComplete(asyncResult -> {
            ctx.assertTrue(asyncResult.succeeded());

            Optional<JsonObject> result = asyncResult.result();

            ctx.assertTrue(result.isPresent());
            ctx.assertEquals(result.get(), expected);

            fetchOneAsync.complete();
        });

        fetchOneAsync.await(5000);

        Async fetchManyAsync = ctx.async();

        service.fetchMany(new JsonArray(), "select * from foo").onComplete(asyncResult -> {
            ctx.assertTrue(asyncResult.succeeded());

            List<JsonObject> result = asyncResult.result();

            ctx.assertEquals(expectedList.size(), result.size());
            ctx.assertEquals(expectedList.get(0), result.get(0));

            fetchManyAsync.complete();
        });

        fetchManyAsync.await(5000);
    }

    @Test
    public void execute(TestContext ctx) {
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        AsyncResult<UpdateResult> asyncResultUpdateResult = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultUpdateResult.succeeded()).thenReturn(true);
        Mockito.when(asyncResultUpdateResult.result()).thenReturn(updateResult);
        Mockito.doAnswer((Answer<AsyncResult<UpdateResult>>) arg0 -> {
            ((Handler<AsyncResult<UpdateResult>>) arg0.getArgument(2)).handle(asyncResultUpdateResult);
            return null;
        }).when(sqlConnection).updateWithParams(Mockito.any(), Mockito.any(), Mockito.any());

        Async async = ctx.async();

        service.execute(new JsonArray(), "update foo set bar = 'bar'", asyncResult -> {
            ctx.assertTrue(asyncResult.succeeded());

            async.complete();
        });

        async.await(5000);
    }

    @Test
    public void insert(TestContext ctx) {
        JsonArray expected = new JsonArray().add(1).add(2);

        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        AsyncResult<UpdateResult> asyncResultUpdateResult = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultUpdateResult.succeeded()).thenReturn(true);
        Mockito.when(asyncResultUpdateResult.result()).thenReturn(updateResult);
        Mockito.when(updateResult.getKeys()).thenReturn(expected);
        Mockito.doAnswer((Answer<AsyncResult<UpdateResult>>) arg0 -> {
            ((Handler<AsyncResult<UpdateResult>>) arg0.getArgument(2)).handle(asyncResultUpdateResult);
            return null;
        }).when(sqlConnection).updateWithParams(Mockito.any(), Mockito.any(), Mockito.any());

        Async async = ctx.async();

        service.insert(new JsonArray(), "insert into foo values ( 1, 2 )").onComplete(asyncResult -> {
            ctx.assertTrue(asyncResult.succeeded());

            ctx.assertEquals(expected, asyncResult.result());

            async.complete();
        });

        async.await(5000);
    }
}
