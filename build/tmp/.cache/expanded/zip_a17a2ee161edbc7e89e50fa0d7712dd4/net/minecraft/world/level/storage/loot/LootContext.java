package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * LootContext stores various context information for loot generation.
 * This includes the Level as well as any known {@link LootContextParam}s.
 */
public class LootContext {
   private final LootParams params;
   private final RandomSource random;
   private final LootDataResolver lootDataResolver;
   private final Set<LootContext.VisitedEntry<?>> visitedElements = Sets.newLinkedHashSet();

   LootContext(LootParams p_287722_, RandomSource p_287702_, LootDataResolver p_287619_) {
      this.params = p_287722_;
      this.random = p_287702_;
      this.lootDataResolver = p_287619_;
   }

   /**
    * Check whether the given parameter is present in this context.
    */
   public boolean hasParam(LootContextParam<?> pParameter) {
      return this.params.hasParam(pParameter);
   }

   /**
    * Get the value of the given parameter.
    * 
    * @throws NoSuchElementException if the parameter is not present in this context
    */
   public <T> T getParam(LootContextParam<T> pParam) {
      return this.params.getParameter(pParam);
   }

   /**
    * Add the dynamic drops for the given dynamic drops name to the given consumer.
    * If no dynamic drops provider for the given name has been registered to this LootContext, nothing is generated.
    * 
    * @see DynamicDrops
    */
   public void addDynamicDrops(ResourceLocation pName, Consumer<ItemStack> pConsumer) {
      this.params.addDynamicDrops(pName, pConsumer);
   }

   /**
    * Get the value of the given parameter if it is present in this context, null otherwise.
    */
   @Nullable
   public <T> T getParamOrNull(LootContextParam<T> pParameter) {
      return this.params.getParamOrNull(pParameter);
   }

   public boolean hasVisitedElement(LootContext.VisitedEntry<?> p_279182_) {
      return this.visitedElements.contains(p_279182_);
   }

   public boolean pushVisitedElement(LootContext.VisitedEntry<?> p_279152_) {
      return this.visitedElements.add(p_279152_);
   }

   public void popVisitedElement(LootContext.VisitedEntry<?> p_279198_) {
      this.visitedElements.remove(p_279198_);
   }

   public LootDataResolver getResolver() {
      return this.lootDataResolver;
   }

   public RandomSource getRandom() {
      return this.random;
   }

   /**
    * The luck value for this loot context. This is usually just the player's {@linkplain Attributes#LUCK luck value},
    * however it may be modified depending on the context of the looting.
    * When fishing for example it is increased based on the Luck of the Sea enchantment.
    */
   public float getLuck() {
      return this.params.getLuck();
   }

   public ServerLevel getLevel() {
      return this.params.getLevel();
   }

   public static LootContext.VisitedEntry<LootTable> createVisitedEntry(LootTable p_279327_) {
      return new LootContext.VisitedEntry<>(LootDataType.TABLE, p_279327_);
   }

   public static LootContext.VisitedEntry<LootItemCondition> createVisitedEntry(LootItemCondition p_279250_) {
      return new LootContext.VisitedEntry<>(LootDataType.PREDICATE, p_279250_);
   }

   public static LootContext.VisitedEntry<LootItemFunction> createVisitedEntry(LootItemFunction p_279163_) {
      return new LootContext.VisitedEntry<>(LootDataType.MODIFIER, p_279163_);
   }

   // ============================== FORGE START ==============================
   public int getLootingModifier() {
      return net.minecraftforge.common.ForgeHooks.getLootingLevel(getParamOrNull(LootContextParams.THIS_ENTITY), getParamOrNull(LootContextParams.KILLER_ENTITY), getParamOrNull(LootContextParams.DAMAGE_SOURCE));
   }

   private ResourceLocation queriedLootTableId;

   private LootContext(LootParams p_287722_, RandomSource p_287702_, LootDataResolver p_287619_, ResourceLocation queriedLootTableId) {
      this(p_287722_, p_287702_, p_287619_);
      this.queriedLootTableId = queriedLootTableId;
   }

   public void setQueriedLootTableId(ResourceLocation queriedLootTableId) {
      if (this.queriedLootTableId == null && queriedLootTableId != null) this.queriedLootTableId = queriedLootTableId;
   }

   public ResourceLocation getQueriedLootTableId() {
      return this.queriedLootTableId == null ? net.minecraftforge.common.loot.LootTableIdCondition.UNKNOWN_LOOT_TABLE : this.queriedLootTableId;
   }
   // =============================== FORGE END ===============================

   public static class Builder {
      private final LootParams params;
      @Nullable
      private RandomSource random;
      private ResourceLocation queriedLootTableId; // Forge: correctly pass around loot table ID with copy constructor

      public Builder(LootParams p_287628_) {
         this.params = p_287628_;
      }

      public Builder(LootContext context) {
         this.params = context.params;
         this.random = context.random;
         this.queriedLootTableId = context.queriedLootTableId;
      }

      public LootContext.Builder withOptionalRandomSeed(long pSeed) {
         if (pSeed != 0L) {
            this.random = RandomSource.create(pSeed);
         }

         return this;
      }

      public LootContext.Builder withQueriedLootTableId(ResourceLocation queriedLootTableId) {
         this.queriedLootTableId = queriedLootTableId;
         return this;
      }

      public ServerLevel getLevel() {
         return this.params.getLevel();
      }

      public LootContext create(@Nullable ResourceLocation p_287626_) {
         ServerLevel serverlevel = this.getLevel();
         MinecraftServer minecraftserver = serverlevel.getServer();
         RandomSource randomsource;
         if (this.random != null) {
            randomsource = this.random;
         } else if (p_287626_ != null) {
            randomsource = serverlevel.getRandomSequence(p_287626_);
         } else {
            randomsource = serverlevel.getRandom();
         }

         return new LootContext(this.params, randomsource, minecraftserver.getLootData(), queriedLootTableId);
      }
   }

   /**
    * Represents a type of entity that can be looked up in a {@link LootContext} using a {@link LootContextParam}.
    */
   public static enum EntityTarget {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      DIRECT_KILLER("direct_killer", LootContextParams.DIRECT_KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER);

      final String name;
      private final LootContextParam<? extends Entity> param;

      private EntityTarget(String pName, LootContextParam<? extends Entity> pParam) {
         this.name = pName;
         this.param = pParam;
      }

      public LootContextParam<? extends Entity> getParam() {
         return this.param;
      }

      // Forge: This method is patched in to expose the same name used in getByName so that ContextNbtProvider#forEntity serializes it properly
      public String getName() {
         return this.name;
      }

      public static LootContext.EntityTarget getByName(String pName) {
         for(LootContext.EntityTarget lootcontext$entitytarget : values()) {
            if (lootcontext$entitytarget.name.equals(pName)) {
               return lootcontext$entitytarget;
            }
         }

         throw new IllegalArgumentException("Invalid entity target " + pName);
      }

      public static class Serializer extends TypeAdapter<LootContext.EntityTarget> {
         public void write(JsonWriter pWriter, LootContext.EntityTarget pEntityTarget) throws IOException {
            pWriter.value(pEntityTarget.name);
         }

         public LootContext.EntityTarget read(JsonReader pReader) throws IOException {
            return LootContext.EntityTarget.getByName(pReader.nextString());
         }
      }
   }

   public static record VisitedEntry<T>(LootDataType<T> type, T value) {
   }
}
