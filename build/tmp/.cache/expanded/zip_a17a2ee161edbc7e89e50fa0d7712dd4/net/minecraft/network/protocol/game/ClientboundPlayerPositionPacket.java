package net.minecraft.network.protocol.game;

import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.RelativeMovement;

public class ClientboundPlayerPositionPacket implements Packet<ClientGamePacketListener> {
   private final double x;
   private final double y;
   private final double z;
   private final float yRot;
   private final float xRot;
   private final Set<RelativeMovement> relativeArguments;
   private final int id;

   public ClientboundPlayerPositionPacket(double p_275438_, double p_275354_, double p_275276_, float p_275280_, float p_275203_, Set<RelativeMovement> p_275228_, int p_275614_) {
      this.x = p_275438_;
      this.y = p_275354_;
      this.z = p_275276_;
      this.yRot = p_275280_;
      this.xRot = p_275203_;
      this.relativeArguments = p_275228_;
      this.id = p_275614_;
   }

   public ClientboundPlayerPositionPacket(FriendlyByteBuf pBuffer) {
      this.x = pBuffer.readDouble();
      this.y = pBuffer.readDouble();
      this.z = pBuffer.readDouble();
      this.yRot = pBuffer.readFloat();
      this.xRot = pBuffer.readFloat();
      this.relativeArguments = RelativeMovement.unpack(pBuffer.readUnsignedByte());
      this.id = pBuffer.readVarInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void write(FriendlyByteBuf pBuffer) {
      pBuffer.writeDouble(this.x);
      pBuffer.writeDouble(this.y);
      pBuffer.writeDouble(this.z);
      pBuffer.writeFloat(this.yRot);
      pBuffer.writeFloat(this.xRot);
      pBuffer.writeByte(RelativeMovement.pack(this.relativeArguments));
      pBuffer.writeVarInt(this.id);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void handle(ClientGamePacketListener pHandler) {
      pHandler.handleMovePlayer(this);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYRot() {
      return this.yRot;
   }

   public float getXRot() {
      return this.xRot;
   }

   public int getId() {
      return this.id;
   }

   /**
    * Returns a set of which fields are relative. Items in this set indicate that the value is a relative change applied
    * to the player's position, rather than an exact value.
    */
   public Set<RelativeMovement> getRelativeArguments() {
      return this.relativeArguments;
   }
}