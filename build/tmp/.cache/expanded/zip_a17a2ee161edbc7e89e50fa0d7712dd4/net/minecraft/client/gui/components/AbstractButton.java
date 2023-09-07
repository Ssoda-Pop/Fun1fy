package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractButton extends AbstractWidget {
   protected static final int TEXTURE_Y_OFFSET = 46;
   protected static final int TEXTURE_WIDTH = 200;
   protected static final int TEXTURE_HEIGHT = 20;
   protected static final int TEXTURE_BORDER_X = 20;
   protected static final int TEXTURE_BORDER_Y = 4;
   protected static final int TEXT_MARGIN = 2;

   public AbstractButton(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
      super(pX, pY, pWidth, pHeight, pMessage);
   }

   public abstract void onPress();

   public void renderWidget(GuiGraphics p_281670_, int p_282682_, int p_281714_, float p_282542_) {
      Minecraft minecraft = Minecraft.getInstance();
      p_281670_.setColor(1.0F, 1.0F, 1.0F, this.alpha);
      RenderSystem.enableBlend();
      RenderSystem.enableDepthTest();
      p_281670_.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
      p_281670_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      int i = getFGColor();
      this.renderString(p_281670_, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
   }

   public void renderString(GuiGraphics p_283366_, Font p_283054_, int p_281656_) {
      this.renderScrollingString(p_283366_, p_283054_, 2, p_281656_);
   }

   private int getTextureY() {
      int i = 1;
      if (!this.active) {
         i = 0;
      } else if (this.isHoveredOrFocused()) {
         i = 2;
      }

      return 46 + i * 20;
   }

   public void onClick(double pMouseX, double pMouseY) {
      this.onPress();
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (this.active && this.visible) {
         if (CommonInputs.selected(pKeyCode)) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
