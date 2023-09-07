package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class StartRidingTrigger extends SimpleCriterionTrigger<StartRidingTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("started_riding");

   public ResourceLocation getId() {
      return ID;
   }

   public StartRidingTrigger.TriggerInstance createInstance(JsonObject p_286276_, ContextAwarePredicate p_286282_, DeserializationContext p_286851_) {
      return new StartRidingTrigger.TriggerInstance(p_286282_);
   }

   public void trigger(ServerPlayer pPlayer) {
      this.trigger(pPlayer, (p_160394_) -> {
         return true;
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      public TriggerInstance(ContextAwarePredicate p_286351_) {
         super(StartRidingTrigger.ID, p_286351_);
      }

      public static StartRidingTrigger.TriggerInstance playerStartsRiding(EntityPredicate.Builder pPlayer) {
         return new StartRidingTrigger.TriggerInstance(EntityPredicate.wrap(pPlayer.build()));
      }
   }
}