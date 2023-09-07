package net.minecraft.client.gui.screens;

import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsSubScreen extends Screen {
   protected final Screen lastScreen;
   protected final Options options;

   public OptionsSubScreen(Screen pLastScreen, Options pOptions, Component pTitle) {
      super(pTitle);
      this.lastScreen = pLastScreen;
      this.options = pOptions;
   }

   public void removed() {
      this.minecraft.options.save();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   protected void basicListRender(GuiGraphics p_282011_, OptionsList p_281793_, int p_281640_, int p_281598_, float p_281558_) {
      this.renderBackground(p_282011_);
      p_281793_.render(p_282011_, p_281640_, p_281598_, p_281558_);
      p_282011_.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
      super.render(p_282011_, p_281640_, p_281598_, p_281558_);
   }
}