package net.minecraft.network.protocol.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;

public record ClientboundSetEntityDataPacket(int id, List<SynchedEntityData.DataValue<?>> packedItems) implements Packet<ClientGamePacketListener> {
   public static final int EOF_MARKER = 255;

   public ClientboundSetEntityDataPacket(FriendlyByteBuf pBuffer) {
      this(pBuffer.readVarInt(), unpack(pBuffer));
   }

   private static void pack(List<SynchedEntityData.DataValue<?>> p_253940_, FriendlyByteBuf p_253901_) {
      for(SynchedEntityData.DataValue<?> datavalue : p_253940_) {
         datavalue.write(p_253901_);
      }

      p_253901_.writeByte(255);
   }

   private static List<SynchedEntityData.DataValue<?>> unpack(FriendlyByteBuf p_253726_) {
      List<SynchedEntityData.DataValue<?>> list = new ArrayList<>();

      int i;
      while((i = p_253726_.readUnsignedByte()) != 255) {
         list.add(SynchedEntityData.DataValue.read(p_253726_, i));
      }

      return list;
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void write(FriendlyByteBuf pBuffer) {
      pBuffer.writeVarInt(this.id);
      pack(this.packedItems, pBuffer);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void handle(ClientGamePacketListener pHandler) {
      pHandler.handleSetEntityData(this);
   }
}