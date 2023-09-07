package net.minecraft.world.entity.vehicle;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class MinecartChest extends AbstractMinecartContainer {
   public MinecartChest(EntityType<? extends MinecartChest> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public MinecartChest(Level pLevel, double pX, double pY, double pZ) {
      super(EntityType.CHEST_MINECART, pX, pY, pZ, pLevel);
   }

   protected Item getDropItem() {
      return Items.CHEST_MINECART;
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getContainerSize() {
      return 27;
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.CHEST;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH);
   }

   public int getDefaultDisplayOffset() {
      return 8;
   }

   public AbstractContainerMenu createMenu(int pId, Inventory pPlayerInventory) {
      return ChestMenu.threeRows(pId, pPlayerInventory, this);
   }

   public void stopOpen(Player p_270111_) {
      this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of(p_270111_));
   }

   public InteractionResult interact(Player p_270398_, InteractionHand p_270576_) {
      InteractionResult interactionresult = this.interactWithContainerVehicle(p_270398_);
      if (interactionresult.consumesAction()) {
         this.gameEvent(GameEvent.CONTAINER_OPEN, p_270398_);
         PiglinAi.angerNearbyPiglins(p_270398_, true);
      }

      return interactionresult;
   }
}