package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StateSwitchingButton extends AbstractWidget {
   protected ResourceLocation resourceLocation;
   protected boolean isStateTriggered;
   protected int xTexStart;
   protected int yTexStart;
   protected int xDiffTex;
   protected int yDiffTex;

   public StateSwitchingButton(int pX, int pY, int pWidth, int pHeight, boolean pInitialState) {
      super(pX, pY, pWidth, pHeight, CommonComponents.EMPTY);
      this.isStateTriggered = pInitialState;
   }

   public void initTextureValues(int pXTexStart, int pYTexStart, int pXDiffTex, int pYDiffTex, ResourceLocation pResourceLocation) {
      this.xTexStart = pXTexStart;
      this.yTexStart = pYTexStart;
      this.xDiffTex = pXDiffTex;
      this.yDiffTex = pYDiffTex;
      this.resourceLocation = pResourceLocation;
   }

   public void setStateTriggered(boolean pTriggered) {
      this.isStateTriggered = pTriggered;
   }

   public boolean isStateTriggered() {
      return this.isStateTriggered;
   }

   public void updateWidgetNarration(NarrationElementOutput p_259073_) {
      this.defaultButtonNarrationText(p_259073_);
   }

   public void renderWidget(GuiGraphics p_283051_, int p_283010_, int p_281379_, float p_283453_) {
      RenderSystem.disableDepthTest();
      int i = this.xTexStart;
      int j = this.yTexStart;
      if (this.isStateTriggered) {
         i += this.xDiffTex;
      }

      if (this.isHoveredOrFocused()) {
         j += this.yDiffTex;
      }

      p_283051_.blit(this.resourceLocation, this.getX(), this.getY(), i, j, this.width, this.height);
      RenderSystem.enableDepthTest();
   }
}