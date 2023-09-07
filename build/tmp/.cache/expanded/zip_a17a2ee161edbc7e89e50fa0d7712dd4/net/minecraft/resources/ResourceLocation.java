package net.minecraft.resources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.lang.reflect.Type;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

/**
 * An immutable location of a resource, in terms of a path and namespace.
 * <p>
 * This is used as an identifier for a resource, usually for those housed in a {@link net.minecraft.core.Registry}, such
 * as blocks and items.
 * <p>
 * {@code minecraft} is always taken as the default namespace for a resource location when none is explicitly stated.
 * When using this for registering objects, this namespace <strong>should</strong> only be used for resources added by
 * Minecraft itself.
 * <p>
 * Generally, and by the implementation of {@link #toString()}, the string representation of this class is expressed in
 * the form {@code namespace:path}. The colon is also used as the default separator for parsing strings as a {@code
 * ResourceLocation}.
 * @see net.minecraft.resources.ResourceKey
 */
public class ResourceLocation implements Comparable<ResourceLocation> {
   public static final Codec<ResourceLocation> CODEC = Codec.STRING.comapFlatMap(ResourceLocation::read, ResourceLocation::toString).stable();
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("argument.id.invalid"));
   public static final char NAMESPACE_SEPARATOR = ':';
   public static final String DEFAULT_NAMESPACE = "minecraft";
   public static final String REALMS_NAMESPACE = "realms";
   private final String namespace;
   private final String path;

   protected ResourceLocation(String p_248791_, String p_249394_, @Nullable ResourceLocation.Dummy p_249089_) {
      this.namespace = p_248791_;
      this.path = p_249394_;
   }

   public ResourceLocation(String pNamespace, String pPath) {
      this(assertValidNamespace(pNamespace, pPath), assertValidPath(pNamespace, pPath), (ResourceLocation.Dummy)null);
   }

   private ResourceLocation(String[] pDecomposedLocation) {
      this(pDecomposedLocation[0], pDecomposedLocation[1]);
   }

   public ResourceLocation(String pLocation) {
      this(decompose(pLocation, ':'));
   }

   /**
    * Constructs a {@code ResourceLocation} from the specified {@code location}, split into a namespace and path by the
    * specified {@code separator} char.
    * <p>
    * If the {@code separator} char is not present in the {@code location}, the namespace defaults to {@code minecraft},
    * taking the {@code location} as the path.
    * @throws net.minecraft.ResourceLocationException if there is a non {@code [a-z0-9_.-]} character in the decomposed
    * namespace or a non {@code [a-z0-9/._-]} character in the decomposed path.
    * @see #tryParse(String)
    * @see #isValidResourceLocation(String)
    * @param pLocation the location string to parse as a {@code ResourceLocation}
    * @param pSeparator the separator to separate the namespace and path by. This should not be any of these characters:
    * {@code [a-z0-9/._-]}.
    */
   public static ResourceLocation of(String pLocation, char pSeparator) {
      return new ResourceLocation(decompose(pLocation, pSeparator));
   }

   /**
    * Attempts to parse the specified {@code location} as a {@code ResourceLocation} by splitting it into a
    * namespace and path by a colon.
    * <p>
    * If no colon is present in the {@code location}, the namespace defaults to {@code minecraft}, taking the {@code
    * location} as the path.
    * @return the parsed resource location; otherwise {@code null} if there is a non {@code [a-z0-9_.-]} character in
    * the decomposed namespace or a non {@code [a-z0-9/._-]} character in the decomposed path
    * @see #of(String, char)
    * @param pLocation the location string to try to parse as a {@code ResourceLocation}
    */
   @Nullable
   public static ResourceLocation tryParse(String pLocation) {
      try {
         return new ResourceLocation(pLocation);
      } catch (ResourceLocationException resourcelocationexception) {
         return null;
      }
   }

   @Nullable
   public static ResourceLocation tryBuild(String p_214294_, String p_214295_) {
      try {
         return new ResourceLocation(p_214294_, p_214295_);
      } catch (ResourceLocationException resourcelocationexception) {
         return null;
      }
   }

   protected static String[] decompose(String pLocation, char pSeparator) {
      String[] astring = new String[]{"minecraft", pLocation};
      int i = pLocation.indexOf(pSeparator);
      if (i >= 0) {
         astring[1] = pLocation.substring(i + 1);
         if (i >= 1) {
            astring[0] = pLocation.substring(0, i);
         }
      }

      return astring;
   }

   public static DataResult<ResourceLocation> read(String p_135838_) {
      try {
         return DataResult.success(new ResourceLocation(p_135838_));
      } catch (ResourceLocationException resourcelocationexception) {
         return DataResult.error(() -> {
            return "Not a valid resource location: " + p_135838_ + " " + resourcelocationexception.getMessage();
         });
      }
   }

   public String getPath() {
      return this.path;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public ResourceLocation withPath(String p_251088_) {
      return new ResourceLocation(this.namespace, assertValidPath(this.namespace, p_251088_), (ResourceLocation.Dummy)null);
   }

   public ResourceLocation withPath(UnaryOperator<String> p_250342_) {
      return this.withPath(p_250342_.apply(this.path));
   }

   public ResourceLocation withPrefix(String p_250620_) {
      return this.withPath(p_250620_ + this.path);
   }

   public ResourceLocation withSuffix(String p_266769_) {
      return this.withPath(this.path + p_266769_);
   }

   public String toString() {
      return this.namespace + ":" + this.path;
   }

   public boolean equals(Object pOther) {
      if (this == pOther) {
         return true;
      } else if (!(pOther instanceof ResourceLocation)) {
         return false;
      } else {
         ResourceLocation resourcelocation = (ResourceLocation)pOther;
         return this.namespace.equals(resourcelocation.namespace) && this.path.equals(resourcelocation.path);
      }
   }

   public int hashCode() {
      return 31 * this.namespace.hashCode() + this.path.hashCode();
   }

   public int compareTo(ResourceLocation pOther) {
      int i = this.path.compareTo(pOther.path);
      if (i == 0) {
         i = this.namespace.compareTo(pOther.namespace);
      }

      return i;
   }

   // Normal compare sorts by path first, this compares namespace first.
   public int compareNamespaced(ResourceLocation o) {
      int ret = this.namespace.compareTo(o.namespace);
      return ret != 0 ? ret : this.path.compareTo(o.path);
   }

   public String toDebugFileName() {
      return this.toString().replace('/', '_').replace(':', '_');
   }

   public String toLanguageKey() {
      return this.namespace + "." + this.path;
   }

   public String toShortLanguageKey() {
      return this.namespace.equals("minecraft") ? this.path : this.toLanguageKey();
   }

   public String toLanguageKey(String p_214297_) {
      return p_214297_ + "." + this.toLanguageKey();
   }

   public String toLanguageKey(String p_270871_, String p_270199_) {
      return p_270871_ + "." + this.toLanguageKey() + "." + p_270199_;
   }

   public static ResourceLocation read(StringReader pReader) throws CommandSyntaxException {
      int i = pReader.getCursor();

      while(pReader.canRead() && isAllowedInResourceLocation(pReader.peek())) {
         pReader.skip();
      }

      String s = pReader.getString().substring(i, pReader.getCursor());

      try {
         return new ResourceLocation(s);
      } catch (ResourceLocationException resourcelocationexception) {
         pReader.setCursor(i);
         throw ERROR_INVALID.createWithContext(pReader);
      }
   }

   public static boolean isAllowedInResourceLocation(char pCharacter) {
      return pCharacter >= '0' && pCharacter <= '9' || pCharacter >= 'a' && pCharacter <= 'z' || pCharacter == '_' || pCharacter == ':' || pCharacter == '/' || pCharacter == '.' || pCharacter == '-';
   }

   /**
    * @return {@code true} if the specified {@code path} is valid: consists only of {@code [a-z0-9/._-]} characters
    */
   public static boolean isValidPath(String pPath) {
      for(int i = 0; i < pPath.length(); ++i) {
         if (!validPathChar(pPath.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   /**
    * @return {@code true} if the specified {@code namespace} is valid: consists only of {@code [a-z0-9_.-]} characters
    */
   public static boolean isValidNamespace(String pNamespace) {
      for(int i = 0; i < pNamespace.length(); ++i) {
         if (!validNamespaceChar(pNamespace.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   private static String assertValidNamespace(String p_250769_, String p_249616_) {
      if (!isValidNamespace(p_250769_)) {
         throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + p_250769_ + ":" + p_249616_);
      } else {
         return p_250769_;
      }
   }

   public static boolean validPathChar(char pPathChar) {
      return pPathChar == '_' || pPathChar == '-' || pPathChar >= 'a' && pPathChar <= 'z' || pPathChar >= '0' && pPathChar <= '9' || pPathChar == '/' || pPathChar == '.';
   }

   public static boolean validNamespaceChar(char pNamespaceChar) {
      return pNamespaceChar == '_' || pNamespaceChar == '-' || pNamespaceChar >= 'a' && pNamespaceChar <= 'z' || pNamespaceChar >= '0' && pNamespaceChar <= '9' || pNamespaceChar == '.';
   }

   /**
    * Splits the specified {@code location} into a namespace and path by a colon, checking both are valid.
    * <p>
    * If no colon is present in the {@code location}, the namespace defaults to {@code minecraft}, taking the {@code
    * location} as the path.</p>
    * @return {@code true} if both the decomposed namespace and path are valid
    * @see #isValidPath(String)
    * @see #isValidNamespace(String)
    */
   public static boolean isValidResourceLocation(String pLocation) {
      String[] astring = decompose(pLocation, ':');
      return isValidNamespace(StringUtils.isEmpty(astring[0]) ? "minecraft" : astring[0]) && isValidPath(astring[1]);
   }

   private static String assertValidPath(String p_251418_, String p_248828_) {
      if (!isValidPath(p_248828_)) {
         throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + p_251418_ + ":" + p_248828_);
      } else {
         return p_248828_;
      }
   }

   protected interface Dummy {
   }

   public static class Serializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
      public ResourceLocation deserialize(JsonElement pJson, Type pTypeOfT, JsonDeserializationContext pContext) throws JsonParseException {
         return new ResourceLocation(GsonHelper.convertToString(pJson, "location"));
      }

      public JsonElement serialize(ResourceLocation pResourceLocation, Type pTypeOfT, JsonSerializationContext pContext) {
         return new JsonPrimitive(pResourceLocation.toString());
      }
   }
}
