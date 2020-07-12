package com.ilegra.holiexpress.user.service;

import com.ilegra.holiexpress.common.service.BaseJdbcService;
import com.ilegra.holiexpress.user.entity.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class UserServiceImpl extends BaseJdbcService implements UserService {

    private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS user (\n" +
            "  id SERIAL,\n" +
            "  name varchar(255) NOT NULL,\n" +
            "  username varchar(255) NOT NULL,\n" +
            "  password varchar(255) NOT NULL,\n" +
            "  password_salt varchar(255) NOT NULL,\n" +
            "  PRIMARY KEY (id) )";
    private static final String INSERT_STATEMENT = "INSERT INTO user (name, username, password, password_salt) VALUES (?, ?, ?, ?)";
    private static final String FETCH_STATEMENT = "SELECT * FROM user WHERE id = ?";
    private static final String FETCH_ALL_STATEMENT = "SELECT * FROM user";
    private static final String DELETE_STATEMENT = "DELETE FROM user WHERE id = ?";

    public UserServiceImpl(Vertx vertx, JsonObject config) {
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
    public void addUser(User user, Handler<AsyncResult<Void>> resultHandler) {
        JsonArray params = new JsonArray()
                .add(user.getName())
                .add(user.getUsername())
                .add(user.getPassword())
                .add(user.getPassword_salt());
        insert(params, INSERT_STATEMENT).onComplete(asyncResult -> {
            user.setId(asyncResult.result().getInteger(0));
            resultHandler.handle(Future.succeededFuture());
        });
    }

    @Override
    public void retrieveUser(String userId, Handler<AsyncResult<User>> resultHandler) {
        this.fetchOne(Integer.parseInt(userId), FETCH_STATEMENT)
                .map(option -> option.map(User::new).orElse(null))
                .onComplete(resultHandler);
    }

    @Override
    public void retrieveAllUsers(Handler<AsyncResult<List<User>>> resultHandler) {
        this.fetchMany(FETCH_ALL_STATEMENT)
                .map(rawList -> rawList.stream()
                        .map(User::new)
                        .collect(Collectors.toList())
                )
                .onComplete(resultHandler);
    }

    @Override
    public void deleteUser(String userId, Handler<AsyncResult<Void>> resultHandler) {
        this.delete(Integer.parseInt(userId), DELETE_STATEMENT, resultHandler);
    }
}
