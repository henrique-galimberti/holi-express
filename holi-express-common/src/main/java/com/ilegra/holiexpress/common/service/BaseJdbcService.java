package com.ilegra.holiexpress.common.service;

import com.ilegra.holiexpress.common.BaseMicroserviceVerticle;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;
import java.util.Optional;

public class BaseJdbcService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseMicroserviceVerticle.class);

    protected final JDBCClient client;

    public BaseJdbcService(Vertx vertx, JsonObject config) {
        this.client = JDBCClient.create(vertx, config.getJsonObject("jdbc-config", new JsonObject()));
    }

    protected void execute(JsonArray params, String sql, Handler<AsyncResult<Void>> resultHandler) {
        client.getConnection(connHandler(resultHandler, connection -> connection.updateWithParams(sql, params, r -> {
            if (r.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            } else {
                LOGGER.error("Failed to execute query", r.cause());
                resultHandler.handle(Future.failedFuture(r.cause()));
            }
            connection.close();
        })));
    }

    protected Future<JsonArray> insert(JsonArray params, String sql) {
        return getConnection()
                .compose(connection -> {
                    Promise<JsonArray> promise = Promise.promise();
                    connection.updateWithParams(sql, params, queryResultHandler -> {
                        if (queryResultHandler.succeeded()) {
                            promise.complete(queryResultHandler.result().getKeys());
                        } else {
                            LOGGER.error("Failed to execute insert", queryResultHandler.cause());
                            promise.fail(queryResultHandler.cause());
                        }
                        connection.close();
                    });
                    return promise.future();
                });
    }

    protected <K> Future<Optional<JsonObject>> fetchOne(K param, String sql) {
        return getConnection()
                .compose(connection -> {
                    Promise<Optional<JsonObject>> promise = Promise.promise();
                    connection.queryWithParams(sql, new JsonArray().add(param), queryResultHandler -> {
                        if (queryResultHandler.succeeded()) {
                            List<JsonObject> rows = queryResultHandler.result().getRows();
                            if (rows == null || rows.isEmpty()) {
                                promise.complete(Optional.empty());
                            } else {
                                promise.complete(Optional.of(rows.get(0)));
                            }
                        } else {
                            LOGGER.error("Failed to fetch query result", queryResultHandler.cause());
                            promise.fail(queryResultHandler.cause());
                        }
                        connection.close();
                    });
                    return promise.future();
                });
    }

    protected Future<List<JsonObject>> fetchMany(JsonArray params, String sql) {
        return getConnection().compose(connection -> {
            Promise<List<JsonObject>> promise = Promise.promise();
            connection.queryWithParams(sql, params, queryResultHandler -> {
                if (queryResultHandler.succeeded()) {
                    promise.complete(queryResultHandler.result().getRows());
                } else {
                    LOGGER.error("Failed to fetch query result", queryResultHandler.cause());
                    promise.fail(queryResultHandler.cause());
                }
                connection.close();
            });
            return promise.future();
        });
    }

    protected Future<List<JsonObject>> fetchMany(String sql) {
        return getConnection().compose(connection -> {
            Promise<List<JsonObject>> promise = Promise.promise();
            connection.query(sql, queryResultHandler -> {
                if (queryResultHandler.succeeded()) {
                    promise.complete(queryResultHandler.result().getRows());
                } else {
                    LOGGER.error("Failed to fetch query result", queryResultHandler.cause());
                    promise.fail(queryResultHandler.cause());
                }
                connection.close();
            });
            return promise.future();
        });
    }

    protected <R> Handler<AsyncResult<SQLConnection>> connHandler(Handler<AsyncResult<R>> failureHandler, Handler<SQLConnection> successHandler) {
        return conn -> {
            if (conn.succeeded()) {
                final SQLConnection connection = conn.result();
                successHandler.handle(connection);
            } else {
                LOGGER.error("Failed to establish connection to jdbc", conn.cause());
                failureHandler.handle(Future.failedFuture(conn.cause()));
            }
        };
    }

    protected Future<SQLConnection> getConnection() {
        Promise<SQLConnection> promise = Promise.promise();
        client.getConnection(promise);
        return promise.future();
    }
}

