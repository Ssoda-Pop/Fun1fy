package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class LevitationTrigger extends SimpleCriterionTrigger<LevitationTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("levitation");

   public ResourceLocation getId() {
      return ID;
   }

   public LevitationTrigger.TriggerInstance createInstance(JsonObject p_286359_, ContextAwarePredicate p_286588_, DeserializationContext p_286241_) {
      DistancePredicate distancepredicate = DistancePredicate.fromJson(p_286359_.get("distance"));
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_286359_.get("duration"));
      return new LevitationTrigger.TriggerInstance(p_286588_, distancepredicate, minmaxbounds$ints);
   }

   public void trigger(ServerPlayer pPlayer, Vec3 pStartPos, int pDuration) {
      this.trigger(pPlayer, (p_49124_) -> {
         return p_49124_.matches(pPlayer, pStartPos, pDuration);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DistancePredicate distance;
      private final MinMaxBounds.Ints duration;

      public TriggerInstance(ContextAwarePredicate p_286511_, DistancePredicate p_286806_, MinMaxBounds.Ints p_286676_) {
         super(LevitationTrigger.ID, p_286511_);
         this.distance = p_286806_;
         this.duration = p_286676_;
      }

      public static LevitationTrigger.TriggerInstance levitated(DistancePredicate pDistance) {
         return new LevitationTrigger.TriggerInstance(ContextAwarePredicate.ANY, pDistance, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ServerPlayer pPlayer, Vec3 pStartPos, int pDuration) {
         if (!this.distance.matches(pStartPos.x, pStartPos.y, pStartPos.z, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ())) {
            return false;
         } else {
            return this.duration.matches(pDuration);
         }
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("distance", this.distance.serializeToJson());
         jsonobject.add("duration", this.duration.serializeToJson());
         return jsonobject;
      }
   }
}