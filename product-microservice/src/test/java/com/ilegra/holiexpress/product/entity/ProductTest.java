package com.ilegra.holiexpress.product.entity;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
public class ProductTest {

    @Test
    public void gettersAndSetters(TestContext testContext) {
        Product product = new Product();

        product.setId(6);
        testContext.assertEquals(6, product.getId());

        product.setImage("image_content");
        testContext.assertEquals("image_content", product.getImage());

        product.setName("product");
        testContext.assertEquals("product", product.getName());

        product.setPrice(999.99);
        testContext.assertEquals(999.99, product.getPrice());

        product.setSellerId(66);
        testContext.assertEquals(66, product.getSellerId());

        product.setType("type");
        testContext.assertEquals("type", product.getType());
    }

    @Test
    public void fromJson(TestContext testContext) {
        JsonObject json = new JsonObject()
                .put("id", 6)
                .put("image", "some_image")
                .put("name", "yyy")
                .put("price", 666.66)
                .put("sellerId", 66)
                .put("type", "xxx");

        Product product = new Product(json);

        testContext.assertEquals(product.getId(), json.getInteger("id"));
        testContext.assertEquals(product.getImage(), json.getString("image"));
        testContext.assertEquals(product.getName(), json.getString("name"));
        testContext.assertEquals(product.getPrice(), json.getDouble("price"));
        testContext.assertEquals(product.getSellerId(), json.getInteger("sellerId"));
        testContext.assertEquals(product.getType(), json.getString("type"));
    }

    @Test
    public void toJson(TestContext testContext) {
        Product product = new Product();

        product.setId(6);
        product.setImage("image_content");
        product.setName("product");
        product.setPrice(999.99);
        product.setSellerId(66);
        product.setType("type");

        JsonObject json = product.toJson();

        testContext.assertEquals(product.getId(), json.getInteger("id"));
        testContext.assertEquals(product.getImage(), json.getString("image"));
        testContext.assertEquals(product.getName(), json.getString("name"));
        testContext.assertEquals(product.getPrice(), json.getDouble("price"));
        testContext.assertEquals(product.getSellerId(), json.getInteger("sellerId"));
        testContext.assertEquals(product.getType(), json.getString("type"));
    }
}
