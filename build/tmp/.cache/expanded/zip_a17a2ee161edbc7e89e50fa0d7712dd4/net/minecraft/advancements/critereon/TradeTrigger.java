package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class TradeTrigger extends SimpleCriterionTrigger<TradeTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("villager_trade");

   public ResourceLocation getId() {
      return ID;
   }

   public TradeTrigger.TriggerInstance createInstance(JsonObject p_286654_, ContextAwarePredicate p_286835_, DeserializationContext p_286772_) {
      ContextAwarePredicate contextawarepredicate = EntityPredicate.fromJson(p_286654_, "villager", p_286772_);
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_286654_.get("item"));
      return new TradeTrigger.TriggerInstance(p_286835_, contextawarepredicate, itempredicate);
   }

   public void trigger(ServerPlayer pPlayer, AbstractVillager pVillager, ItemStack pStack) {
      LootContext lootcontext = EntityPredicate.createContext(pPlayer, pVillager);
      this.trigger(pPlayer, (p_70970_) -> {
         return p_70970_.matches(lootcontext, pStack);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate villager;
      private final ItemPredicate item;

      public TriggerInstance(ContextAwarePredicate p_286523_, ContextAwarePredicate p_286395_, ItemPredicate p_286263_) {
         super(TradeTrigger.ID, p_286523_);
         this.villager = p_286395_;
         this.item = p_286263_;
      }

      public static TradeTrigger.TriggerInstance tradedWithVillager() {
         return new TradeTrigger.TriggerInstance(ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ItemPredicate.ANY);
      }

      public static TradeTrigger.TriggerInstance tradedWithVillager(EntityPredicate.Builder pVillager) {
         return new TradeTrigger.TriggerInstance(EntityPredicate.wrap(pVillager.build()), ContextAwarePredicate.ANY, ItemPredicate.ANY);
      }

      public boolean matches(LootContext pContext, ItemStack pStack) {
         if (!this.villager.matches(pContext)) {
            return false;
         } else {
            return this.item.matches(pStack);
         }
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("villager", this.villager.toJson(pConditions));
         return jsonobject;
      }
   }
}