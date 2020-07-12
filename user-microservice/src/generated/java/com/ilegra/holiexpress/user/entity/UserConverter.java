package com.ilegra.holiexpress.user.entity;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.ilegra.holiexpress.user.entity.User}.
 * NOTE: This class has been automatically generated from the {@link com.ilegra.holiexpress.user.entity.User} original class using Vert.x codegen.
 */
public class UserConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, User obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "id":
          if (member.getValue() instanceof Number) {
            obj.setId(((Number)member.getValue()).intValue());
          }
          break;
        case "name":
          if (member.getValue() instanceof String) {
            obj.setName((String)member.getValue());
          }
          break;
        case "password":
          if (member.getValue() instanceof String) {
            obj.setPassword((String)member.getValue());
          }
          break;
        case "password_salt":
          if (member.getValue() instanceof String) {
            obj.setPassword_salt((String)member.getValue());
          }
          break;
        case "username":
          if (member.getValue() instanceof String) {
            obj.setUsername((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(User obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(User obj, java.util.Map<String, Object> json) {
    json.put("id", obj.getId());
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
    if (obj.getPassword() != null) {
      json.put("password", obj.getPassword());
    }
    if (obj.getPassword_salt() != null) {
      json.put("password_salt", obj.getPassword_salt());
    }
    if (obj.getUsername() != null) {
      json.put("username", obj.getUsername());
    }
  }
}
