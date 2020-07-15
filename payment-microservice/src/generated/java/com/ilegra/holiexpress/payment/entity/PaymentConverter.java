package com.ilegra.holiexpress.payment.entity;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.ilegra.holiexpress.payment.entity.Payment}.
 * NOTE: This class has been automatically generated from the {@link com.ilegra.holiexpress.payment.entity.Payment} original class using Vert.x codegen.
 */
public class PaymentConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, Payment obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "orderId":
          if (member.getValue() instanceof Number) {
            obj.setOrderId(((Number)member.getValue()).intValue());
          }
          break;
        case "status":
          if (member.getValue() instanceof String) {
            obj.setStatus((String)member.getValue());
          }
          break;
        case "transactionId":
          if (member.getValue() instanceof String) {
            obj.setTransactionId((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(Payment obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(Payment obj, java.util.Map<String, Object> json) {
    json.put("orderId", obj.getOrderId());
    if (obj.getStatus() != null) {
      json.put("status", obj.getStatus());
    }
    if (obj.getTransactionId() != null) {
      json.put("transactionId", obj.getTransactionId());
    }
  }
}
