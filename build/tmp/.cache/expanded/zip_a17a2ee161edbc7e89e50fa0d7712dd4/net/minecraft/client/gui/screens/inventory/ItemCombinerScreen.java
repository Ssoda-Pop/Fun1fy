package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ItemCombinerScreen<T extends ItemCombinerMenu> extends AbstractContainerScreen<T> implements ContainerListener {
   private final ResourceLocation menuResource;

   public ItemCombinerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation pMenuResource) {
      super(pMenu, pPlayerInventory, pTitle);
      this.menuResource = pMenuResource;
   }

   protected void subInit() {
   }

   protected void init() {
      super.init();
      this.subInit();
      this.menu.addSlotListener(this);
   }

   public void removed() {
      super.removed();
      this.menu.removeSlotListener(this);
   }

   public void render(GuiGraphics p_281810_, int p_283312_, int p_283420_, float p_282956_) {
      this.renderBackground(p_281810_);
      super.render(p_281810_, p_283312_, p_283420_, p_282956_);
      this.renderFg(p_281810_, p_283312_, p_283420_, p_282956_);
      this.renderTooltip(p_281810_, p_283312_, p_283420_);
   }

   protected void renderFg(GuiGraphics p_283399_, int p_98928_, int p_98929_, float p_98930_) {
   }

   protected void renderBg(GuiGraphics p_282749_, float p_283494_, int p_283098_, int p_282054_) {
      p_282749_.blit(this.menuResource, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      this.renderErrorIcon(p_282749_, this.leftPos, this.topPos);
   }

   protected abstract void renderErrorIcon(GuiGraphics p_281990_, int p_266822_, int p_267045_);

   public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
   }

   /**
    * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
    * contents of that slot.
    */
   public void slotChanged(AbstractContainerMenu pContainerToSend, int pSlotInd, ItemStack pStack) {
   }
}