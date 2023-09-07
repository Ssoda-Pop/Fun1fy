package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.slf4j.Logger;

public class ChiseledBookShelfBlockEntity extends BlockEntity implements Container {
   public static final int MAX_BOOKS_IN_STORAGE = 6;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
   private int lastInteractedSlot = -1;

   public ChiseledBookShelfBlockEntity(BlockPos p_249541_, BlockState p_251752_) {
      super(BlockEntityType.CHISELED_BOOKSHELF, p_249541_, p_251752_);
   }

   private void updateState(int p_261806_) {
      if (p_261806_ >= 0 && p_261806_ < 6) {
         this.lastInteractedSlot = p_261806_;
         BlockState blockstate = this.getBlockState();

         for(int i = 0; i < ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.size(); ++i) {
            boolean flag = !this.getItem(i).isEmpty();
            BooleanProperty booleanproperty = ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i);
            blockstate = blockstate.setValue(booleanproperty, Boolean.valueOf(flag));
         }

         Objects.requireNonNull(this.level).setBlock(this.worldPosition, blockstate, 3);
      } else {
         LOGGER.error("Expected slot 0-5, got {}", (int)p_261806_);
      }
   }

   public void load(CompoundTag p_249911_) {
      this.items.clear();
      ContainerHelper.loadAllItems(p_249911_, this.items);
      this.lastInteractedSlot = p_249911_.getInt("last_interacted_slot");
   }

   protected void saveAdditional(CompoundTag p_251872_) {
      ContainerHelper.saveAllItems(p_251872_, this.items, true);
      p_251872_.putInt("last_interacted_slot", this.lastInteractedSlot);
   }

   public int count() {
      return (int)this.items.stream().filter(Predicate.not(ItemStack::isEmpty)).count();
   }

   public void clearContent() {
      this.items.clear();
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getContainerSize() {
      return 6;
   }

   public boolean isEmpty() {
      return this.items.stream().allMatch(ItemStack::isEmpty);
   }

   /**
    * Returns the stack in the given slot.
    */
   public ItemStack getItem(int p_256203_) {
      return this.items.get(p_256203_);
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack removeItem(int p_255828_, int p_255673_) {
      ItemStack itemstack = Objects.requireNonNullElse(this.items.get(p_255828_), ItemStack.EMPTY);
      this.items.set(p_255828_, ItemStack.EMPTY);
      if (!itemstack.isEmpty()) {
         this.updateState(p_255828_);
      }

      return itemstack;
   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   public ItemStack removeItemNoUpdate(int p_255874_) {
      return this.removeItem(p_255874_, 1);
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setItem(int p_256610_, ItemStack p_255789_) {
      if (p_255789_.is(ItemTags.BOOKSHELF_BOOKS)) {
         this.items.set(p_256610_, p_255789_);
         this.updateState(p_256610_);
      }

   }

   public boolean canTakeItem(Container p_282172_, int p_281387_, ItemStack p_283257_) {
      return p_282172_.hasAnyMatching((p_281577_) -> {
         if (p_281577_.isEmpty()) {
            return true;
         } else {
            return ItemStack.isSameItemSameTags(p_283257_, p_281577_) && p_281577_.getCount() + p_283257_.getCount() <= Math.min(p_281577_.getMaxStackSize(), p_282172_.getMaxStackSize());
         }
      });
   }

   /**
    * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
    */
   public int getMaxStackSize() {
      return 1;
   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean stillValid(Player p_256481_) {
      return Container.stillValidBlockEntity(this, p_256481_);
   }

   /**
    * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
    * guis use Slot.isItemValid
    */
   public boolean canPlaceItem(int p_256567_, ItemStack p_255922_) {
      return p_255922_.is(ItemTags.BOOKSHELF_BOOKS) && this.getItem(p_256567_).isEmpty();
   }

   public int getLastInteractedSlot() {
      return this.lastInteractedSlot;
   }
}