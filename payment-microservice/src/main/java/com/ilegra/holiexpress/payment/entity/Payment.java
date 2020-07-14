package com.ilegra.holiexpress.payment.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Payment {
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_FINISHED = "finished";
    public static final String STATUS_ERROR = "error";

    private String checkoutId;
    private int orderId;
    private String status;

    public Payment() {
    }

    public Payment(Payment other) {
        this.checkoutId = other.checkoutId;
        this.orderId = other.orderId;
        this.status = other.status;
    }

    public Payment(JsonObject json) {
        PaymentConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        PaymentConverter.toJson(this, json);
        return json;
    }

    public String getId() {
        return checkoutId;
    }

    public void setId(String id) {
        this.checkoutId = checkoutId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

        Payment payment = (Payment) o;

        return checkoutId.equals(payment.checkoutId);
    }

    @Override
    public int hashCode() {
        return checkoutId.hashCode();
    }

    @Override
    public String toString() {
        return this.toJson().encodePrettily();
    }
}
