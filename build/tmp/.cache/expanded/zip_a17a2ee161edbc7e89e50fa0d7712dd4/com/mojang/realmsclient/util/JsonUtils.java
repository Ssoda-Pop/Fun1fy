package com.mojang.realmsclient.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JsonUtils {
   public static <T> T getRequired(String p_275573_, JsonObject p_275650_, Function<JsonObject, T> p_275655_) {
      JsonElement jsonelement = p_275650_.get(p_275573_);
      if (jsonelement != null && !jsonelement.isJsonNull()) {
         if (!jsonelement.isJsonObject()) {
            throw new IllegalStateException("Required property " + p_275573_ + " was not a JsonObject as espected");
         } else {
            return p_275655_.apply(jsonelement.getAsJsonObject());
         }
      } else {
         throw new IllegalStateException("Missing required property: " + p_275573_);
      }
   }

   public static String getRequiredString(String p_275692_, JsonObject p_275706_) {
      String s = getStringOr(p_275692_, p_275706_, (String)null);
      if (s == null) {
         throw new IllegalStateException("Missing required property: " + p_275692_);
      } else {
         return s;
      }
   }

   @Nullable
   public static String getStringOr(String pKey, JsonObject pJson, @Nullable String pDefaultValue) {
      JsonElement jsonelement = pJson.get(pKey);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsString();
      } else {
         return pDefaultValue;
      }
   }

   @Nullable
   public static UUID getUuidOr(String p_275342_, JsonObject p_275515_, @Nullable UUID p_275232_) {
      String s = getStringOr(p_275342_, p_275515_, (String)null);
      return s == null ? p_275232_ : UUID.fromString(s);
   }

   public static int getIntOr(String pKey, JsonObject pJson, int pDefaultValue) {
      JsonElement jsonelement = pJson.get(pKey);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsInt();
      } else {
         return pDefaultValue;
      }
   }

   public static long getLongOr(String pKey, JsonObject pJson, long pDefaultValue) {
      JsonElement jsonelement = pJson.get(pKey);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsLong();
      } else {
         return pDefaultValue;
      }
   }

   public static boolean getBooleanOr(String pKey, JsonObject pJson, boolean pDefaultValue) {
      JsonElement jsonelement = pJson.get(pKey);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsBoolean();
      } else {
         return pDefaultValue;
      }
   }

   public static Date getDateOr(String pKey, JsonObject pJson) {
      JsonElement jsonelement = pJson.get(pKey);
      return jsonelement != null ? new Date(Long.parseLong(jsonelement.getAsString())) : new Date();
   }
}