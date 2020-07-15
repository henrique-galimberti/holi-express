package com.ilegra.holiexpress.user.entity;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
public class UserTest {

    @Test
    public void gettersAndSetters(TestContext testContext) {
        User user = new User();

        user.setId(6);
        testContext.assertEquals(6, user.getId());

        user.setName("Naruto");
        testContext.assertEquals("Naruto", user.getName());

        user.setUsername("naruto.uzumaki");
        testContext.assertEquals("naruto.uzumaki", user.getUsername());

        user.setPassword("sakura");
        testContext.assertEquals("sakura", user.getPassword());

        user.setPassword_salt("sakura_salt");
        testContext.assertEquals("sakura_salt", user.getPassword_salt());
    }

    @Test
    public void fromJson(TestContext testContext) {
        JsonObject json = new JsonObject()
                .put("id", 6)
                .put("name", "Naruto")
                .put("username", "naruto.uzumaki")
                .put("password", "sakura")
                .put("password_salt", "sakura_salt");

        User user = new User(json);

        testContext.assertEquals(user.getId(), json.getInteger("id"));
        testContext.assertEquals(user.getName(), json.getString("name"));
        testContext.assertEquals(user.getUsername(), json.getString("username"));
        testContext.assertEquals(user.getPassword(), json.getString("password"));
        testContext.assertEquals(user.getPassword_salt(), json.getString("password_salt"));
    }

    @Test
    public void toJson(TestContext testContext) {
        User user = new User();

        user.setId(6);
        user.setName("Naruto");
        user.setUsername("naruto.uzumaki");
        user.setPassword("sakura");
        user.setPassword_salt("sakura_salt");

        JsonObject json = user.toJson();

        testContext.assertEquals(user.getId(), json.getInteger("id"));
        testContext.assertEquals(user.getName(), json.getString("name"));
        testContext.assertEquals(user.getUsername(), json.getString("username"));
        testContext.assertEquals(user.getPassword(), json.getString("password"));
        testContext.assertEquals(user.getPassword_salt(), json.getString("password_salt"));
    }
}
