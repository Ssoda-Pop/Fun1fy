package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.storage.loot.LootContext;

public class LightningStrikeTrigger extends SimpleCriterionTrigger<LightningStrikeTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("lightning_strike");

   public ResourceLocation getId() {
      return ID;
   }

   public LightningStrikeTrigger.TriggerInstance createInstance(JsonObject p_286889_, ContextAwarePredicate p_286650_, DeserializationContext p_286384_) {
      ContextAwarePredicate contextawarepredicate = EntityPredicate.fromJson(p_286889_, "lightning", p_286384_);
      ContextAwarePredicate contextawarepredicate1 = EntityPredicate.fromJson(p_286889_, "bystander", p_286384_);
      return new LightningStrikeTrigger.TriggerInstance(p_286650_, contextawarepredicate, contextawarepredicate1);
   }

   public void trigger(ServerPlayer pPlayer, LightningBolt pLightning, List<Entity> pNearbyEntities) {
      List<LootContext> list = pNearbyEntities.stream().map((p_153390_) -> {
         return EntityPredicate.createContext(pPlayer, p_153390_);
      }).collect(Collectors.toList());
      LootContext lootcontext = EntityPredicate.createContext(pPlayer, pLightning);
      this.trigger(pPlayer, (p_153402_) -> {
         return p_153402_.matches(lootcontext, list);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate lightning;
      private final ContextAwarePredicate bystander;

      public TriggerInstance(ContextAwarePredicate p_286747_, ContextAwarePredicate p_286287_, ContextAwarePredicate p_286566_) {
         super(LightningStrikeTrigger.ID, p_286747_);
         this.lightning = p_286287_;
         this.bystander = p_286566_;
      }

      public static LightningStrikeTrigger.TriggerInstance lighthingStrike(EntityPredicate pLightning, EntityPredicate pBystander) {
         return new LightningStrikeTrigger.TriggerInstance(ContextAwarePredicate.ANY, EntityPredicate.wrap(pLightning), EntityPredicate.wrap(pBystander));
      }

      public boolean matches(LootContext pPlayerContext, List<LootContext> pEntityContexts) {
         if (!this.lightning.matches(pPlayerContext)) {
            return false;
         } else {
            return this.bystander == ContextAwarePredicate.ANY || !pEntityContexts.stream().noneMatch(this.bystander::matches);
         }
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("lightning", this.lightning.toJson(pConditions));
         jsonobject.add("bystander", this.bystander.toJson(pConditions));
         return jsonobject;
      }
   }
}