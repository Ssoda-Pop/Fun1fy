package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;

public class BredAnimalsTrigger extends SimpleCriterionTrigger<BredAnimalsTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("bred_animals");

   public ResourceLocation getId() {
      return ID;
   }

   public BredAnimalsTrigger.TriggerInstance createInstance(JsonObject p_286457_, ContextAwarePredicate p_286507_, DeserializationContext p_286849_) {
      ContextAwarePredicate contextawarepredicate = EntityPredicate.fromJson(p_286457_, "parent", p_286849_);
      ContextAwarePredicate contextawarepredicate1 = EntityPredicate.fromJson(p_286457_, "partner", p_286849_);
      ContextAwarePredicate contextawarepredicate2 = EntityPredicate.fromJson(p_286457_, "child", p_286849_);
      return new BredAnimalsTrigger.TriggerInstance(p_286507_, contextawarepredicate, contextawarepredicate1, contextawarepredicate2);
   }

   public void trigger(ServerPlayer pPlayer, Animal pParent, Animal pPartner, @Nullable AgeableMob pChild) {
      LootContext lootcontext = EntityPredicate.createContext(pPlayer, pParent);
      LootContext lootcontext1 = EntityPredicate.createContext(pPlayer, pPartner);
      LootContext lootcontext2 = pChild != null ? EntityPredicate.createContext(pPlayer, pChild) : null;
      this.trigger(pPlayer, (p_18653_) -> {
         return p_18653_.matches(lootcontext, lootcontext1, lootcontext2);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate parent;
      private final ContextAwarePredicate partner;
      private final ContextAwarePredicate child;

      public TriggerInstance(ContextAwarePredicate p_286459_, ContextAwarePredicate p_286695_, ContextAwarePredicate p_286476_, ContextAwarePredicate p_286433_) {
         super(BredAnimalsTrigger.ID, p_286459_);
         this.parent = p_286695_;
         this.partner = p_286476_;
         this.child = p_286433_;
      }

      public static BredAnimalsTrigger.TriggerInstance bredAnimals() {
         return new BredAnimalsTrigger.TriggerInstance(ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY);
      }

      public static BredAnimalsTrigger.TriggerInstance bredAnimals(EntityPredicate.Builder pChildBuilder) {
         return new BredAnimalsTrigger.TriggerInstance(ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, EntityPredicate.wrap(pChildBuilder.build()));
      }

      public static BredAnimalsTrigger.TriggerInstance bredAnimals(EntityPredicate pParent, EntityPredicate pPartner, EntityPredicate pChild) {
         return new BredAnimalsTrigger.TriggerInstance(ContextAwarePredicate.ANY, EntityPredicate.wrap(pParent), EntityPredicate.wrap(pPartner), EntityPredicate.wrap(pChild));
      }

      public boolean matches(LootContext pParentContext, LootContext pPartnerContext, @Nullable LootContext pChildContext) {
         if (this.child == ContextAwarePredicate.ANY || pChildContext != null && this.child.matches(pChildContext)) {
            return this.parent.matches(pParentContext) && this.partner.matches(pPartnerContext) || this.parent.matches(pPartnerContext) && this.partner.matches(pParentContext);
         } else {
            return false;
         }
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("parent", this.parent.toJson(pConditions));
         jsonobject.add("partner", this.partner.toJson(pConditions));
         jsonobject.add("child", this.child.toJson(pConditions));
         return jsonobject;
      }
   }
}