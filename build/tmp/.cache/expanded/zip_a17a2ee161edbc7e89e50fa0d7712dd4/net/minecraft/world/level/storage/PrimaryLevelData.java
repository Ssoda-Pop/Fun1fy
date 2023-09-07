package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.minecraft.world.level.timers.TimerQueue;
import org.slf4j.Logger;

public class PrimaryLevelData implements ServerLevelData, WorldData {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected static final String PLAYER = "Player";
   protected static final String WORLD_GEN_SETTINGS = "WorldGenSettings";
   private LevelSettings settings;
   private final WorldOptions worldOptions;
   private final PrimaryLevelData.SpecialWorldProperty specialWorldProperty;
   private final Lifecycle worldGenSettingsLifecycle;
   private int xSpawn;
   private int ySpawn;
   private int zSpawn;
   private float spawnAngle;
   private long gameTime;
   private long dayTime;
   @Nullable
   private final DataFixer fixerUpper;
   private final int playerDataVersion;
   private boolean upgradedPlayerTag;
   @Nullable
   private CompoundTag loadedPlayerTag;
   private final int version;
   private int clearWeatherTime;
   private boolean raining;
   private int rainTime;
   private boolean thundering;
   private int thunderTime;
   private boolean initialized;
   private boolean difficultyLocked;
   private WorldBorder.Settings worldBorder;
   private EndDragonFight.Data endDragonFightData;
   @Nullable
   private CompoundTag customBossEvents;
   private int wanderingTraderSpawnDelay;
   private int wanderingTraderSpawnChance;
   @Nullable
   private UUID wanderingTraderId;
   private final Set<String> knownServerBrands;
   private boolean wasModded;
   private final Set<String> removedFeatureFlags;
   private final TimerQueue<MinecraftServer> scheduledEvents;
   private boolean confirmedExperimentalWarning = false;

   private PrimaryLevelData(@Nullable DataFixer p_277859_, int p_277672_, @Nullable CompoundTag p_277888_, boolean p_278109_, int p_277714_, int p_278088_, int p_278037_, float p_277542_, long p_277414_, long p_277635_, int p_277595_, int p_277794_, int p_278007_, boolean p_277943_, int p_277674_, boolean p_277644_, boolean p_277749_, boolean p_278004_, WorldBorder.Settings p_277729_, int p_277856_, int p_278051_, @Nullable UUID p_277341_, Set<String> p_277989_, Set<String> p_277399_, TimerQueue<MinecraftServer> p_277860_, @Nullable CompoundTag p_277936_, EndDragonFight.Data p_289764_, LevelSettings p_278064_, WorldOptions p_278072_, PrimaryLevelData.SpecialWorldProperty p_277548_, Lifecycle p_277915_) {
      this.fixerUpper = p_277859_;
      this.wasModded = p_278109_;
      this.xSpawn = p_277714_;
      this.ySpawn = p_278088_;
      this.zSpawn = p_278037_;
      this.spawnAngle = p_277542_;
      this.gameTime = p_277414_;
      this.dayTime = p_277635_;
      this.version = p_277595_;
      this.clearWeatherTime = p_277794_;
      this.rainTime = p_278007_;
      this.raining = p_277943_;
      this.thunderTime = p_277674_;
      this.thundering = p_277644_;
      this.initialized = p_277749_;
      this.difficultyLocked = p_278004_;
      this.worldBorder = p_277729_;
      this.wanderingTraderSpawnDelay = p_277856_;
      this.wanderingTraderSpawnChance = p_278051_;
      this.wanderingTraderId = p_277341_;
      this.knownServerBrands = p_277989_;
      this.removedFeatureFlags = p_277399_;
      this.loadedPlayerTag = p_277888_;
      this.playerDataVersion = p_277672_;
      this.scheduledEvents = p_277860_;
      this.customBossEvents = p_277936_;
      this.endDragonFightData = p_289764_;
      this.settings = p_278064_;
      this.worldOptions = p_278072_;
      this.specialWorldProperty = p_277548_;
      this.worldGenSettingsLifecycle = p_277915_;
   }

