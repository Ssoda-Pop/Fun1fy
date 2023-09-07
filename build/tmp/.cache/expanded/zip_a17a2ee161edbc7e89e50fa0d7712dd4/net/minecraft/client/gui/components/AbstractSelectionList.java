package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSelectionList<E extends AbstractSelectionList.Entry<E>> extends AbstractContainerEventHandler implements Renderable, NarratableEntry {
   protected final Minecraft minecraft;
   protected final int itemHeight;
   private final List<E> children = new AbstractSelectionList.TrackedList();
   protected int width;
   protected int height;
   protected int y0;
   protected int y1;
   protected int x1;
   protected int x0;
   protected boolean centerListVertically = true;
   private double scrollAmount;
   private boolean renderSelection = true;
   private boolean renderHeader;
   protected int headerHeight;
   private boolean scrolling;
   @Nullable
   private E selected;
   private boolean renderBackground = true;
   private boolean renderTopAndBottom = true;
   @Nullable
   private E hovered;

   public AbstractSelectionList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
      this.minecraft = pMinecraft;
      this.width = pWidth;
      this.height = pHeight;
      this.y0 = pY0;
      this.y1 = pY1;
      this.itemHeight = pItemHeight;
      this.x0 = 0;
      this.x1 = pWidth;
   }

   public void setRenderSelection(boolean pRenderSelection) {
      this.renderSelection = pRenderSelection;
   }

   protected void setRenderHeader(boolean pRenderHeader, int pHeaderHeight) {
      this.renderHeader = pRenderHeader;
      this.headerHeight = pHeaderHeight;
      if (!pRenderHeader) {
         this.headerHeight = 0;
      }

   }

   public int getRowWidth() {
      return 220;
   }

   @Nullable
   public E getSelected() {
      return this.selected;
   }

   public void setSelected(@Nullable E pSelected) {
      this.selected = pSelected;
   }

   public E getFirstElement() {
      return this.children.get(0);
   }

   public void setRenderBackground(boolean pRenderBackground) {
      this.renderBackground = pRenderBackground;
   }

   public void setRenderTopAndBottom(boolean pRenderTopAndButton) {
      this.renderTopAndBottom = pRenderTopAndButton;
   }

   @Nullable
   public E getFocused() {
      return (E)(super.getFocused());
   }

   public final List<E> children() {
      return this.children;
   }

   protected void clearEntries() {
      this.children.clear();
      this.selected = null;
   }

   protected void replaceEntries(Collection<E> pEntries) {
      this.clearEntries();
      this.children.addAll(pEntries);
   }

   protected E getEntry(int pIndex) {
      return this.children().get(pIndex);
   }

   protected int addEntry(E pEntry) {
      this.children.add(pEntry);
      return this.children.size() - 1;
   }

   protected void addEntryToTop(E pEntry) {
      double d0 = (double)this.getMaxScroll() - this.getScrollAmount();
      this.children.add(0, pEntry);
      this.setScrollAmount((double)this.getMaxScroll() - d0);
   }

   protected boolean removeEntryFromTop(E pEntry) {
      double d0 = (double)this.getMaxScroll() - this.getScrollAmount();
      boolean flag = this.removeEntry(pEntry);
      this.setScrollAmount((double)this.getMaxScroll() - d0);
      return flag;
   }

   protected int getItemCount() {
      return this.children().size();
   }

   protected boolean isSelectedItem(int pIndex) {
      return Objects.equals(this.getSelected(), this.children().get(pIndex));
   }

   @Nullable
   protected final E getEntryAtPosition(double pMouseX, double pMouseY) {
      int i = this.getRowWidth() / 2;
      int j = this.x0 + this.width / 2;
      int k = j - i;
      int l = j + i;
      int i1 = Mth.floor(pMouseY - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
      int j1 = i1 / this.itemHeight;
      return (E)(pMouseX < (double)this.getScrollbarPosition() && pMouseX >= (double)k && pMouseX <= (double)l && j1 >= 0 && i1 >= 0 && j1 < this.getItemCount() ? this.children().get(j1) : null);
   }

   public void updateSize(int pWidth, int pHeight, int pY0, int pY1) {
      this.width = pWidth;
      this.height = pHeight;
      this.y0 = pY0;
      this.y1 = pY1;
      this.x0 = 0;
      this.x1 = pWidth;
   }

   public void setLeftPos(int pX0) {
      this.x0 = pX0;
      this.x1 = pX0 + this.width;
   }

   protected int getMaxPosition() {
      return this.getItemCount() * this.itemHeight + this.headerHeight;
   }

   protected void clickedHeader(int pMouseX, int pMouseY) {
   }

   protected void renderHeader(GuiGraphics p_282337_, int p_93444_, int p_93445_) {
   }

   protected void renderBackground(GuiGraphics p_283512_) {
   }

   protected void renderDecorations(GuiGraphics p_281477_, int p_93459_, int p_93460_) {
   }

   public void render(GuiGraphics p_282708_, int p_283242_, int p_282891_, float p_283683_) {
      this.renderBackground(p_282708_);
      int i = this.getScrollbarPosition();
      int j = i + 6;
      this.hovered = this.isMouseOver((double)p_283242_, (double)p_282891_) ? this.getEntryAtPosition((double)p_283242_, (double)p_282891_) : null;
      if (this.renderBackground) {
         p_282708_.setColor(0.125F, 0.125F, 0.125F, 1.0F);
         int k = 32;
         p_282708_.blit(Screen.BACKGROUND_LOCATION, this.x0, this.y0, (float)this.x1, (float)(this.y1 + (int)this.getScrollAmount()), this.x1 - this.x0, this.y1 - this.y0, 32, 32);
         p_282708_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      int l1 = this.getRowLeft();
      int l = this.y0 + 4 - (int)this.getScrollAmount();
      this.enableScissor(p_282708_);
      if (this.renderHeader) {
         this.renderHeader(p_282708_, l1, l);
      }

      this.renderList(p_282708_, p_283242_, p_282891_, p_283683_);
      p_282708_.disableScissor();
      if (this.renderTopAndBottom) {
         int i1 = 32;
         p_282708_.setColor(0.25F, 0.25F, 0.25F, 1.0F);
         p_282708_.blit(Screen.BACKGROUND_LOCATION, this.x0, 0, 0.0F, 0.0F, this.width, this.y0, 32, 32);
         p_282708_.blit(Screen.BACKGROUND_LOCATION, this.x0, this.y1, 0.0F, (float)this.y1, this.width, this.height - this.y1, 32, 32);
         p_282708_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         int j1 = 4;
         p_282708_.fillGradient(RenderType.guiOverlay(), this.x0, this.y0, this.x1, this.y0 + 4, -16777216, 0, 0);
         p_282708_.fillGradient(RenderType.guiOverlay(), this.x0, this.y1 - 4, this.x1, this.y1, 0, -16777216, 0);
      }

      int i2 = this.getMaxScroll();
      if (i2 > 0) {
         int j2 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
         j2 = Mth.clamp(j2, 32, this.y1 - this.y0 - 8);
         int k1 = (int)this.getScrollAmount() * (this.y1 - this.y0 - j2) / i2 + this.y0;
         if (k1 < this.y0) {
            k1 = this.y0;
         }

         p_282708_.fill(i, this.y0, j, this.y1, -16777216);
         p_282708_.fill(i, k1, j, k1 + j2, -8355712);
         p_282708_.fill(i, k1, j - 1, k1 + j2 - 1, -4144960);
      }

      this.renderDecorations(p_282708_, p_283242_, p_282891_);
      RenderSystem.disableBlend();
   }

   protected void enableScissor(GuiGraphics p_282811_) {
      p_282811_.enableScissor(this.x0, this.y0, this.x1, this.y1);
   }

   protected void centerScrollOn(E pEntry) {
      this.setScrollAmount((double)(this.children().indexOf(pEntry) * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2));
   }

   protected void ensureVisible(E pEntry) {
      int i = this.getRowTop(this.children().indexOf(pEntry));
      int j = i - this.y0 - 4 - this.itemHeight;
      if (j < 0) {
         this.scroll(j);
      }

      int k = this.y1 - i - this.itemHeight - this.itemHeight;
      if (k < 0) {
         this.scroll(-k);
      }

   }

   private void scroll(int pScroll) {
      this.setScrollAmount(this.getScrollAmount() + (double)pScroll);
   }

   public double getScrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(double pScroll) {
      this.scrollAmount = Mth.clamp(pScroll, 0.0D, (double)this.getMaxScroll());
   }

   public int getMaxScroll() {
      return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
   }

   public int getScrollBottom() {
      return (int)this.getScrollAmount() - this.height - this.headerHeight;
   }

   protected void updateScrollingState(double pMouseX, double pMouseY, int pButton) {
      this.scrolling = pButton == 0 && pMouseX >= (double)this.getScrollbarPosition() && pMouseX < (double)(this.getScrollbarPosition() + 6);
   }

   protected int getScrollbarPosition() {
      return this.width / 2 + 124;
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      this.updateScrollingState(pMouseX, pMouseY, pButton);
      if (!this.isMouseOver(pMouseX, pMouseY)) {
         return false;
      } else {
         E e = this.getEntryAtPosition(pMouseX, pMouseY);
         if (e != null) {
            if (e.mouseClicked(pMouseX, pMouseY, pButton)) {
               E e1 = this.getFocused();
               if (e1 != e && e1 instanceof ContainerEventHandler) {
                  ContainerEventHandler containereventhandler = (ContainerEventHandler)e1;
                  containereventhandler.setFocused((GuiEventListener)null);
               }

               this.setFocused(e);
               this.setDragging(true);
               return true;
            }
         } else if (pButton == 0) {
            this.clickedHeader((int)(pMouseX - (double)(this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(pMouseY - (double)this.y0) + (int)this.getScrollAmount() - 4);
            return true;
         }

         return this.scrolling;
      }
   }

   public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      if (this.getFocused() != null) {
         this.getFocused().mouseReleased(pMouseX, pMouseY, pButton);
      }

      return false;
   }

   public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      if (super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)) {
         return true;
      } else if (pButton == 0 && this.scrolling) {
         if (pMouseY < (double)this.y0) {
            this.setScrollAmount(0.0D);
         } else if (pMouseY > (double)this.y1) {
            this.setScrollAmount((double)this.getMaxScroll());
         } else {
            double d0 = (double)Math.max(1, this.getMaxScroll());
            int i = this.y1 - this.y0;
            int j = Mth.clamp((int)((float)(i * i) / (float)this.getMaxPosition()), 32, i - 8);
            double d1 = Math.max(1.0D, d0 / (double)(i - j));
            this.setScrollAmount(this.getScrollAmount() + pDragY * d1);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
      this.setScrollAmount(this.getScrollAmount() - pDelta * (double)this.itemHeight / 2.0D);
      return true;
   }

   public void setFocused(@Nullable GuiEventListener p_265738_) {
      super.setFocused(p_265738_);
      int i = this.children.indexOf(p_265738_);
      if (i >= 0) {
         E e = this.children.get(i);
         this.setSelected(e);
         if (this.minecraft.getLastInputType().isKeyboard()) {
            this.ensureVisible(e);
         }
      }

   }

   @Nullable
   protected E nextEntry(ScreenDirection p_265160_) {
      return this.nextEntry(p_265160_, (p_93510_) -> {
         return true;
      });
   }

   @Nullable
   protected E nextEntry(ScreenDirection p_265210_, Predicate<E> p_265604_) {
      return this.nextEntry(p_265210_, p_265604_, this.getSelected());
   }

   @Nullable
   protected E nextEntry(ScreenDirection p_265159_, Predicate<E> p_265109_, @Nullable E p_265379_) {
      byte b0;
      switch (p_265159_) {
         case RIGHT:
         case LEFT:
            b0 = 0;
            break;
         case UP:
            b0 = -1;
            break;
         case DOWN:
            b0 = 1;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      int i = b0;
      if (!this.children().isEmpty() && i != 0) {
         int j;
         if (p_265379_ == null) {
            j = i > 0 ? 0 : this.children().size() - 1;
         } else {
            j = this.children().indexOf(p_265379_) + i;
         }

         for(int k = j; k >= 0 && k < this.children.size(); k += i) {
            E e = this.children().get(k);
            if (p_265109_.test(e)) {
               return e;
            }
         }
      }

      return (E)null;
   }

   public boolean isMouseOver(double pMouseX, double pMouseY) {
      return pMouseY >= (double)this.y0 && pMouseY <= (double)this.y1 && pMouseX >= (double)this.x0 && pMouseX <= (double)this.x1;
   }

   protected void renderList(GuiGraphics p_282079_, int p_239229_, int p_239230_, float p_239231_) {
      int i = this.getRowLeft();
      int j = this.getRowWidth();
      int k = this.itemHeight - 4;
      int l = this.getItemCount();

      for(int i1 = 0; i1 < l; ++i1) {
         int j1 = this.getRowTop(i1);
         int k1 = this.getRowBottom(i1);
         if (k1 >= this.y0 && j1 <= this.y1) {
            this.renderItem(p_282079_, p_239229_, p_239230_, p_239231_, i1, i, j1, j, k);
         }
      }

   }

   protected void renderItem(GuiGraphics p_282205_, int p_238966_, int p_238967_, float p_238968_, int p_238969_, int p_238970_, int p_238971_, int p_238972_, int p_238973_) {
      E e = this.getEntry(p_238969_);
      e.renderBack(p_282205_, p_238969_, p_238971_, p_238970_, p_238972_, p_238973_, p_238966_, p_238967_, Objects.equals(this.hovered, e), p_238968_);
      if (this.renderSelection && this.isSelectedItem(p_238969_)) {
         int i = this.isFocused() ? -1 : -8355712;
         this.renderSelection(p_282205_, p_238971_, p_238972_, p_238973_, i, -16777216);
      }

      e.render(p_282205_, p_238969_, p_238971_, p_238970_, p_238972_, p_238973_, p_238966_, p_238967_, Objects.equals(this.hovered, e), p_238968_);
   }

   protected void renderSelection(GuiGraphics p_283589_, int p_240142_, int p_240143_, int p_240144_, int p_240145_, int p_240146_) {
      int i = this.x0 + (this.width - p_240143_) / 2;
      int j = this.x0 + (this.width + p_240143_) / 2;
      p_283589_.fill(i, p_240142_ - 2, j, p_240142_ + p_240144_ + 2, p_240145_);
      p_283589_.fill(i + 1, p_240142_ - 1, j - 1, p_240142_ + p_240144_ + 1, p_240146_);
   }

   public int getRowLeft() {
      return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   public int getRowRight() {
      return this.getRowLeft() + this.getRowWidth();
   }

   protected int getRowTop(int pIndex) {
      return this.y0 + 4 - (int)this.getScrollAmount() + pIndex * this.itemHeight + this.headerHeight;
   }

   protected int getRowBottom(int pIndex) {
      return this.getRowTop(pIndex) + this.itemHeight;
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      if (this.isFocused()) {
         return NarratableEntry.NarrationPriority.FOCUSED;
      } else {
         return this.hovered != null ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
      }
   }

   @Nullable
   protected E remove(int pIndex) {
      E e = this.children.get(pIndex);
      return (E)(this.removeEntry(this.children.get(pIndex)) ? e : null);
   }

   protected boolean removeEntry(E pEntry) {
      boolean flag = this.children.remove(pEntry);
      if (flag && pEntry == this.getSelected()) {
         this.setSelected((E)null);
      }

      return flag;
   }

   @Nullable
   protected E getHovered() {
      return this.hovered;
   }

   void bindEntryToSelf(AbstractSelectionList.Entry<E> pEntry) {
      pEntry.list = this;
   }

   protected void narrateListElementPosition(NarrationElementOutput pNarrationElementOutput, E pEntry) {
      List<E> list = this.children();
      if (list.size() > 1) {
         int i = list.indexOf(pEntry);
         if (i != -1) {
            pNarrationElementOutput.add(NarratedElementType.POSITION, Component.translatable("narrator.position.list", i + 1, list.size()));
         }
      }

   }

   public ScreenRectangle getRectangle() {
      return new ScreenRectangle(this.x0, this.y0, this.x1 - this.x0, this.y1 - this.y0);
   }

   public int getWidth() { return this.width; }
   public int getHeight() { return this.height; }
   public int getTop() { return this.y0; }
   public int getBottom() { return this.y1; }
   public int getLeft() { return this.x0; }
   public int getRight() { return this.x1; }

   @OnlyIn(Dist.CLIENT)
   protected abstract static class Entry<E extends AbstractSelectionList.Entry<E>> implements GuiEventListener {
      /** @deprecated */
      @Deprecated
      protected AbstractSelectionList<E> list;

      public void setFocused(boolean p_265302_) {
      }

      public boolean isFocused() {
         return this.list.getFocused() == this;
      }

      public abstract void render(GuiGraphics p_283112_, int p_93524_, int p_93525_, int p_93526_, int p_93527_, int p_93528_, int p_93529_, int p_93530_, boolean p_93531_, float p_93532_);

      public void renderBack(GuiGraphics p_282673_, int p_275556_, int p_275667_, int p_275713_, int p_275408_, int p_275330_, int p_275603_, int p_275450_, boolean p_275434_, float p_275384_) {
      }

      public boolean isMouseOver(double pMouseX, double pMouseY) {
         return Objects.equals(this.list.getEntryAtPosition(pMouseX, pMouseY), this);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class TrackedList extends AbstractList<E> {
      private final List<E> delegate = Lists.newArrayList();

      public E get(int pIndex) {
         return this.delegate.get(pIndex);
      }

      public int size() {
         return this.delegate.size();
      }

      public E set(int pIndex, E pEntry) {
         E e = this.delegate.set(pIndex, pEntry);
         AbstractSelectionList.this.bindEntryToSelf(pEntry);
         return e;
      }

      public void add(int pIndex, E pEntry) {
         this.delegate.add(pIndex, pEntry);
         AbstractSelectionList.this.bindEntryToSelf(pEntry);
      }

      public E remove(int pIndex) {
         return this.delegate.remove(pIndex);
      }
   }
}
