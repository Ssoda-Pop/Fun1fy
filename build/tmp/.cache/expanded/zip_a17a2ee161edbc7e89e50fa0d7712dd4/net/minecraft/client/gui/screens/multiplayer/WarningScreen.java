package net.minecraft.client.gui.screens.multiplayer;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class WarningScreen extends Screen {
   private final Component content;
   @Nullable
   private final Component check;
   private final Component narration;
   @Nullable
   protected Checkbox stopShowing;
   private MultiLineLabel message = MultiLineLabel.EMPTY;

   protected WarningScreen(Component pTitle, Component pContent, Component pNarration) {
      this(pTitle, pContent, (Component)null, pNarration);
   }

   protected WarningScreen(Component pTitle, Component pContent, @Nullable Component pCheck, Component pNarration) {
      super(pTitle);
      this.content = pContent;
      this.check = pCheck;
      this.narration = pNarration;
   }

   protected abstract void initButtons(int pYOffset);

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.content, this.width - 100);
      int i = (this.message.getLineCount() + 1) * this.getLineHeight();
      if (this.check != null) {
         int j = this.font.width(this.check);
         this.stopShowing = new Checkbox(this.width / 2 - j / 2 - 8, 76 + i, j + 24, 20, this.check, false);
         this.addRenderableWidget(this.stopShowing);
      }

      this.initButtons(i);
   }

   public Component getNarrationMessage() {
      return this.narration;
   }

   public void render(GuiGraphics p_282073_, int p_283174_, int p_282617_, float p_282654_) {
      this.renderBackground(p_282073_);
      this.renderTitle(p_282073_);
      int i = this.width / 2 - this.message.getWidth() / 2;
      this.message.renderLeftAligned(p_282073_, i, 70, this.getLineHeight(), 16777215);
      super.render(p_282073_, p_283174_, p_282617_, p_282654_);
   }

   protected void renderTitle(GuiGraphics p_281725_) {
      p_281725_.drawString(this.font, this.title, 25, 30, 16777215);
   }

   protected int getLineHeight() {
      return 9 * 2;
   }
}