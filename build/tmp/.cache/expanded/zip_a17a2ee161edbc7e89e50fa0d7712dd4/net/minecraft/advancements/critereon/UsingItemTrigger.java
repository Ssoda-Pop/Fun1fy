package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class UsingItemTrigger extends SimpleCriterionTrigger<UsingItemTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("using_item");

   public ResourceLocation getId() {
      return ID;
   }

   public UsingItemTrigger.TriggerInstance createInstance(JsonObject p_286642_, ContextAwarePredicate p_286670_, DeserializationContext p_286897_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_286642_.get("item"));
      return new UsingItemTrigger.TriggerInstance(p_286670_, itempredicate);
   }

   public void trigger(ServerPlayer pPlayer, ItemStack pItem) {
      this.trigger(pPlayer, (p_163870_) -> {
         return p_163870_.matches(pItem);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ContextAwarePredicate p_286652_, ItemPredicate p_286296_) {
         super(UsingItemTrigger.ID, p_286652_);
         this.item = p_286296_;
      }

      public static UsingItemTrigger.TriggerInstance lookingAt(EntityPredicate.Builder pEntityPredicateBuilder, ItemPredicate.Builder pItemPredicateBuilder) {
         return new UsingItemTrigger.TriggerInstance(EntityPredicate.wrap(pEntityPredicateBuilder.build()), pItemPredicateBuilder.build());
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