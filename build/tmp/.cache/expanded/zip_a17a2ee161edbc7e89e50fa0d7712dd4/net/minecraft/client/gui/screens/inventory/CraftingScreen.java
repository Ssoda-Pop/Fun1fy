package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CraftingScreen extends AbstractContainerScreen<CraftingMenu> implements RecipeUpdateListener {
   private static final ResourceLocation CRAFTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/crafting_table.png");
   private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
   private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
   private boolean widthTooNarrow;

   public CraftingScreen(CraftingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
      super(pMenu, pPlayerInventory, pTitle);
   }

   protected void init() {
      super.init();
      this.widthTooNarrow = this.width < 379;
      this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
      this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
      this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (p_289630_) -> {
         this.recipeBookComponent.toggleVisibility();
         this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
         p_289630_.setPosition(this.leftPos + 5, this.height / 2 - 49);
      }));
      this.addWidget(this.recipeBookComponent);
      this.setInitialFocus(this.recipeBookComponent);
      this.titleLabelX = 29;
   }

   public void containerTick() {
      super.containerTick();
      this.recipeBookComponent.tick();
   }

   public void render(GuiGraphics p_282508_, int p_98480_, int p_98481_, float p_98482_) {
      this.renderBackground(p_282508_);
      if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
         this.renderBg(p_282508_, p_98482_, p_98480_, p_98481_);
         this.recipeBookComponent.render(p_282508_, p_98480_, p_98481_, p_98482_);
      } else {
         this.recipeBookComponent.render(p_282508_, p_98480_, p_98481_, p_98482_);
         super.render(p_282508_, p_98480_, p_98481_, p_98482_);
         this.recipeBookComponent.renderGhostRecipe(p_282508_, this.leftPos, this.topPos, true, p_98482_);
      }

      this.renderTooltip(p_282508_, p_98480_, p_98481_);
      this.recipeBookComponent.renderTooltip(p_282508_, this.leftPos, this.topPos, p_98480_, p_98481_);
   }

   protected void renderBg(GuiGraphics p_283540_, float p_282132_, int p_283078_, int p_283647_) {
      int i = this.leftPos;
      int j = (this.height - this.imageHeight) / 2;
      p_283540_.blit(CRAFTING_TABLE_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
   }

   protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
      return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(pX, pY, pWidth, pHeight, pMouseX, pMouseY);
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (this.recipeBookComponent.mouseClicked(pMouseX, pMouseY, pButton)) {
         this.setFocused(this.recipeBookComponent);
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? true : super.mouseClicked(pMouseX, pMouseY, pButton);
      }
   }

   protected boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop, int pMouseButton) {
      boolean flag = pMouseX < (double)pGuiLeft || pMouseY < (double)pGuiTop || pMouseX >= (double)(pGuiLeft + this.imageWidth) || pMouseY >= (double)(pGuiTop + this.imageHeight);
      return this.recipeBookComponent.hasClickedOutside(pMouseX, pMouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, pMouseButton) && flag;
   }

   /**
    * Called when the mouse is clicked over a slot or outside the gui.
    */
   protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
      super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
      this.recipeBookComponent.slotClicked(pSlot);
   }

   public void recipesUpdated() {
      this.recipeBookComponent.recipesUpdated();
   }

   public RecipeBookComponent getRecipeBookComponent() {
      return this.recipeBookComponent;
   }
}