package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class SummonedEntityTrigger extends SimpleCriterionTrigger<SummonedEntityTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("summoned_entity");

   public ResourceLocation getId() {
      return ID;
   }

   public SummonedEntityTrigger.TriggerInstance createInstance(JsonObject p_286669_, ContextAwarePredicate p_286745_, DeserializationContext p_286637_) {
      ContextAwarePredicate contextawarepredicate = EntityPredicate.fromJson(p_286669_, "entity", p_286637_);
      return new SummonedEntityTrigger.TriggerInstance(p_286745_, contextawarepredicate);
   }

   public void trigger(ServerPlayer pPlayer, Entity pEntity) {
      LootContext lootcontext = EntityPredicate.createContext(pPlayer, pEntity);
      this.trigger(pPlayer, (p_68265_) -> {
         return p_68265_.matches(lootcontext);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate entity;

      public TriggerInstance(ContextAwarePredicate p_286853_, ContextAwarePredicate p_286838_) {
         super(SummonedEntityTrigger.ID, p_286853_);
         this.entity = p_286838_;
      }

      public static SummonedEntityTrigger.TriggerInstance summonedEntity(EntityPredicate.Builder pEntityPredicateBuilder) {
         return new SummonedEntityTrigger.TriggerInstance(ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicateBuilder.build()));
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