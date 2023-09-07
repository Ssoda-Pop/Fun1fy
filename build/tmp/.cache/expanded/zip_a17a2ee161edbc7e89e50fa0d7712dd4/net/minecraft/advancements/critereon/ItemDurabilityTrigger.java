package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemDurabilityTrigger extends SimpleCriterionTrigger<ItemDurabilityTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("item_durability_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public ItemDurabilityTrigger.TriggerInstance createInstance(JsonObject p_286693_, ContextAwarePredicate p_286383_, DeserializationContext p_286352_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_286693_.get("item"));
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_286693_.get("durability"));
      MinMaxBounds.Ints minmaxbounds$ints1 = MinMaxBounds.Ints.fromJson(p_286693_.get("delta"));
      return new ItemDurabilityTrigger.TriggerInstance(p_286383_, itempredicate, minmaxbounds$ints, minmaxbounds$ints1);
   }

   public void trigger(ServerPlayer pPlayer, ItemStack pItem, int pNewDurability) {
      this.trigger(pPlayer, (p_43676_) -> {
         return p_43676_.matches(pItem, pNewDurability);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.Ints durability;
      private final MinMaxBounds.Ints delta;

      public TriggerInstance(ContextAwarePredicate p_286731_, ItemPredicate p_286447_, MinMaxBounds.Ints p_286431_, MinMaxBounds.Ints p_286460_) {
         super(ItemDurabilityTrigger.ID, p_286731_);
         this.item = p_286447_;
         this.durability = p_286431_;
         this.delta = p_286460_;
      }

      public static ItemDurabilityTrigger.TriggerInstance changedDurability(ItemPredicate pItem, MinMaxBounds.Ints pDurability) {
         return changedDurability(ContextAwarePredicate.ANY, pItem, pDurability);
      }

      public static ItemDurabilityTrigger.TriggerInstance changedDurability(ContextAwarePredicate p_286720_, ItemPredicate p_286288_, MinMaxBounds.Ints p_286730_) {
         return new ItemDurabilityTrigger.TriggerInstance(p_286720_, p_286288_, p_286730_, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ItemStack pItem, int pDurability) {
         if (!this.item.matches(pItem)) {
            return false;
         } else if (!this.durability.matches(pItem.getMaxDamage() - pDurability)) {
            return false;
         } else {
            return this.delta.matches(pItem.getDamageValue() - pDurability);
         }
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("durability", this.durability.serializeToJson());
         jsonobject.add("delta", this.delta.serializeToJson());
         return jsonobject;
      }
   }
}