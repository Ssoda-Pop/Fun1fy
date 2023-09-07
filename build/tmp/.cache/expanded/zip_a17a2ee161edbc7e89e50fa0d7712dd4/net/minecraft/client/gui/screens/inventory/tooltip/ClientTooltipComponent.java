package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public interface ClientTooltipComponent {
   static ClientTooltipComponent create(FormattedCharSequence pText) {
      return new ClientTextTooltip(pText);
   }

   static ClientTooltipComponent create(TooltipComponent pVisualTooltipComponent) {
      if (pVisualTooltipComponent instanceof BundleTooltip) {
         return new ClientBundleTooltip((BundleTooltip)pVisualTooltipComponent);
      } else {
         ClientTooltipComponent result = net.minecraftforge.client.gui.ClientTooltipComponentManager.createClientTooltipComponent(pVisualTooltipComponent);
         if (result != null) return result;
         throw new IllegalArgumentException("Unknown TooltipComponent");
      }
   }

   int getHeight();

   int getWidth(Font pFont);

   default void renderText(Font p_169953_, int p_169954_, int p_169955_, Matrix4f p_253692_, MultiBufferSource.BufferSource p_169957_) {
   }

   default void renderImage(Font p_194048_, int p_194049_, int p_194050_, GuiGraphics p_283459_) {
   }
}
