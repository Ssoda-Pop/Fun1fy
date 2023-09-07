package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ConstructBeaconTrigger extends SimpleCriterionTrigger<ConstructBeaconTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("construct_beacon");

   public ResourceLocation getId() {
      return ID;
   }

   public ConstructBeaconTrigger.TriggerInstance createInstance(JsonObject p_286465_, ContextAwarePredicate p_286914_, DeserializationContext p_286803_) {
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_286465_.get("level"));
      return new ConstructBeaconTrigger.TriggerInstance(p_286914_, minmaxbounds$ints);
   }

   public void trigger(ServerPlayer pPlayer, int pLevel) {
      this.trigger(pPlayer, (p_148028_) -> {
         return p_148028_.matches(pLevel);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints level;

      public TriggerInstance(ContextAwarePredicate p_286868_, MinMaxBounds.Ints p_286272_) {
         super(ConstructBeaconTrigger.ID, p_286868_);
         this.level = p_286272_;
      }

      public static ConstructBeaconTrigger.TriggerInstance constructedBeacon() {
         return new ConstructBeaconTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY);
      }

      public static ConstructBeaconTrigger.TriggerInstance constructedBeacon(MinMaxBounds.Ints pLevel) {
         return new ConstructBeaconTrigger.TriggerInstance(ContextAwarePredicate.ANY, pLevel);
      }

      public boolean matches(int pLevel) {
         return this.level.matches(pLevel);
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("level", this.level.serializeToJson());
         return jsonobject;
      }
   }
}