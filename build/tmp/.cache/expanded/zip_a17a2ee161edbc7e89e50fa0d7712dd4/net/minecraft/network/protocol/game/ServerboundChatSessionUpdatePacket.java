package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatSessionUpdatePacket(RemoteChatSession.Data chatSession) implements Packet<ServerGamePacketListener> {
   public ServerboundChatSessionUpdatePacket(FriendlyByteBuf p_254010_) {
      this(RemoteChatSession.Data.read(p_254010_));
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void write(FriendlyByteBuf p_253690_) {
      RemoteChatSession.Data.write(p_253690_, this.chatSession);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void handle(ServerGamePacketListener p_253620_) {
      p_253620_.handleChatSessionUpdate(this);
   }
}