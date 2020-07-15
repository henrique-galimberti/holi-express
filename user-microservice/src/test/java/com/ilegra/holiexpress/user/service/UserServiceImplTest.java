package com.ilegra.holiexpress.user.service;

import com.ilegra.holiexpress.user.entity.User;
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

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({JDBCClient.class})
public class UserServiceImplTest {

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
    public void addUser(TestContext testContext) {
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

        UserServiceImpl service = new UserServiceImpl(vertx, new JsonObject());

        User user = new User();
        user.setName("Naruto");
        user.setUsername("naruto.uzumaki");
        user.setPassword("sakura");
        user.setPassword_salt("sakura_salt");

        Async async = testContext.async();
        service.addUser(user, asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            testContext.assertEquals(user.getId(), expected.getInteger(0));
            async.complete();
        });
        async.await(5000);
    }

    @Test
    public void retrieveUser(TestContext testContext) {
        List<JsonObject> expected = new ArrayList<>();

        User user = new User();
        user.setId(6);
        user.setName("Naruto");
        user.setUsername("naruto.uzumaki");
        user.setPassword("sakura");
        user.setPassword_salt("sakura_salt");

        expected.add(user.toJson());

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

        UserServiceImpl service = new UserServiceImpl(vertx, new JsonObject());

        Async async = testContext.async();
        service.retrieveUser("6", asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            User result = asyncResult.result();
            testContext.assertEquals(user.toString(), result.toString());
            async.complete();
        });
        async.await(5000);
    }

    @Test
    public void retrieveAllUsers(TestContext testContext) {
        List<JsonObject> expected = new ArrayList<>();

        User user = new User();
        user.setId(6);
        user.setName("Naruto");
        user.setUsername("naruto.uzumaki");
        user.setPassword("sakura");
        user.setPassword_salt("sakura_salt");

        expected.add(user.toJson());
        expected.add(user.toJson());
        expected.add(user.toJson());

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

        UserServiceImpl service = new UserServiceImpl(vertx, new JsonObject());

        Async async = testContext.async();
        service.retrieveAllUsers(asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            List<User> result = asyncResult.result();
            testContext.assertEquals(result.size(), expected.size());
            testContext.assertEquals(result.get(0).toString(), user.toString());
            async.complete();
        });
        async.await(5000);
    }
}
