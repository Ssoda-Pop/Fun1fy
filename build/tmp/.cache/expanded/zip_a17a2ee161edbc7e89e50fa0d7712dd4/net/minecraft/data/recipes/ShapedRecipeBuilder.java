package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class ShapedRecipeBuilder extends CraftingRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final Item result;
   private final int count;
   private final List<String> rows = Lists.newArrayList();
   private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
   private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
   @Nullable
   private String group;
   private boolean showNotification = true;

   public ShapedRecipeBuilder(RecipeCategory p_249996_, ItemLike p_251475_, int p_248948_) {
      this.category = p_249996_;
      this.result = p_251475_.asItem();
      this.count = p_248948_;
   }

   public static ShapedRecipeBuilder shaped(RecipeCategory p_250853_, ItemLike p_249747_) {
      return shaped(p_250853_, p_249747_, 1);
   }

   public static ShapedRecipeBuilder shaped(RecipeCategory p_251325_, ItemLike p_250636_, int p_249081_) {
      return new ShapedRecipeBuilder(p_251325_, p_250636_, p_249081_);
   }

   /**
    * Adds a key to the recipe pattern.
    */
   public ShapedRecipeBuilder define(Character pSymbol, TagKey<Item> pTag) {
      return this.define(pSymbol, Ingredient.of(pTag));
   }

   /**
    * Adds a key to the recipe pattern.
    */
   public ShapedRecipeBuilder define(Character pSymbol, ItemLike pItem) {
      return this.define(pSymbol, Ingredient.of(pItem));
   }

   /**
    * Adds a key to the recipe pattern.
    */
   public ShapedRecipeBuilder define(Character pSymbol, Ingredient pIngredient) {
      if (this.key.containsKey(pSymbol)) {
         throw new IllegalArgumentException("Symbol '" + pSymbol + "' is already defined!");
      } else if (pSymbol == ' ') {
         throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
      } else {
         this.key.put(pSymbol, pIngredient);
         return this;
      }
   }

   /**
    * Adds a new entry to the patterns for this recipe.
    */
   public ShapedRecipeBuilder pattern(String pPattern) {
      if (!this.rows.isEmpty() && pPattern.length() != this.rows.get(0).length()) {
         throw new IllegalArgumentException("Pattern must be the same width on every line!");
      } else {
         this.rows.add(pPattern);
         return this;
      }
   }

   public ShapedRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
      this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
      return this;
   }

   public ShapedRecipeBuilder group(@Nullable String pGroupName) {
      this.group = pGroupName;
      return this;
   }

   public ShapedRecipeBuilder showNotification(boolean p_273326_) {
      this.showNotification = p_273326_;
      return this;
   }

   public Item getResult() {
      return this.result;
   }

   public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
      this.ensureValid(pRecipeId);
      this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
      pFinishedRecipeConsumer.accept(new ShapedRecipeBuilder.Result(pRecipeId, this.result, this.count, this.group == null ? "" : this.group, determineBookCategory(this.category), this.rows, this.key, this.advancement, pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"), this.showNotification));
   }

   /**
    * Makes sure that this recipe is valid and obtainable.
    */
   private void ensureValid(ResourceLocation pId) {
      if (this.rows.isEmpty()) {
         throw new IllegalStateException("No pattern is defined for shaped recipe " + pId + "!");
      } else {
         Set<Character> set = Sets.newHashSet(this.key.keySet());
         set.remove(' ');

         for(String s : this.rows) {
            for(int i = 0; i < s.length(); ++i) {
               char c0 = s.charAt(i);
               if (!this.key.containsKey(c0) && c0 != ' ') {
                  throw new IllegalStateException("Pattern in recipe " + pId + " uses undefined symbol '" + c0 + "'");
               }

               set.remove(c0);
            }
         }

         if (!set.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + pId);
         } else if (this.rows.size() == 1 && this.rows.get(0).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + pId + " only takes in a single item - should it be a shapeless recipe instead?");
         } else if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pId);
         }
      }
   }

   public static class Result extends CraftingRecipeBuilder.CraftingResult {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<String> pattern;
      private final Map<Character, Ingredient> key;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;
      private final boolean showNotification;

      public Result(ResourceLocation p_273548_, Item p_273530_, int p_272738_, String p_273549_, CraftingBookCategory p_273500_, List<String> p_273744_, Map<Character, Ingredient> p_272991_, Advancement.Builder p_273260_, ResourceLocation p_273106_, boolean p_272862_) {
         super(p_273500_);
         this.id = p_273548_;
         this.result = p_273530_;
         this.count = p_272738_;
         this.group = p_273549_;
         this.pattern = p_273744_;
         this.key = p_272991_;
         this.advancement = p_273260_;
         this.advancementId = p_273106_;
         this.showNotification = p_272862_;
      }

      public void serializeRecipeData(JsonObject pJson) {
         super.serializeRecipeData(pJson);
         if (!this.group.isEmpty()) {
            pJson.addProperty("group", this.group);
         }

         JsonArray jsonarray = new JsonArray();

         for(String s : this.pattern) {
            jsonarray.add(s);
         }

         pJson.add("pattern", jsonarray);
         JsonObject jsonobject = new JsonObject();

         for(Map.Entry<Character, Ingredient> entry : this.key.entrySet()) {
            jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
         }

         pJson.add("key", jsonobject);
         JsonObject jsonobject1 = new JsonObject();
         jsonobject1.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
         if (this.count > 1) {
            jsonobject1.addProperty("count", this.count);
         }

         pJson.add("result", jsonobject1);
         pJson.addProperty("show_notification", this.showNotification);
      }

      public RecipeSerializer<?> getType() {
         return RecipeSerializer.SHAPED_RECIPE;
      }

      /**
       * Gets the ID for the recipe.
       */
      public ResourceLocation getId() {
         return this.id;
      }

      /**
       * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
       */
      @Nullable
      public JsonObject serializeAdvancement() {
         return this.advancement.serializeToJson();
      }

      /**
       * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson}
       * is non-null.
       */
      @Nullable
      public ResourceLocation getAdvancementId() {
         return this.advancementId;
      }
   }
}