   public PrimaryLevelData(LevelSettings p_251081_, WorldOptions p_251666_, PrimaryLevelData.SpecialWorldProperty p_252268_, Lifecycle p_251714_) {
      this((DataFixer)null, SharedConstants.getCurrentVersion().getDataVersion().getVersion(), (CompoundTag)null, false, 0, 0, 0, 0.0F, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_SETTINGS, 0, 0, (UUID)null, Sets.newLinkedHashSet(), new HashSet<>(), new TimerQueue<>(TimerCallbacks.SERVER_CALLBACKS), (CompoundTag)null, EndDragonFight.Data.DEFAULT, p_251081_.copy(), p_251666_, p_252268_, p_251714_);
   }

   public static <T> PrimaryLevelData parse(Dynamic<T> p_78531_, DataFixer p_78532_, int p_78533_, @Nullable CompoundTag p_78534_, LevelSettings p_78535_, LevelVersion p_78536_, PrimaryLevelData.SpecialWorldProperty p_250651_, WorldOptions p_251864_, Lifecycle p_78538_) {
      long i = p_78531_.get("Time").asLong(0L);
      return new PrimaryLevelData(p_78532_, p_78533_, p_78534_, p_78531_.get("WasModded").asBoolean(false), p_78531_.get("SpawnX").asInt(0), p_78531_.get("SpawnY").asInt(0), p_78531_.get("SpawnZ").asInt(0), p_78531_.get("SpawnAngle").asFloat(0.0F), i, p_78531_.get("DayTime").asLong(i), p_78536_.levelDataVersion(), p_78531_.get("clearWeatherTime").asInt(0), p_78531_.get("rainTime").asInt(0), p_78531_.get("raining").asBoolean(false), p_78531_.get("thunderTime").asInt(0), p_78531_.get("thundering").asBoolean(false), p_78531_.get("initialized").asBoolean(true), p_78531_.get("DifficultyLocked").asBoolean(false), WorldBorder.Settings.read(p_78531_, WorldBorder.DEFAULT_SETTINGS), p_78531_.get("WanderingTraderSpawnDelay").asInt(0), p_78531_.get("WanderingTraderSpawnChance").asInt(0), p_78531_.get("WanderingTraderId").read(UUIDUtil.CODEC).result().orElse((UUID)null), p_78531_.get("ServerBrands").asStream().flatMap((p_78529_) -> {
         return p_78529_.asString().result().stream();
      }).collect(Collectors.toCollection(Sets::newLinkedHashSet)), p_78531_.get("removed_features").asStream().flatMap((p_277335_) -> {
         return p_277335_.asString().result().stream();
      }).collect(Collectors.toSet()), new TimerQueue<>(TimerCallbacks.SERVER_CALLBACKS, p_78531_.get("ScheduledEvents").asStream()), (CompoundTag)p_78531_.get("CustomBossEvents").orElseEmptyMap().getValue(), p_78531_.get("DragonFight").read(EndDragonFight.Data.CODEC).resultOrPartial(LOGGER::error).orElse(EndDragonFight.Data.DEFAULT), p_78535_, p_251864_, p_250651_, p_78538_).withConfirmedWarning(p_78538_ != Lifecycle.stable() && p_78531_.get("confirmedExperimentalSettings").asBoolean(false));
   }

   public CompoundTag createTag(RegistryAccess pRegistries, @Nullable CompoundTag pHostPlayerNBT) {
      this.updatePlayerTag();
      if (pHostPlayerNBT == null) {
         pHostPlayerNBT = this.loadedPlayerTag;
      }

      CompoundTag compoundtag = new CompoundTag();
      this.setTagData(pRegistries, compoundtag, pHostPlayerNBT);
      return compoundtag;
   }

