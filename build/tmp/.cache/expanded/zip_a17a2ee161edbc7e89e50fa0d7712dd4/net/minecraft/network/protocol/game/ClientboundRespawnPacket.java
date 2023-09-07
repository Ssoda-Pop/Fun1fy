package net.minecraft.network.protocol.game;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ClientboundRespawnPacket implements Packet<ClientGamePacketListener> {
   public static final byte KEEP_ATTRIBUTES = 1;
   public static final byte KEEP_ENTITY_DATA = 2;
   public static final byte KEEP_ALL_DATA = 3;
   private final ResourceKey<DimensionType> dimensionType;
   private final ResourceKey<Level> dimension;
   /** First 8 bytes of the SHA-256 hash of the world's seed */
   private final long seed;
   private final GameType playerGameType;
   @Nullable
   private final GameType previousPlayerGameType;
   private final boolean isDebug;
   private final boolean isFlat;
   private final byte dataToKeep;
   private final Optional<GlobalPos> lastDeathLocation;
   private final int portalCooldown;

   public ClientboundRespawnPacket(ResourceKey<DimensionType> p_287723_, ResourceKey<Level> p_287745_, long p_287746_, GameType p_287624_, @Nullable GameType p_287780_, boolean p_287655_, boolean p_287735_, byte p_287694_, Optional<GlobalPos> p_287615_, int p_287636_) {
      this.dimensionType = p_287723_;
      this.dimension = p_287745_;
      this.seed = p_287746_;
      this.playerGameType = p_287624_;
      this.previousPlayerGameType = p_287780_;
      this.isDebug = p_287655_;
      this.isFlat = p_287735_;
      this.dataToKeep = p_287694_;
      this.lastDeathLocation = p_287615_;
      this.portalCooldown = p_287636_;
   }

   public ClientboundRespawnPacket(FriendlyByteBuf pBuffer) {
      this.dimensionType = pBuffer.readResourceKey(Registries.DIMENSION_TYPE);
      this.dimension = pBuffer.readResourceKey(Registries.DIMENSION);
      this.seed = pBuffer.readLong();
      this.playerGameType = GameType.byId(pBuffer.readUnsignedByte());
      this.previousPlayerGameType = GameType.byNullableId(pBuffer.readByte());
      this.isDebug = pBuffer.readBoolean();
      this.isFlat = pBuffer.readBoolean();
      this.dataToKeep = pBuffer.readByte();
      this.lastDeathLocation = pBuffer.readOptional(FriendlyByteBuf::readGlobalPos);
      this.portalCooldown = pBuffer.readVarInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void write(FriendlyByteBuf pBuffer) {
      pBuffer.writeResourceKey(this.dimensionType);
      pBuffer.writeResourceKey(this.dimension);
      pBuffer.writeLong(this.seed);
      pBuffer.writeByte(this.playerGameType.getId());
      pBuffer.writeByte(GameType.getNullableId(this.previousPlayerGameType));
      pBuffer.writeBoolean(this.isDebug);
      pBuffer.writeBoolean(this.isFlat);
      pBuffer.writeByte(this.dataToKeep);
      pBuffer.writeOptional(this.lastDeathLocation, FriendlyByteBuf::writeGlobalPos);
      pBuffer.writeVarInt(this.portalCooldown);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void handle(ClientGamePacketListener pHandler) {
      pHandler.handleRespawn(this);
   }

   public ResourceKey<DimensionType> getDimensionType() {
      return this.dimensionType;
   }

   public ResourceKey<Level> getDimension() {
      return this.dimension;
   }

   /**
    * get value
    */
   public long getSeed() {
      return this.seed;
   }

   public GameType getPlayerGameType() {
      return this.playerGameType;
   }

   @Nullable
   public GameType getPreviousPlayerGameType() {
      return this.previousPlayerGameType;
   }

   public boolean isDebug() {
      return this.isDebug;
   }

   public boolean isFlat() {
      return this.isFlat;
   }

   public boolean shouldKeep(byte p_263573_) {
      return (this.dataToKeep & p_263573_) != 0;
   }

   public Optional<GlobalPos> getLastDeathLocation() {
      return this.lastDeathLocation;
   }

   public int getPortalCooldown() {
      return this.portalCooldown;
   }
}