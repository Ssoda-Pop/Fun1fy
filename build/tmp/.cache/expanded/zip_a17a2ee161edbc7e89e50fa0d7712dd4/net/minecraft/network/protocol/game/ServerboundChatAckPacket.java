package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatAckPacket(int offset) implements Packet<ServerGamePacketListener> {
   public ServerboundChatAckPacket(FriendlyByteBuf p_242339_) {
      this(p_242339_.readVarInt());
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void write(FriendlyByteBuf p_242345_) {
      p_242345_.writeVarInt(this.offset);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void handle(ServerGamePacketListener p_242391_) {
      p_242391_.handleChatAck(this);
   }
}