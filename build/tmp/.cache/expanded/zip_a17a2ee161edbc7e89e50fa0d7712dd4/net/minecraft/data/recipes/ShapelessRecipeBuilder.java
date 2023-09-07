package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
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

public class ShapelessRecipeBuilder extends CraftingRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final Item result;
   private final int count;
   private final List<Ingredient> ingredients = Lists.newArrayList();
   private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
   @Nullable
   private String group;

   public ShapelessRecipeBuilder(RecipeCategory p_250837_, ItemLike p_251897_, int p_252227_) {
      this.category = p_250837_;
      this.result = p_251897_.asItem();
      this.count = p_252227_;
   }

   public static ShapelessRecipeBuilder shapeless(RecipeCategory p_250714_, ItemLike p_249659_) {
      return new ShapelessRecipeBuilder(p_250714_, p_249659_, 1);
   }

   public static ShapelessRecipeBuilder shapeless(RecipeCategory p_252339_, ItemLike p_250836_, int p_249928_) {
      return new ShapelessRecipeBuilder(p_252339_, p_250836_, p_249928_);
   }

   /**
    * Adds an ingredient that can be any item in the given tag.
    */
   public ShapelessRecipeBuilder requires(TagKey<Item> pTag) {
      return this.requires(Ingredient.of(pTag));
   }

   /**
    * Adds an ingredient of the given item.
    */
   public ShapelessRecipeBuilder requires(ItemLike pItem) {
      return this.requires(pItem, 1);
   }

   /**
    * Adds the given ingredient multiple times.
    */
   public ShapelessRecipeBuilder requires(ItemLike pItem, int pQuantity) {
      for(int i = 0; i < pQuantity; ++i) {
         this.requires(Ingredient.of(pItem));
      }

      return this;
   }

   /**
    * Adds an ingredient.
    */
   public ShapelessRecipeBuilder requires(Ingredient pIngredient) {
      return this.requires(pIngredient, 1);
   }

   /**
    * Adds an ingredient multiple times.
    */
   public ShapelessRecipeBuilder requires(Ingredient pIngredient, int pQuantity) {
      for(int i = 0; i < pQuantity; ++i) {
         this.ingredients.add(pIngredient);
      }

      return this;
   }

   public ShapelessRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
      this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
      return this;
   }

   public ShapelessRecipeBuilder group(@Nullable String pGroupName) {
      this.group = pGroupName;
      return this;
   }

   public Item getResult() {
      return this.result;
   }

   public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
      this.ensureValid(pRecipeId);
      this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
      pFinishedRecipeConsumer.accept(new ShapelessRecipeBuilder.Result(pRecipeId, this.result, this.count, this.group == null ? "" : this.group, determineBookCategory(this.category), this.ingredients, this.advancement, pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }

   /**
    * Makes sure that this recipe is valid and obtainable.
    */
   private void ensureValid(ResourceLocation pId) {
      if (this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + pId);
      }
   }

   public static class Result extends CraftingRecipeBuilder.CraftingResult {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<Ingredient> ingredients;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation p_249007_, Item p_248667_, int p_249014_, String p_248592_, CraftingBookCategory p_249485_, List<Ingredient> p_252312_, Advancement.Builder p_249909_, ResourceLocation p_249109_) {
         super(p_249485_);
         this.id = p_249007_;
         this.result = p_248667_;
         this.count = p_249014_;
         this.group = p_248592_;
         this.ingredients = p_252312_;
         this.advancement = p_249909_;
         this.advancementId = p_249109_;
      }

      public void serializeRecipeData(JsonObject pJson) {
         super.serializeRecipeData(pJson);
         if (!this.group.isEmpty()) {
            pJson.addProperty("group", this.group);
         }

         JsonArray jsonarray = new JsonArray();

         for(Ingredient ingredient : this.ingredients) {
            jsonarray.add(ingredient.toJson());
         }

         pJson.add("ingredients", jsonarray);
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
         if (this.count > 1) {
            jsonobject.addProperty("count", this.count);
         }

         pJson.add("result", jsonobject);
      }

      public RecipeSerializer<?> getType() {
         return RecipeSerializer.SHAPELESS_RECIPE;
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