package com.ilegra.holiexpress.payment;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
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

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({JDBCClient.class})
public class PaymentVerticleTest {

    private String deploymentId;

    @Before
    public void mockJDBC() {
        JDBCClient mockedJDBCClient = Mockito.mock(JDBCClient.class);
        PowerMockito.mockStatic(JDBCClient.class);
        PowerMockito.when(JDBCClient.create(Mockito.any(), (JsonObject) Mockito.any())).thenReturn(mockedJDBCClient);

        SQLConnection sqlConnection = Mockito.mock(SQLConnection.class);

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
    public void start_stop(TestContext testContext) {
        Vertx vertx = Vertx.vertx();

        PaymentVerticle verticle = new PaymentVerticle();

        Async asyncStart = testContext.async();
        vertx.deployVerticle(verticle, asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            deploymentId = asyncResult.result();
            asyncStart.complete();
        });
        asyncStart.await(5000);

        Async asyncStop = testContext.async();
        vertx.undeploy(deploymentId, asyncResult -> {
            testContext.assertTrue(asyncResult.succeeded());
            asyncStop.complete();
        });
        asyncStop.await(5000);
    }
}
