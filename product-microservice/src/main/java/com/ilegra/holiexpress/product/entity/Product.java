package com.ilegra.holiexpress.product.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Product {

    private int id;
    private int sellerId;
    private String name;
    private double price = 0.0d;
    private String image;
    private String type;

    public Product() {
    }

    public Product(Product other) {
        this.id = other.id;
        this.sellerId = other.sellerId;
        this.name = other.name;
        this.price = other.price;
        this.image = other.image;
        this.type = other.type;
    }

    public Product(JsonObject json) {
        ProductConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        ProductConverter.toJson(this, json);
        return json;
    }

    public int getId() {
        return id;
    }

    public Product setId(int id) {
        this.id = id;
        return this;
    }

    public int getSellerId() {
        return sellerId;
    }

    public Product setSellerId(int sellerId) {
        this.sellerId = sellerId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Product setPrice(double price) {
        this.price = price;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Product setImage(String image) {
        this.image = image;
        return this;
    }

    public String getType() {
        return type;
    }

    public Product setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return id == product.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return this.toJson().encodePrettily();
    }
}