   private void setTagData(RegistryAccess pRegistry, CompoundTag pNbt, @Nullable CompoundTag pPlayerNBT) {
      pNbt.put("ServerBrands", stringCollectionToTag(this.knownServerBrands));
      pNbt.putBoolean("WasModded", this.wasModded);
      if (!this.removedFeatureFlags.isEmpty()) {
         pNbt.put("removed_features", stringCollectionToTag(this.removedFeatureFlags));
      }

      CompoundTag compoundtag = new CompoundTag();
      compoundtag.putString("Name", SharedConstants.getCurrentVersion().getName());
      compoundtag.putInt("Id", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
      compoundtag.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().isStable());
      compoundtag.putString("Series", SharedConstants.getCurrentVersion().getDataVersion().getSeries());
      pNbt.put("Version", compoundtag);
      NbtUtils.addCurrentDataVersion(pNbt);
      DynamicOps<Tag> dynamicops = RegistryOps.create(NbtOps.INSTANCE, pRegistry);
      WorldGenSettings.encode(dynamicops, this.worldOptions, pRegistry).resultOrPartial(Util.prefix("WorldGenSettings: ", LOGGER::error)).ifPresent((p_78574_) -> {
         pNbt.put("WorldGenSettings", p_78574_);
      });
      pNbt.putInt("GameType", this.settings.gameType().getId());
      pNbt.putInt("SpawnX", this.xSpawn);
      pNbt.putInt("SpawnY", this.ySpawn);
      pNbt.putInt("SpawnZ", this.zSpawn);
      pNbt.putFloat("SpawnAngle", this.spawnAngle);
      pNbt.putLong("Time", this.gameTime);
      pNbt.putLong("DayTime", this.dayTime);
      pNbt.putLong("LastPlayed", Util.getEpochMillis());
      pNbt.putString("LevelName", this.settings.levelName());
      pNbt.putInt("version", 19133);
      pNbt.putInt("clearWeatherTime", this.clearWeatherTime);
      pNbt.putInt("rainTime", this.rainTime);
      pNbt.putBoolean("raining", this.raining);
      pNbt.putInt("thunderTime", this.thunderTime);
      pNbt.putBoolean("thundering", this.thundering);
      pNbt.putBoolean("hardcore", this.settings.hardcore());
      pNbt.putBoolean("allowCommands", this.settings.allowCommands());
      pNbt.putBoolean("initialized", this.initialized);
      this.worldBorder.write(pNbt);
      pNbt.putByte("Difficulty", (byte)this.settings.difficulty().getId());
      pNbt.putBoolean("DifficultyLocked", this.difficultyLocked);
      pNbt.put("GameRules", this.settings.gameRules().createTag());
      pNbt.put("DragonFight", Util.getOrThrow(EndDragonFight.Data.CODEC.encodeStart(NbtOps.INSTANCE, this.endDragonFightData), IllegalStateException::new));
      if (pPlayerNBT != null) {
         pNbt.put("Player", pPlayerNBT);
      }

      DataResult<Tag> dataresult = WorldDataConfiguration.CODEC.encodeStart(NbtOps.INSTANCE, this.settings.getDataConfiguration());
      dataresult.get().ifLeft((p_248505_) -> {
         pNbt.merge((CompoundTag)p_248505_);
      }).ifRight((p_248506_) -> {
         LOGGER.warn("Failed to encode configuration {}", (Object)p_248506_.message());
      });
      if (this.customBossEvents != null) {
         pNbt.put("CustomBossEvents", this.customBossEvents);
      }

      pNbt.put("ScheduledEvents", this.scheduledEvents.store());
      pNbt.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
      pNbt.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
      if (this.wanderingTraderId != null) {
         pNbt.putUUID("WanderingTraderId", this.wanderingTraderId);
      }

      pNbt.putString("forgeLifecycle", net.minecraftforge.common.ForgeHooks.encodeLifecycle(this.settings.getLifecycle()));
      pNbt.putBoolean("confirmedExperimentalSettings", this.confirmedExperimentalWarning);
   }

   private static ListTag stringCollectionToTag(Set<String> p_277880_) {
      ListTag listtag = new ListTag();
      p_277880_.stream().map(StringTag::valueOf).forEach(listtag::add);
      return listtag;
   }

   /**
    * Returns the x spawn position
    */
   public int getXSpawn() {
      return this.xSpawn;
   }

   /**
    * Return the Y axis spawning point of the player.
    */
   public int getYSpawn() {
      return this.ySpawn;
   }

   /**
    * Returns the z spawn position
    */
   public int getZSpawn() {
      return this.zSpawn;
   }

