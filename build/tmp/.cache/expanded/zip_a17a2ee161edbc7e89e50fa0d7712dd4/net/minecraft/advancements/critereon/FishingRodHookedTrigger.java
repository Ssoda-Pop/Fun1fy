package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class FishingRodHookedTrigger extends SimpleCriterionTrigger<FishingRodHookedTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("fishing_rod_hooked");

   public ResourceLocation getId() {
      return ID;
   }

   public FishingRodHookedTrigger.TriggerInstance createInstance(JsonObject p_286350_, ContextAwarePredicate p_286888_, DeserializationContext p_286756_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_286350_.get("rod"));
      ContextAwarePredicate contextawarepredicate = EntityPredicate.fromJson(p_286350_, "entity", p_286756_);
      ItemPredicate itempredicate1 = ItemPredicate.fromJson(p_286350_.get("item"));
      return new FishingRodHookedTrigger.TriggerInstance(p_286888_, itempredicate, contextawarepredicate, itempredicate1);
   }

   public void trigger(ServerPlayer pPlayer, ItemStack pRod, FishingHook pEntity, Collection<ItemStack> pStacks) {
      LootContext lootcontext = EntityPredicate.createContext(pPlayer, (Entity)(pEntity.getHookedIn() != null ? pEntity.getHookedIn() : pEntity));
      this.trigger(pPlayer, (p_40425_) -> {
         return p_40425_.matches(pRod, lootcontext, pStacks);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate rod;
      private final ContextAwarePredicate entity;
      private final ItemPredicate item;

      public TriggerInstance(ContextAwarePredicate p_286346_, ItemPredicate p_286539_, ContextAwarePredicate p_286253_, ItemPredicate p_286372_) {
         super(FishingRodHookedTrigger.ID, p_286346_);
         this.rod = p_286539_;
         this.entity = p_286253_;
         this.item = p_286372_;
      }

      public static FishingRodHookedTrigger.TriggerInstance fishedItem(ItemPredicate pRod, EntityPredicate pBobber, ItemPredicate pItem) {
         return new FishingRodHookedTrigger.TriggerInstance(ContextAwarePredicate.ANY, pRod, EntityPredicate.wrap(pBobber), pItem);
      }

      public boolean matches(ItemStack pRod, LootContext pContext, Collection<ItemStack> pStacks) {
         if (!this.rod.matches(pRod)) {
            return false;
         } else if (!this.entity.matches(pContext)) {
            return false;
         } else {
            if (this.item != ItemPredicate.ANY) {
               boolean flag = false;
               Entity entity = pContext.getParamOrNull(LootContextParams.THIS_ENTITY);
               if (entity instanceof ItemEntity) {
                  ItemEntity itementity = (ItemEntity)entity;
                  if (this.item.matches(itementity.getItem())) {
                     flag = true;
                  }
               }

               for(ItemStack itemstack : pStacks) {
                  if (this.item.matches(itemstack)) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }

            return true;
         }
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("rod", this.rod.serializeToJson());
         jsonobject.add("entity", this.entity.toJson(pConditions));
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}