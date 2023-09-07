package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class FilledBucketTrigger extends SimpleCriterionTrigger<FilledBucketTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("filled_bucket");

   public ResourceLocation getId() {
      return ID;
   }

   public FilledBucketTrigger.TriggerInstance createInstance(JsonObject p_286783_, ContextAwarePredicate p_286776_, DeserializationContext p_286812_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_286783_.get("item"));
      return new FilledBucketTrigger.TriggerInstance(p_286776_, itempredicate);
   }

   public void trigger(ServerPlayer pPlayer, ItemStack pStack) {
      this.trigger(pPlayer, (p_38777_) -> {
         return p_38777_.matches(pStack);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ContextAwarePredicate p_286231_, ItemPredicate p_286845_) {
         super(FilledBucketTrigger.ID, p_286231_);
         this.item = p_286845_;
      }

      public static FilledBucketTrigger.TriggerInstance filledBucket(ItemPredicate pItem) {
         return new FilledBucketTrigger.TriggerInstance(ContextAwarePredicate.ANY, pItem);
      }

      public boolean matches(ItemStack pStack) {
         return this.item.matches(pStack);
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}