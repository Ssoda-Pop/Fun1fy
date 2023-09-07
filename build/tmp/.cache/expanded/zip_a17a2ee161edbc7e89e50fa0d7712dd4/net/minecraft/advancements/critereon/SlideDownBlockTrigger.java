package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SlideDownBlockTrigger extends SimpleCriterionTrigger<SlideDownBlockTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("slide_down_block");

   public ResourceLocation getId() {
      return ID;
   }

   public SlideDownBlockTrigger.TriggerInstance createInstance(JsonObject p_286879_, ContextAwarePredicate p_286565_, DeserializationContext p_286581_) {
      Block block = deserializeBlock(p_286879_);
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(p_286879_.get("state"));
      if (block != null) {
         statepropertiespredicate.checkState(block.getStateDefinition(), (p_66983_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_66983_);
         });
      }

      return new SlideDownBlockTrigger.TriggerInstance(p_286565_, block, statepropertiespredicate);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject pJson) {
      if (pJson.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(pJson, "block"));
         return BuiltInRegistries.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayer pPlayer, BlockState pState) {
      this.trigger(pPlayer, (p_66986_) -> {
         return p_66986_.matches(pState);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final Block block;
      private final StatePropertiesPredicate state;

      public TriggerInstance(ContextAwarePredicate p_286920_, @Nullable Block p_286622_, StatePropertiesPredicate p_286692_) {
         super(SlideDownBlockTrigger.ID, p_286920_);
         this.block = p_286622_;
         this.state = p_286692_;
      }

      public static SlideDownBlockTrigger.TriggerInstance slidesDownBlock(Block pBlock) {
         return new SlideDownBlockTrigger.TriggerInstance(ContextAwarePredicate.ANY, pBlock, StatePropertiesPredicate.ANY);
      }

      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         if (this.block != null) {
            jsonobject.addProperty("block", BuiltInRegistries.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("state", this.state.serializeToJson());
         return jsonobject;
      }

      public boolean matches(BlockState pState) {
         if (this.block != null && !pState.is(this.block)) {
            return false;
         } else {
            return this.state.matches(pState);
         }
      }
   }
}