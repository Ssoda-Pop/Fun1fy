package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class KilledTrigger extends SimpleCriterionTrigger<KilledTrigger.TriggerInstance> {
   final ResourceLocation id;

   public KilledTrigger(ResourceLocation pId) {
      this.id = pId;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public KilledTrigger.TriggerInstance createInstance(JsonObject p_286718_, ContextAwarePredicate p_286909_, DeserializationContext p_286514_) {
      return new KilledTrigger.TriggerInstance(this.id, p_286909_, EntityPredicate.fromJson(p_286718_, "entity", p_286514_), DamageSourcePredicate.fromJson(p_286718_.get("killing_blow")));
   }

   public void trigger(ServerPlayer pPlayer, Entity pEntity, DamageSource pSource) {
      LootContext lootcontext = EntityPredicate.createContext(pPlayer, pEntity);
      this.trigger(pPlayer, (p_48112_) -> {
         return p_48112_.matches(pPlayer, lootcontext, pSource);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate entityPredicate;
      private final DamageSourcePredicate killingBlow;

      public TriggerInstance(ResourceLocation p_286471_, ContextAwarePredicate p_286673_, ContextAwarePredicate p_286390_, DamageSourcePredicate p_286643_) {
         super(p_286471_, p_286673_);
         this.entityPredicate = p_286390_;
         this.killingBlow = p_286643_;
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate pEntityPredicate) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicate), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder pEntityPredicateBuilder) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicateBuilder.build()), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity() {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate pEntityPredicate, DamageSourcePredicate pKillingBlow) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicate), pKillingBlow);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder pEntityPredicateBuilder, DamageSourcePredicate pKillingBlow) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicateBuilder.build()), pKillingBlow);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate pEntityPredicate, DamageSourcePredicate.Builder pKillingBlowBuilder) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicate), pKillingBlowBuilder.build());
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder pEntityPredicateBuilder, DamageSourcePredicate.Builder pKillingBlowBuilder) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicateBuilder.build()), pKillingBlowBuilder.build());
      }

      public static KilledTrigger.TriggerInstance playerKilledEntityNearSculkCatalyst() {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.id, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate pEntityPredicate) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicate), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate.Builder pEntityPredicateBuilder) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicateBuilder.build()), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer() {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate pEntityPredicate, DamageSourcePredicate pKillingBlow) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicate), pKillingBlow);
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate.Builder pEntityPredicateBuilder, DamageSourcePredicate pKillingBlow) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicateBuilder.build()), pKillingBlow);
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate pEntityPredicate, DamageSourcePredicate.Builder pKillingBlowBuilder) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicate), pKillingBlowBuilder.build());
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate.Builder pEntityPredicateBuilder, DamageSourcePredicate.Builder pKillingBlowBuilder) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicateBuilder.build()), pKillingBlowBuilder.build());
      }

      public boolean matches(ServerPlayer pPlayer, LootContext pContext, DamageSource pSource) {
         return !this.killingBlow.matches(pPlayer, pSource) ? false : this.entityPredicate.matches(pContext);
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("entity", this.entityPredicate.toJson(pConditions));
         jsonobject.add("killing_blow", this.killingBlow.serializeToJson());
         return jsonobject;
      }
   }
}