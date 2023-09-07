package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeUnlockedTrigger extends SimpleCriterionTrigger<RecipeUnlockedTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");

   public ResourceLocation getId() {
      return ID;
   }

   public RecipeUnlockedTrigger.TriggerInstance createInstance(JsonObject p_286387_, ContextAwarePredicate p_286739_, DeserializationContext p_286649_) {
      ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_286387_, "recipe"));
      return new RecipeUnlockedTrigger.TriggerInstance(p_286739_, resourcelocation);
   }

   public void trigger(ServerPlayer pPlayer, Recipe<?> pRecipe) {
      this.trigger(pPlayer, (p_63723_) -> {
         return p_63723_.matches(pRecipe);
      });
   }

   public static RecipeUnlockedTrigger.TriggerInstance unlocked(ResourceLocation pRecipe) {
      return new RecipeUnlockedTrigger.TriggerInstance(ContextAwarePredicate.ANY, pRecipe);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ResourceLocation recipe;

      public TriggerInstance(ContextAwarePredicate p_286461_, ResourceLocation p_286775_) {
         super(RecipeUnlockedTrigger.ID, p_286461_);
         this.recipe = p_286775_;
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.addProperty("recipe", this.recipe.toString());
         return jsonobject;
      }

      public boolean matches(Recipe<?> pRecipe) {
         return this.recipe.equals(pRecipe.getId());
      }
   }
}