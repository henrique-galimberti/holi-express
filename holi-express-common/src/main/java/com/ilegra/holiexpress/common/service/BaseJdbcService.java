package com.ilegra.holiexpress.common.service;

import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;
import java.util.Optional;

public class BaseJdbcService {

    protected final JDBCClient client;

    public BaseJdbcService(Vertx vertx, JsonObject config) {
        this.client = JDBCClient.create(vertx, config.getJsonObject("jdbc-config"));
    }

    protected void executeCommand(JsonArray params, String sql, Handler<AsyncResult<Void>> resultHandler) {
        client.getConnection(connHandler(resultHandler, connection -> connection.updateWithParams(sql, params, r -> {
            if (r.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            } else {
                resultHandler.handle(Future.failedFuture(r.cause()));
            }
            connection.close();
        })));
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
                    promise.fail(queryResultHandler.cause());
                }
                connection.close();
            });
            return promise.future();
        });
    }

    protected <K> void delete(K id, String sql, Handler<AsyncResult<Void>> resultHandler) {
        client.getConnection(connHandler(resultHandler, connection -> {
            JsonArray params = new JsonArray().add(id);
            connection.updateWithParams(sql, params, r -> {
                if (r.succeeded()) {
                    resultHandler.handle(Future.succeededFuture());
                } else {
                    resultHandler.handle(Future.failedFuture(r.cause()));
                }
                connection.close();
            });
        }));
    }

    protected <R> Handler<AsyncResult<SQLConnection>> connHandler(Handler<AsyncResult<R>> failureHandler, Handler<SQLConnection> successHandler) {
        return conn -> {
            if (conn.succeeded()) {
                final SQLConnection connection = conn.result();
                successHandler.handle(connection);
            } else {
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

