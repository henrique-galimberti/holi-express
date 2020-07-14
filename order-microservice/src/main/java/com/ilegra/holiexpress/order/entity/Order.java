package com.ilegra.holiexpress.order.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Order {

    public static final String STATUS_WAITING_PAYMENT = "waiting_payment";
    public static final String STATUS_DELIVERING_PROCESS = "delivering_process";
    public static final String STATUS_FINISHED = "finished";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_ERROR = "payment_error";

    private int id;
    private int buyerId;
    private int productId;
    private double value;
    private String status;

    public Order() {
    }

    public Order(Order other) {
        this.id = other.id;
        this.buyerId = other.buyerId;
        this.productId = other.productId;
        this.value = other.value;
        this.status = other.status;
    }

    public Order(JsonObject json) {
        OrderConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        OrderConverter.toJson(this, json);
        return json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return id == order.id;
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
