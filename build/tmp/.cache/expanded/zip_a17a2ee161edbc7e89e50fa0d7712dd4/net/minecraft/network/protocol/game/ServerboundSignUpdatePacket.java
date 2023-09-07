package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundSignUpdatePacket implements Packet<ServerGamePacketListener> {
   private static final int MAX_STRING_LENGTH = 384;
   private final BlockPos pos;
   private final String[] lines;
   private final boolean isFrontText;

   public ServerboundSignUpdatePacket(BlockPos p_277902_, boolean p_277750_, String p_278086_, String p_277504_, String p_277814_, String p_277726_) {
      this.pos = p_277902_;
      this.isFrontText = p_277750_;
      this.lines = new String[]{p_278086_, p_277504_, p_277814_, p_277726_};
   }

   public ServerboundSignUpdatePacket(FriendlyByteBuf pBuffer) {
      this.pos = pBuffer.readBlockPos();
      this.isFrontText = pBuffer.readBoolean();
      this.lines = new String[4];

      for(int i = 0; i < 4; ++i) {
         this.lines[i] = pBuffer.readUtf(384);
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void write(FriendlyByteBuf pBuffer) {
      pBuffer.writeBlockPos(this.pos);
      pBuffer.writeBoolean(this.isFrontText);

      for(int i = 0; i < 4; ++i) {
         pBuffer.writeUtf(this.lines[i]);
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void handle(ServerGamePacketListener pHandler) {
      pHandler.handleSignUpdate(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public boolean isFrontText() {
      return this.isFrontText;
   }

   public String[] getLines() {
      return this.lines;
   }
}