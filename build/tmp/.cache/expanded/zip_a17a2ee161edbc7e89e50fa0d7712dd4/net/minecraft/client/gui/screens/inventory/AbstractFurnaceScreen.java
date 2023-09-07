package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceMenu> extends AbstractContainerScreen<T> implements RecipeUpdateListener {
   private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
   public final AbstractFurnaceRecipeBookComponent recipeBookComponent;
   private boolean widthTooNarrow;
   private final ResourceLocation texture;

   public AbstractFurnaceScreen(T pMenu, AbstractFurnaceRecipeBookComponent pRecipeBookComponent, Inventory pPlayerInventory, Component pTitle, ResourceLocation pTexture) {
      super(pMenu, pPlayerInventory, pTitle);
      this.recipeBookComponent = pRecipeBookComponent;
      this.texture = pTexture;
   }

   public void init() {
      super.init();
      this.widthTooNarrow = this.width < 379;
      this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
      this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
      this.addRenderableWidget(new ImageButton(this.leftPos + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (p_289628_) -> {
         this.recipeBookComponent.toggleVisibility();
         this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
         p_289628_.setPosition(this.leftPos + 20, this.height / 2 - 49);
      }));
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
   }

   public void containerTick() {
      super.containerTick();
      this.recipeBookComponent.tick();
   }

   public void render(GuiGraphics p_282573_, int p_97859_, int p_97860_, float p_97861_) {
      this.renderBackground(p_282573_);
      if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
         this.renderBg(p_282573_, p_97861_, p_97859_, p_97860_);
         this.recipeBookComponent.render(p_282573_, p_97859_, p_97860_, p_97861_);
      } else {
         this.recipeBookComponent.render(p_282573_, p_97859_, p_97860_, p_97861_);
         super.render(p_282573_, p_97859_, p_97860_, p_97861_);
         this.recipeBookComponent.renderGhostRecipe(p_282573_, this.leftPos, this.topPos, true, p_97861_);
      }

      this.renderTooltip(p_282573_, p_97859_, p_97860_);
      this.recipeBookComponent.renderTooltip(p_282573_, this.leftPos, this.topPos, p_97859_, p_97860_);
   }

   protected void renderBg(GuiGraphics p_282928_, float p_281631_, int p_281252_, int p_281891_) {
      int i = this.leftPos;
      int j = this.topPos;
      p_282928_.blit(this.texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
      if (this.menu.isLit()) {
         int k = this.menu.getLitProgress();
         p_282928_.blit(this.texture, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
      }

      int l = this.menu.getBurnProgress();
      p_282928_.blit(this.texture, i + 79, j + 34, 176, 14, l + 1, 16);
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (this.recipeBookComponent.mouseClicked(pMouseX, pMouseY, pButton)) {
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? true : super.mouseClicked(pMouseX, pMouseY, pButton);
      }
   }

   /**
    * Called when the mouse is clicked over a slot or outside the gui.
    */
   protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
      super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
      this.recipeBookComponent.slotClicked(pSlot);
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      return this.recipeBookComponent.keyPressed(pKeyCode, pScanCode, pModifiers) ? false : super.keyPressed(pKeyCode, pScanCode, pModifiers);
   }

   protected boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop, int pMouseButton) {
      boolean flag = pMouseX < (double)pGuiLeft || pMouseY < (double)pGuiTop || pMouseX >= (double)(pGuiLeft + this.imageWidth) || pMouseY >= (double)(pGuiTop + this.imageHeight);
      return this.recipeBookComponent.hasClickedOutside(pMouseX, pMouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, pMouseButton) && flag;
   }

   public boolean charTyped(char pCodePoint, int pModifiers) {
      return this.recipeBookComponent.charTyped(pCodePoint, pModifiers) ? true : super.charTyped(pCodePoint, pModifiers);
   }

   public void recipesUpdated() {
      this.recipeBookComponent.recipesUpdated();
   }

   public RecipeBookComponent getRecipeBookComponent() {
      return this.recipeBookComponent;
   }
}