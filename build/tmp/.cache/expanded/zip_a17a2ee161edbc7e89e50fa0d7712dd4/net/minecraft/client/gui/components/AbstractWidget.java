package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractWidget implements Renderable, GuiEventListener, LayoutElement, NarratableEntry {
   public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   public static final ResourceLocation ACCESSIBILITY_TEXTURE = new ResourceLocation("textures/gui/accessibility.png");
   private static final double PERIOD_PER_SCROLLED_PIXEL = 0.5D;
   private static final double MIN_SCROLL_PERIOD = 3.0D;
   protected int width;
   protected int height;
   private int x;
   private int y;
   private Component message;
   protected boolean isHovered;
   public boolean active = true;
   public boolean visible = true;
   protected float alpha = 1.0F;
   private int tabOrderGroup;
   private boolean focused;
   @Nullable
   private Tooltip tooltip;
   private int tooltipMsDelay;
   private long hoverOrFocusedStartTime;
   private boolean wasHoveredOrFocused;

   public AbstractWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
      this.x = pX;
      this.y = pY;
      this.width = pWidth;
      this.height = pHeight;
      this.message = pMessage;
   }

   public int getHeight() {
      return this.height;
   }

   public void render(GuiGraphics p_282421_, int p_93658_, int p_93659_, float p_93660_) {
      if (this.visible) {
         this.isHovered = p_93658_ >= this.getX() && p_93659_ >= this.getY() && p_93658_ < this.getX() + this.width && p_93659_ < this.getY() + this.height;
         this.renderWidget(p_282421_, p_93658_, p_93659_, p_93660_);
         this.updateTooltip();
      }
   }

   private void updateTooltip() {
      if (this.tooltip != null) {
         boolean flag = this.isHovered || this.isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard();
         if (flag != this.wasHoveredOrFocused) {
            if (flag) {
               this.hoverOrFocusedStartTime = Util.getMillis();
            }

            this.wasHoveredOrFocused = flag;
         }

         if (flag && Util.getMillis() - this.hoverOrFocusedStartTime > (long)this.tooltipMsDelay) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen != null) {
               screen.setTooltipForNextRenderPass(this.tooltip, this.createTooltipPositioner(), this.isFocused());
            }
         }

      }
   }

   protected ClientTooltipPositioner createTooltipPositioner() {
      return (ClientTooltipPositioner)(!this.isHovered && this.isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard() ? new BelowOrAboveWidgetTooltipPositioner(this) : new MenuTooltipPositioner(this));
   }

   public void setTooltip(@Nullable Tooltip p_259796_) {
      this.tooltip = p_259796_;
   }

   @Nullable
   public Tooltip getTooltip() {
      return this.tooltip;
   }

   public void setTooltipDelay(int p_259732_) {
      this.tooltipMsDelay = p_259732_;
   }

   protected MutableComponent createNarrationMessage() {
      return wrapDefaultNarrationMessage(this.getMessage());
   }

   public static MutableComponent wrapDefaultNarrationMessage(Component pMessage) {
      return Component.translatable("gui.narrate.button", pMessage);
   }

   public abstract void renderWidget(GuiGraphics p_282139_, int p_268034_, int p_268009_, float p_268085_);

   protected static void renderScrollingString(GuiGraphics p_281620_, Font p_282651_, Component p_281467_, int p_283621_, int p_282084_, int p_283398_, int p_281938_, int p_283471_) {
      int i = p_282651_.width(p_281467_);
      int j = (p_282084_ + p_281938_ - 9) / 2 + 1;
      int k = p_283398_ - p_283621_;
      if (i > k) {
         int l = i - k;
         double d0 = (double)Util.getMillis() / 1000.0D;
         double d1 = Math.max((double)l * 0.5D, 3.0D);
         double d2 = Math.sin((Math.PI / 2D) * Math.cos((Math.PI * 2D) * d0 / d1)) / 2.0D + 0.5D;
         double d3 = Mth.lerp(d2, 0.0D, (double)l);
         p_281620_.enableScissor(p_283621_, p_282084_, p_283398_, p_281938_);
         p_281620_.drawString(p_282651_, p_281467_, p_283621_ - (int)d3, j, p_283471_);
         p_281620_.disableScissor();
      } else {
         p_281620_.drawCenteredString(p_282651_, p_281467_, (p_283621_ + p_283398_) / 2, j, p_283471_);
      }

   }

   protected void renderScrollingString(GuiGraphics p_281857_, Font p_282790_, int p_282664_, int p_282944_) {
      int i = this.getX() + p_282664_;
      int j = this.getX() + this.getWidth() - p_282664_;
      renderScrollingString(p_281857_, p_282790_, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight(), p_282944_);
   }

   public void renderTexture(GuiGraphics p_283546_, ResourceLocation p_281674_, int p_281808_, int p_282444_, int p_283651_, int p_281601_, int p_283472_, int p_282390_, int p_281441_, int p_281711_, int p_281541_) {
      int i = p_281601_;
      if (!this.isActive()) {
         i = p_281601_ + p_283472_ * 2;
      } else if (this.isHoveredOrFocused()) {
         i = p_281601_ + p_283472_;
      }

      RenderSystem.enableDepthTest();
      p_283546_.blit(p_281674_, p_281808_, p_282444_, (float)p_283651_, (float)i, p_282390_, p_281441_, p_281711_, p_281541_);
   }

   public void onClick(double pMouseX, double pMouseY) {
   }

   public void onRelease(double pMouseX, double pMouseY) {
   }

   protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (this.active && this.visible) {
         if (this.isValidClickButton(pButton)) {
            boolean flag = this.clicked(pMouseX, pMouseY);
            if (flag) {
               this.playDownSound(Minecraft.getInstance().getSoundManager());
               this.onClick(pMouseX, pMouseY);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      if (this.isValidClickButton(pButton)) {
         this.onRelease(pMouseX, pMouseY);
         return true;
      } else {
         return false;
      }
   }

   protected boolean isValidClickButton(int pButton) {
      return pButton == 0;
   }

   public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      if (this.isValidClickButton(pButton)) {
         this.onDrag(pMouseX, pMouseY, pDragX, pDragY);
         return true;
      } else {
         return false;
      }
   }

   protected boolean clicked(double pMouseX, double pMouseY) {
      return this.active && this.visible && pMouseX >= (double)this.getX() && pMouseY >= (double)this.getY() && pMouseX < (double)(this.getX() + this.width) && pMouseY < (double)(this.getY() + this.height);
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent p_265640_) {
      if (this.active && this.visible) {
         return !this.isFocused() ? ComponentPath.leaf(this) : null;
      } else {
         return null;
      }
   }

   public boolean isMouseOver(double pMouseX, double pMouseY) {
      return this.active && this.visible && pMouseX >= (double)this.getX() && pMouseY >= (double)this.getY() && pMouseX < (double)(this.getX() + this.width) && pMouseY < (double)(this.getY() + this.height);
   }

   public void playDownSound(SoundManager pHandler) {
      pHandler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int pWidth) {
      this.width = pWidth;
   }

   public void setHeight(int value) {
      this.height = value;
   }

   public void setAlpha(float pAlpha) {
      this.alpha = pAlpha;
   }

   public void setMessage(Component pMessage) {
      this.message = pMessage;
   }

   public Component getMessage() {
      return this.message;
   }

   public boolean isFocused() {
      return this.focused;
   }

   public boolean isHovered() {
      return this.isHovered;
   }

   public boolean isHoveredOrFocused() {
      return this.isHovered() || this.isFocused();
   }

   public boolean isActive() {
      return this.visible && this.active;
   }

   public void setFocused(boolean pFocused) {
      this.focused = pFocused;
   }

   public static final int UNSET_FG_COLOR = -1;
   protected int packedFGColor = UNSET_FG_COLOR;
   public int getFGColor() {
      if (packedFGColor != UNSET_FG_COLOR) return packedFGColor;
      return this.active ? 16777215 : 10526880; // White : Light Grey
   }
   public void setFGColor(int color) {
      this.packedFGColor = color;
   }
   public void clearFGColor() {
      this.packedFGColor = UNSET_FG_COLOR;
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      if (this.isFocused()) {
         return NarratableEntry.NarrationPriority.FOCUSED;
      } else {
         return this.isHovered ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
      }
   }

   public final void updateNarration(NarrationElementOutput p_259921_) {
      this.updateWidgetNarration(p_259921_);
      if (this.tooltip != null) {
         this.tooltip.updateNarration(p_259921_);
      }

   }

   protected abstract void updateWidgetNarration(NarrationElementOutput p_259858_);

   protected void defaultButtonNarrationText(NarrationElementOutput pNarrationElementOutput) {
      pNarrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
      if (this.active) {
         if (this.isFocused()) {
            pNarrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.focused"));
         } else {
            pNarrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
         }
      }

   }

   public int getX() {
      return this.x;
   }

   public void setX(int p_254495_) {
      this.x = p_254495_;
   }

   public int getY() {
      return this.y;
   }

   public void setY(int p_253718_) {
      this.y = p_253718_;
   }

   public void visitWidgets(Consumer<AbstractWidget> p_265566_) {
      p_265566_.accept(this);
   }

   public ScreenRectangle getRectangle() {
      return LayoutElement.super.getRectangle();
   }

   public int getTabOrderGroup() {
      return this.tabOrderGroup;
   }

   public void setTabOrderGroup(int p_268123_) {
      this.tabOrderGroup = p_268123_;
   }
}
