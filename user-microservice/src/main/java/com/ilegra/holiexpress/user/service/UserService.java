package com.ilegra.holiexpress.user.service;

import com.ilegra.holiexpress.user.entity.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

public interface UserService {

    void initializePersistence(Handler<AsyncResult<Void>> resultHandler);

    void addUser(User user, Handler<AsyncResult<Void>> resultHandler);

    void retrieveUser(String productId, Handler<AsyncResult<User>> resultHandler);

    void retrieveAllUsers(Handler<AsyncResult<List<User>>> resultHandler);
}
