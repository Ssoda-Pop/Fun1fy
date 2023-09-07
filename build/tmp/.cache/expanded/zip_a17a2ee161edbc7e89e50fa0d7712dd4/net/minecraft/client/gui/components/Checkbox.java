package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Checkbox extends AbstractButton {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
   private static final int TEXT_COLOR = 14737632;
   private boolean selected;
   private final boolean showLabel;

   public Checkbox(int pX, int pY, int pWidth, int pHeight, Component pMessage, boolean pSelected) {
      this(pX, pY, pWidth, pHeight, pMessage, pSelected, true);
   }

   public Checkbox(int pX, int pY, int pWidth, int pHeight, Component pMessage, boolean pSelected, boolean pShowLabel) {
      super(pX, pY, pWidth, pHeight, pMessage);
      this.selected = pSelected;
      this.showLabel = pShowLabel;
   }

   public void onPress() {
      this.selected = !this.selected;
   }

   public boolean selected() {
      return this.selected;
   }

   public void updateWidgetNarration(NarrationElementOutput p_260253_) {
      p_260253_.add(NarratedElementType.TITLE, this.createNarrationMessage());
      if (this.active) {
         if (this.isFocused()) {
            p_260253_.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
         } else {
            p_260253_.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
         }
      }

   }

   public void renderWidget(GuiGraphics p_283124_, int p_282925_, int p_282705_, float p_282612_) {
      Minecraft minecraft = Minecraft.getInstance();
      RenderSystem.enableDepthTest();
      Font font = minecraft.font;
      p_283124_.setColor(1.0F, 1.0F, 1.0F, this.alpha);
      RenderSystem.enableBlend();
      p_283124_.blit(TEXTURE, this.getX(), this.getY(), this.isFocused() ? 20.0F : 0.0F, this.selected ? 20.0F : 0.0F, 20, this.height, 64, 64);
      p_283124_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.showLabel) {
         p_283124_.drawString(font, this.getMessage(), this.getX() + 24, this.getY() + (this.height - 8) / 2, 14737632 | Mth.ceil(this.alpha * 255.0F) << 24);
      }

   }
}