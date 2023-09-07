package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerInteractTrigger extends SimpleCriterionTrigger<PlayerInteractTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("player_interacted_with_entity");

   public ResourceLocation getId() {
      return ID;
   }

   protected PlayerInteractTrigger.TriggerInstance createInstance(JsonObject p_286617_, ContextAwarePredicate p_286504_, DeserializationContext p_286558_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_286617_.get("item"));
      ContextAwarePredicate contextawarepredicate = EntityPredicate.fromJson(p_286617_, "entity", p_286558_);
      return new PlayerInteractTrigger.TriggerInstance(p_286504_, itempredicate, contextawarepredicate);
   }

   public void trigger(ServerPlayer pPlayer, ItemStack pItem, Entity pEntity) {
      LootContext lootcontext = EntityPredicate.createContext(pPlayer, pEntity);
      this.trigger(pPlayer, (p_61501_) -> {
         return p_61501_.matches(pItem, lootcontext);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final ContextAwarePredicate entity;

      public TriggerInstance(ContextAwarePredicate p_286824_, ItemPredicate p_286719_, ContextAwarePredicate p_286219_) {
         super(PlayerInteractTrigger.ID, p_286824_);
         this.item = p_286719_;
         this.entity = p_286219_;
      }

      public static PlayerInteractTrigger.TriggerInstance itemUsedOnEntity(ContextAwarePredicate p_286452_, ItemPredicate.Builder p_286289_, ContextAwarePredicate p_286370_) {
         return new PlayerInteractTrigger.TriggerInstance(p_286452_, p_286289_.build(), p_286370_);
      }

      public static PlayerInteractTrigger.TriggerInstance itemUsedOnEntity(ItemPredicate.Builder p_286235_, ContextAwarePredicate p_286667_) {
         return itemUsedOnEntity(ContextAwarePredicate.ANY, p_286235_, p_286667_);
      }

      public boolean matches(ItemStack pItem, LootContext pLootContext) {
         return !this.item.matches(pItem) ? false : this.entity.matches(pLootContext);
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("entity", this.entity.toJson(pConditions));
         return jsonobject;
      }
   }
}