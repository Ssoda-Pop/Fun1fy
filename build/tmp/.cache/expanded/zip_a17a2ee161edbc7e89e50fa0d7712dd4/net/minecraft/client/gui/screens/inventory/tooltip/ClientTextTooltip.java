package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ClientTextTooltip implements ClientTooltipComponent {
   private final FormattedCharSequence text;

   public ClientTextTooltip(FormattedCharSequence pText) {
      this.text = pText;
   }

   public int getWidth(Font pFont) {
      return pFont.width(this.text);
   }

   public int getHeight() {
      return 10;
   }

   public void renderText(Font p_254285_, int p_254192_, int p_253697_, Matrix4f p_253880_, MultiBufferSource.BufferSource p_254231_) {
      p_254285_.drawInBatch(this.text, (float)p_254192_, (float)p_253697_, -1, true, p_253880_, p_254231_, Font.DisplayMode.NORMAL, 0, 15728880);
   }
}