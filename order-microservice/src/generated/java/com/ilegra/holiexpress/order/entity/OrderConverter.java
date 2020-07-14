package com.ilegra.holiexpress.order.entity;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.ilegra.holiexpress.order.entity.Order}.
 * NOTE: This class has been automatically generated from the {@link com.ilegra.holiexpress.order.entity.Order} original class using Vert.x codegen.
 */
public class OrderConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, Order obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "buyerId":
          if (member.getValue() instanceof Number) {
            obj.setBuyerId(((Number)member.getValue()).intValue());
          }
          break;
        case "id":
          if (member.getValue() instanceof Number) {
            obj.setId(((Number)member.getValue()).intValue());
          }
          break;
        case "productId":
          if (member.getValue() instanceof Number) {
            obj.setProductId(((Number)member.getValue()).intValue());
          }
          break;
        case "status":
          if (member.getValue() instanceof String) {
            obj.setStatus((String)member.getValue());
          }
          break;
        case "value":
          if (member.getValue() instanceof Number) {
            obj.setValue(((Number)member.getValue()).doubleValue());
          }
          break;
      }
    }
  }

  public static void toJson(Order obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(Order obj, java.util.Map<String, Object> json) {
    json.put("buyerId", obj.getBuyerId());
    json.put("id", obj.getId());
    json.put("productId", obj.getProductId());
    if (obj.getStatus() != null) {
      json.put("status", obj.getStatus());
    }
    json.put("value", obj.getValue());
  }
}
