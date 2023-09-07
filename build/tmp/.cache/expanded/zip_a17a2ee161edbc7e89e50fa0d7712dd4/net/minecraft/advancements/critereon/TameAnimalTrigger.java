package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;

public class TameAnimalTrigger extends SimpleCriterionTrigger<TameAnimalTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("tame_animal");

   public ResourceLocation getId() {
      return ID;
   }

   public TameAnimalTrigger.TriggerInstance createInstance(JsonObject p_286910_, ContextAwarePredicate p_286765_, DeserializationContext p_286732_) {
      ContextAwarePredicate contextawarepredicate = EntityPredicate.fromJson(p_286910_, "entity", p_286732_);
      return new TameAnimalTrigger.TriggerInstance(p_286765_, contextawarepredicate);
   }

   public void trigger(ServerPlayer pPlayer, Animal pEntity) {
      LootContext lootcontext = EntityPredicate.createContext(pPlayer, pEntity);
      this.trigger(pPlayer, (p_68838_) -> {
         return p_68838_.matches(lootcontext);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate entity;

      public TriggerInstance(ContextAwarePredicate p_286593_, ContextAwarePredicate p_286484_) {
         super(TameAnimalTrigger.ID, p_286593_);
         this.entity = p_286484_;
      }

      public static TameAnimalTrigger.TriggerInstance tamedAnimal() {
         return new TameAnimalTrigger.TriggerInstance(ContextAwarePredicate.ANY, ContextAwarePredicate.ANY);
      }

      public static TameAnimalTrigger.TriggerInstance tamedAnimal(EntityPredicate pEntityPredicate) {
         return new TameAnimalTrigger.TriggerInstance(ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicate));
      }

      public boolean matches(LootContext pLootContext) {
         return this.entity.matches(pLootContext);
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("entity", this.entity.toJson(pConditions));
         return jsonobject;
      }
   }
}