package com.ilegra.holiexpress.product.entity;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.ilegra.holiexpress.product.entity.Product}.
 * NOTE: This class has been automatically generated from the {@link com.ilegra.holiexpress.product.entity.Product} original class using Vert.x codegen.
 */
public class ProductConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, Product obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "id":
          if (member.getValue() instanceof Number) {
            obj.setId(((Number)member.getValue()).intValue());
          }
          break;
        case "image":
          if (member.getValue() instanceof String) {
            obj.setImage((String)member.getValue());
          }
          break;
        case "name":
          if (member.getValue() instanceof String) {
            obj.setName((String)member.getValue());
          }
          break;
        case "price":
          if (member.getValue() instanceof Number) {
            obj.setPrice(((Number)member.getValue()).doubleValue());
          }
          break;
        case "sellerId":
          if (member.getValue() instanceof Number) {
            obj.setSellerId(((Number)member.getValue()).intValue());
          }
          break;
        case "type":
          if (member.getValue() instanceof String) {
            obj.setType((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(Product obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(Product obj, java.util.Map<String, Object> json) {
    json.put("id", obj.getId());
    if (obj.getImage() != null) {
      json.put("image", obj.getImage());
    }
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
    json.put("price", obj.getPrice());
    json.put("sellerId", obj.getSellerId());
    if (obj.getType() != null) {
      json.put("type", obj.getType());
    }
  }
}
