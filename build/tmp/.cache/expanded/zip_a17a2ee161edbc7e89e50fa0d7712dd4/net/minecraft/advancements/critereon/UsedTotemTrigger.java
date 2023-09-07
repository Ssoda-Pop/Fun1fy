package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class UsedTotemTrigger extends SimpleCriterionTrigger<UsedTotemTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("used_totem");

   public ResourceLocation getId() {
      return ID;
   }

   public UsedTotemTrigger.TriggerInstance createInstance(JsonObject p_286841_, ContextAwarePredicate p_286597_, DeserializationContext p_286414_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_286841_.get("item"));
      return new UsedTotemTrigger.TriggerInstance(p_286597_, itempredicate);
   }

   public void trigger(ServerPlayer pPlayer, ItemStack pItem) {
      this.trigger(pPlayer, (p_74436_) -> {
         return p_74436_.matches(pItem);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ContextAwarePredicate p_286406_, ItemPredicate p_286462_) {
         super(UsedTotemTrigger.ID, p_286406_);
         this.item = p_286462_;
      }

      public static UsedTotemTrigger.TriggerInstance usedTotem(ItemPredicate pItem) {
         return new UsedTotemTrigger.TriggerInstance(ContextAwarePredicate.ANY, pItem);
      }

      public static UsedTotemTrigger.TriggerInstance usedTotem(ItemLike pItem) {
         return new UsedTotemTrigger.TriggerInstance(ContextAwarePredicate.ANY, ItemPredicate.Builder.item().of(pItem).build());
      }

      public boolean matches(ItemStack pItem) {
         return this.item.matches(pItem);
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}