package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ObjectSelectionList<E extends ObjectSelectionList.Entry<E>> extends AbstractSelectionList<E> {
   private static final Component USAGE_NARRATION = Component.translatable("narration.selection.usage");

   public ObjectSelectionList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
      super(pMinecraft, pWidth, pHeight, pY0, pY1, pItemHeight);
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent p_265150_) {
      if (this.getItemCount() == 0) {
         return null;
      } else if (this.isFocused() && p_265150_ instanceof FocusNavigationEvent.ArrowNavigation) {
         FocusNavigationEvent.ArrowNavigation focusnavigationevent$arrownavigation = (FocusNavigationEvent.ArrowNavigation)p_265150_;
         E e1 = this.nextEntry(focusnavigationevent$arrownavigation.direction());
         return e1 != null ? ComponentPath.path(this, ComponentPath.leaf(e1)) : null;
      } else if (!this.isFocused()) {
         E e = this.getSelected();
         if (e == null) {
            e = this.nextEntry(p_265150_.getVerticalDirectionForInitialFocus());
         }

         return e == null ? null : ComponentPath.path(this, ComponentPath.leaf(e));
      } else {
         return null;
      }
   }

   public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
      E e = this.getHovered();
      if (e != null) {
         this.narrateListElementPosition(pNarrationElementOutput.nest(), e);
         e.updateNarration(pNarrationElementOutput);
      } else {
         E e1 = this.getSelected();
         if (e1 != null) {
            this.narrateListElementPosition(pNarrationElementOutput.nest(), e1);
            e1.updateNarration(pNarrationElementOutput);
         }
      }

      if (this.isFocused()) {
         pNarrationElementOutput.add(NarratedElementType.USAGE, USAGE_NARRATION);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry<E extends ObjectSelectionList.Entry<E>> extends AbstractSelectionList.Entry<E> implements NarrationSupplier {
      public abstract Component getNarration();

      public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
         pNarrationElementOutput.add(NarratedElementType.TITLE, this.getNarration());
      }
   }
}