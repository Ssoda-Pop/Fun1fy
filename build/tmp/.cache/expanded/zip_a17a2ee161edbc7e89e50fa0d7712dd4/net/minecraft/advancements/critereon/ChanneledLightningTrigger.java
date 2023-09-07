package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class ChanneledLightningTrigger extends SimpleCriterionTrigger<ChanneledLightningTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

   public ResourceLocation getId() {
      return ID;
   }

   public ChanneledLightningTrigger.TriggerInstance createInstance(JsonObject p_286858_, ContextAwarePredicate p_286240_, DeserializationContext p_286562_) {
      ContextAwarePredicate[] acontextawarepredicate = EntityPredicate.fromJsonArray(p_286858_, "victims", p_286562_);
      return new ChanneledLightningTrigger.TriggerInstance(p_286240_, acontextawarepredicate);
   }

   public void trigger(ServerPlayer pPlayer, Collection<? extends Entity> pEntityTriggered) {
      List<LootContext> list = pEntityTriggered.stream().map((p_21720_) -> {
         return EntityPredicate.createContext(pPlayer, p_21720_);
      }).collect(Collectors.toList());
      this.trigger(pPlayer, (p_21730_) -> {
         return p_21730_.matches(list);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate[] victims;

      public TriggerInstance(ContextAwarePredicate p_286697_, ContextAwarePredicate[] p_286366_) {
         super(ChanneledLightningTrigger.ID, p_286697_);
         this.victims = p_286366_;
      }

      public static ChanneledLightningTrigger.TriggerInstance channeledLightning(EntityPredicate... pVictims) {
         return new ChanneledLightningTrigger.TriggerInstance(ContextAwarePredicate.ANY, Stream.of(pVictims).map(EntityPredicate::wrap).toArray((p_286116_) -> {
            return new ContextAwarePredicate[p_286116_];
         }));
      }

      public boolean matches(Collection<? extends LootContext> pVictims) {
         for(ContextAwarePredicate contextawarepredicate : this.victims) {
            boolean flag = false;

            for(LootContext lootcontext : pVictims) {
               if (contextawarepredicate.matches(lootcontext)) {
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

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("victims", ContextAwarePredicate.toJson(this.victims, pConditions));
         return jsonobject;
      }
   }
}