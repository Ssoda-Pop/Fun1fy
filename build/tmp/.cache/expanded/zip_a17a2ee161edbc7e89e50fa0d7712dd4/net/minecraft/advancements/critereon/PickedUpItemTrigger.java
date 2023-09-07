package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PickedUpItemTrigger extends SimpleCriterionTrigger<PickedUpItemTrigger.TriggerInstance> {
   private final ResourceLocation id;

   public PickedUpItemTrigger(ResourceLocation pId) {
      this.id = pId;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   protected PickedUpItemTrigger.TriggerInstance createInstance(JsonObject p_286475_, ContextAwarePredicate p_286683_, DeserializationContext p_286255_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_286475_.get("item"));
      ContextAwarePredicate contextawarepredicate = EntityPredicate.fromJson(p_286475_, "entity", p_286255_);
      return new PickedUpItemTrigger.TriggerInstance(this.id, p_286683_, itempredicate, contextawarepredicate);
   }

   public void trigger(ServerPlayer pPlayer, ItemStack pStack, @Nullable Entity pEntity) {
      LootContext lootcontext = EntityPredicate.createContext(pPlayer, pEntity);
      this.trigger(pPlayer, (p_221306_) -> {
         return p_221306_.matches(pPlayer, pStack, lootcontext);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final ContextAwarePredicate entity;

      public TriggerInstance(ResourceLocation p_286249_, ContextAwarePredicate p_286258_, ItemPredicate p_286761_, ContextAwarePredicate p_286491_) {
         super(p_286249_, p_286258_);
         this.item = p_286761_;
         this.entity = p_286491_;
      }

      public static PickedUpItemTrigger.TriggerInstance thrownItemPickedUpByEntity(ContextAwarePredicate p_286865_, ItemPredicate p_286788_, ContextAwarePredicate p_286327_) {
         return new PickedUpItemTrigger.TriggerInstance(CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_ENTITY.getId(), p_286865_, p_286788_, p_286327_);
      }

      public static PickedUpItemTrigger.TriggerInstance thrownItemPickedUpByPlayer(ContextAwarePredicate p_286405_, ItemPredicate p_286518_, ContextAwarePredicate p_286381_) {
         return new PickedUpItemTrigger.TriggerInstance(CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_PLAYER.getId(), p_286405_, p_286518_, p_286381_);
      }

      public boolean matches(ServerPlayer pPlayer, ItemStack pStack, LootContext pContext) {
         if (!this.item.matches(pStack)) {
            return false;
         } else {
            return this.entity.matches(pContext);
         }
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("entity", this.entity.toJson(pConditions));
         return jsonobject;
      }
   }
}