   public float getSpawnAngle() {
      return this.spawnAngle;
   }

   public long getGameTime() {
      return this.gameTime;
   }

   /**
    * Get current world time
    */
   public long getDayTime() {
      return this.dayTime;
   }

   private void updatePlayerTag() {
      if (!this.upgradedPlayerTag && this.loadedPlayerTag != null) {
         if (this.playerDataVersion < SharedConstants.getCurrentVersion().getDataVersion().getVersion()) {
            if (this.fixerUpper == null) {
               throw (NullPointerException)Util.pauseInIde(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
            }

            this.loadedPlayerTag = DataFixTypes.PLAYER.updateToCurrentVersion(this.fixerUpper, this.loadedPlayerTag, this.playerDataVersion);
         }

         this.upgradedPlayerTag = true;
      }
   }

   public CompoundTag getLoadedPlayerTag() {
      this.updatePlayerTag();
      return this.loadedPlayerTag;
   }

   /**
    * Set the x spawn position to the passed in value
    */
   public void setXSpawn(int pX) {
      this.xSpawn = pX;
   }

   /**
    * Sets the y spawn position
    */
   public void setYSpawn(int pY) {
      this.ySpawn = pY;
   }

   /**
    * Set the z spawn position to the passed in value
    */
   public void setZSpawn(int pZ) {
      this.zSpawn = pZ;
   }

   public void setSpawnAngle(float pAngle) {
      this.spawnAngle = pAngle;
   }

   public void setGameTime(long pTime) {
      this.gameTime = pTime;
   }

   /**
    * Set current world time
    */
   public void setDayTime(long pTime) {
      this.dayTime = pTime;
   }

   public void setSpawn(BlockPos pSpawnPoint, float pAngle) {
      this.xSpawn = pSpawnPoint.getX();
      this.ySpawn = pSpawnPoint.getY();
      this.zSpawn = pSpawnPoint.getZ();
      this.spawnAngle = pAngle;
   }

   /**
    * Get current world name
    */
   public String getLevelName() {
      return this.settings.levelName();
   }

   public int getVersion() {
      return this.version;
   }

   public int getClearWeatherTime() {
      return this.clearWeatherTime;
   }

   public void setClearWeatherTime(int pTime) {
      this.clearWeatherTime = pTime;
   }

   /**
    * Returns true if it is thundering, false otherwise.
    */
   public boolean isThundering() {
      return this.thundering;
   }

   /**
    * Sets whether it is thundering or not.
    */
   public void setThundering(boolean pThundering) {
      this.thundering = pThundering;
   }

   /**
    * Returns the number of ticks until next thunderbolt.
    */
   public int getThunderTime() {
      return this.thunderTime;
   }

   /**
    * Defines the number of ticks until next thunderbolt.
    */
   public void setThunderTime(int pTime) {
      this.thunderTime = pTime;
   }

   /**
    * Returns true if it is raining, false otherwise.
    */
   public boolean isRaining() {
      return this.raining;
   }

   /**
    * Sets whether it is raining or not.
    */
   public void setRaining(boolean pIsRaining) {
      this.raining = pIsRaining;
   }

   /**
    * Return the number of ticks until rain.
    */
   public int getRainTime() {
      return this.rainTime;
   }

   /**
    * Sets the number of ticks until rain.
    */
   public void setRainTime(int pTime) {
      this.rainTime = pTime;
   }

   /**
    * Gets the GameType.
    */
   public GameType getGameType() {
      return this.settings.gameType();
   }

   public void setGameType(GameType pType) {
      this.settings = this.settings.withGameType(pType);
   }

   /**
    * Returns true if hardcore mode is enabled, otherwise false
    */
   public boolean isHardcore() {
      return this.settings.hardcore();
   }

   /**
    * Returns true if commands are allowed on this World.
    */
   public boolean getAllowCommands() {
      return this.settings.allowCommands();
   }

   /**
    * Returns true if the World is initialized.
    */
   public boolean isInitialized() {
      return this.initialized;
   }

   /**
    * Sets the initialization status of the World.
    */
   public void setInitialized(boolean pInitialized) {
      this.initialized = pInitialized;
   }

   /**
    * Gets the GameRules class Instance.
    */
   public GameRules getGameRules() {
      return this.settings.gameRules();
   }

   public WorldBorder.Settings getWorldBorder() {
      return this.worldBorder;
   }

   public void setWorldBorder(WorldBorder.Settings pSerializer) {
      this.worldBorder = pSerializer;
   }

   public Difficulty getDifficulty() {
      return this.settings.difficulty();
   }

   public void setDifficulty(Difficulty pDifficulty) {
      this.settings = this.settings.withDifficulty(pDifficulty);
   }

   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   public void setDifficultyLocked(boolean pLocked) {
      this.difficultyLocked = pLocked;
   }

   public TimerQueue<MinecraftServer> getScheduledEvents() {
      return this.scheduledEvents;
   }

   public void fillCrashReportCategory(CrashReportCategory pCrashReportCategory, LevelHeightAccessor pLevel) {
      ServerLevelData.super.fillCrashReportCategory(pCrashReportCategory, pLevel);
      WorldData.super.fillCrashReportCategory(pCrashReportCategory);
   }

   public WorldOptions worldGenOptions() {
      return this.worldOptions;
   }

   public boolean isFlatWorld() {
      return this.specialWorldProperty == PrimaryLevelData.SpecialWorldProperty.FLAT;
   }

   public boolean isDebugWorld() {
      return this.specialWorldProperty == PrimaryLevelData.SpecialWorldProperty.DEBUG;
   }

   public Lifecycle worldGenSettingsLifecycle() {
      return this.worldGenSettingsLifecycle;
   }

   public EndDragonFight.Data endDragonFightData() {
      return this.endDragonFightData;
   }

   public void setEndDragonFightData(EndDragonFight.Data p_289770_) {
      this.endDragonFightData = p_289770_;
   }

   public WorldDataConfiguration getDataConfiguration() {
      return this.settings.getDataConfiguration();
   }

   public void setDataConfiguration(WorldDataConfiguration p_252328_) {
      this.settings = this.settings.withDataConfiguration(p_252328_);
   }

   @Nullable
   public CompoundTag getCustomBossEvents() {
      return this.customBossEvents;
   }

   public void setCustomBossEvents(@Nullable CompoundTag pNbt) {
      this.customBossEvents = pNbt;
   }

   public int getWanderingTraderSpawnDelay() {
      return this.wanderingTraderSpawnDelay;
   }

   public void setWanderingTraderSpawnDelay(int pDelay) {
      this.wanderingTraderSpawnDelay = pDelay;
   }

   public int getWanderingTraderSpawnChance() {
      return this.wanderingTraderSpawnChance;
   }

   public void setWanderingTraderSpawnChance(int pChance) {
      this.wanderingTraderSpawnChance = pChance;
   }

   @Nullable
   public UUID getWanderingTraderId() {
      return this.wanderingTraderId;
   }

   public void setWanderingTraderId(UUID pId) {
      this.wanderingTraderId = pId;
   }

   public void setModdedInfo(String pName, boolean pIsModded) {
      this.knownServerBrands.add(pName);
      this.wasModded |= pIsModded;
   }

   public boolean wasModded() {
      return this.wasModded;
   }

   public Set<String> getKnownServerBrands() {
      return ImmutableSet.copyOf(this.knownServerBrands);
   }

   public Set<String> getRemovedFeatureFlags() {
      return Set.copyOf(this.removedFeatureFlags);
   }

   public ServerLevelData overworldData() {
      return this;
   }

   public LevelSettings getLevelSettings() {
      return this.settings.copy();
   }

   public boolean hasConfirmedExperimentalWarning() {
      return this.confirmedExperimentalWarning;
   }

   public PrimaryLevelData withConfirmedWarning(boolean confirmedWarning) { // Builder-like to not patch ctor
      this.confirmedExperimentalWarning = confirmedWarning;
      return this;
   }

   /** @deprecated */
   @Deprecated
   public static enum SpecialWorldProperty {
      NONE,
      FLAT,
      DEBUG;
   }
}
