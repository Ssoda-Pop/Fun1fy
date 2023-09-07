package net.minecraft.client.gui.components.events;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2i;

@OnlyIn(Dist.CLIENT)
public interface ContainerEventHandler extends GuiEventListener {
   List<? extends GuiEventListener> children();

   /**
    * Returns the first event listener that intersects with the mouse coordinates.
    */
   default Optional<GuiEventListener> getChildAt(double pMouseX, double pMouseY) {
      for(GuiEventListener guieventlistener : this.children()) {
         if (guieventlistener.isMouseOver(pMouseX, pMouseY)) {
            return Optional.of(guieventlistener);
         }
      }

      return Optional.empty();
   }

   default boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      for(GuiEventListener guieventlistener : this.children()) {
         if (guieventlistener.mouseClicked(pMouseX, pMouseY, pButton)) {
            this.setFocused(guieventlistener);
            if (pButton == 0) {
               this.setDragging(true);
            }

            return true;
         }
      }

      return false;
   }

   default boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      this.setDragging(false);
      return this.getChildAt(pMouseX, pMouseY).filter((p_94708_) -> {
         return p_94708_.mouseReleased(pMouseX, pMouseY, pButton);
      }).isPresent();
   }

   default boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      return this.getFocused() != null && this.isDragging() && pButton == 0 ? this.getFocused().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY) : false;
   }

   boolean isDragging();

   void setDragging(boolean pIsDragging);

   default boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
      return this.getChildAt(pMouseX, pMouseY).filter((p_94693_) -> {
         return p_94693_.mouseScrolled(pMouseX, pMouseY, pDelta);
      }).isPresent();
   }

   default boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      return this.getFocused() != null && this.getFocused().keyPressed(pKeyCode, pScanCode, pModifiers);
   }

   default boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
      return this.getFocused() != null && this.getFocused().keyReleased(pKeyCode, pScanCode, pModifiers);
   }

   default boolean charTyped(char pCodePoint, int pModifiers) {
      return this.getFocused() != null && this.getFocused().charTyped(pCodePoint, pModifiers);
   }

   @Nullable
   GuiEventListener getFocused();

   void setFocused(@Nullable GuiEventListener pFocused);

   default void setFocused(boolean p_265504_) {
   }

   default boolean isFocused() {
      return this.getFocused() != null;
   }

   @Nullable
   default ComponentPath getCurrentFocusPath() {
      GuiEventListener guieventlistener = this.getFocused();
      return guieventlistener != null ? ComponentPath.path(this, guieventlistener.getCurrentFocusPath()) : null;
   }

   default void magicalSpecialHackyFocus(@Nullable GuiEventListener pEventListener) {
      this.setFocused(pEventListener);
   }

   @Nullable
   default ComponentPath nextFocusPath(FocusNavigationEvent p_265668_) {
      GuiEventListener guieventlistener = this.getFocused();
      if (guieventlistener != null) {
         ComponentPath componentpath = guieventlistener.nextFocusPath(p_265668_);
         if (componentpath != null) {
            return ComponentPath.path(this, componentpath);
         }
      }

      if (p_265668_ instanceof FocusNavigationEvent.TabNavigation focusnavigationevent$tabnavigation) {
         return this.handleTabNavigation(focusnavigationevent$tabnavigation);
      } else if (p_265668_ instanceof FocusNavigationEvent.ArrowNavigation focusnavigationevent$arrownavigation) {
         return this.handleArrowNavigation(focusnavigationevent$arrownavigation);
      } else {
         return null;
      }
   }

   @Nullable
   private ComponentPath handleTabNavigation(FocusNavigationEvent.TabNavigation p_265354_) {
      boolean flag = p_265354_.forward();
      GuiEventListener guieventlistener = this.getFocused();
      List<? extends GuiEventListener> list = new ArrayList<>(this.children());
      Collections.sort(list, Comparator.comparingInt((p_289623_) -> {
         return p_289623_.getTabOrderGroup();
      }));
      int j = list.indexOf(guieventlistener);
      int i;
      if (guieventlistener != null && j >= 0) {
         i = j + (flag ? 1 : 0);
      } else if (flag) {
         i = 0;
      } else {
         i = list.size();
      }

      ListIterator<? extends GuiEventListener> listiterator = list.listIterator(i);
      BooleanSupplier booleansupplier = flag ? listiterator::hasNext : listiterator::hasPrevious;
      Supplier<? extends GuiEventListener> supplier = flag ? listiterator::next : listiterator::previous;

      while(booleansupplier.getAsBoolean()) {
         GuiEventListener guieventlistener1 = supplier.get();
         ComponentPath componentpath = guieventlistener1.nextFocusPath(p_265354_);
         if (componentpath != null) {
            return ComponentPath.path(this, componentpath);
         }
      }

      return null;
   }

   @Nullable
   private ComponentPath handleArrowNavigation(FocusNavigationEvent.ArrowNavigation p_265760_) {
      GuiEventListener guieventlistener = this.getFocused();
      if (guieventlistener == null) {
         ScreenDirection screendirection = p_265760_.direction();
         ScreenRectangle screenrectangle1 = this.getRectangle().getBorder(screendirection.getOpposite());
         return ComponentPath.path(this, this.nextFocusPathInDirection(screenrectangle1, screendirection, (GuiEventListener)null, p_265760_));
      } else {
         ScreenRectangle screenrectangle = guieventlistener.getRectangle();
         return ComponentPath.path(this, this.nextFocusPathInDirection(screenrectangle, p_265760_.direction(), guieventlistener, p_265760_));
      }
   }

   @Nullable
   private ComponentPath nextFocusPathInDirection(ScreenRectangle p_265054_, ScreenDirection p_265167_, @Nullable GuiEventListener p_265476_, FocusNavigationEvent p_265762_) {
      ScreenAxis screenaxis = p_265167_.getAxis();
      ScreenAxis screenaxis1 = screenaxis.orthogonal();
      ScreenDirection screendirection = screenaxis1.getPositive();
      int i = p_265054_.getBoundInDirection(p_265167_.getOpposite());
      List<GuiEventListener> list = new ArrayList<>();

      for(GuiEventListener guieventlistener : this.children()) {
         if (guieventlistener != p_265476_) {
            ScreenRectangle screenrectangle = guieventlistener.getRectangle();
            if (screenrectangle.overlapsInAxis(p_265054_, screenaxis1)) {
               int j = screenrectangle.getBoundInDirection(p_265167_.getOpposite());
               if (p_265167_.isAfter(j, i)) {
                  list.add(guieventlistener);
               } else if (j == i && p_265167_.isAfter(screenrectangle.getBoundInDirection(p_265167_), p_265054_.getBoundInDirection(p_265167_))) {
                  list.add(guieventlistener);
               }
            }
         }
      }

      Comparator<GuiEventListener> comparator = Comparator.comparing((p_264674_) -> {
         return p_264674_.getRectangle().getBoundInDirection(p_265167_.getOpposite());
      }, p_265167_.coordinateValueComparator());
      Comparator<GuiEventListener> comparator1 = Comparator.comparing((p_264676_) -> {
         return p_264676_.getRectangle().getBoundInDirection(screendirection.getOpposite());
      }, screendirection.coordinateValueComparator());
      list.sort(comparator.thenComparing(comparator1));

      for(GuiEventListener guieventlistener1 : list) {
         ComponentPath componentpath = guieventlistener1.nextFocusPath(p_265762_);
         if (componentpath != null) {
            return componentpath;
         }
      }

      return this.nextFocusPathVaguelyInDirection(p_265054_, p_265167_, p_265476_, p_265762_);
   }

   @Nullable
   private ComponentPath nextFocusPathVaguelyInDirection(ScreenRectangle p_265390_, ScreenDirection p_265687_, @Nullable GuiEventListener p_265498_, FocusNavigationEvent p_265048_) {
      ScreenAxis screenaxis = p_265687_.getAxis();
      ScreenAxis screenaxis1 = screenaxis.orthogonal();
      List<Pair<GuiEventListener, Long>> list = new ArrayList<>();
      ScreenPosition screenposition = ScreenPosition.of(screenaxis, p_265390_.getBoundInDirection(p_265687_), p_265390_.getCenterInAxis(screenaxis1));

      for(GuiEventListener guieventlistener : this.children()) {
         if (guieventlistener != p_265498_) {
            ScreenRectangle screenrectangle = guieventlistener.getRectangle();
            ScreenPosition screenposition1 = ScreenPosition.of(screenaxis, screenrectangle.getBoundInDirection(p_265687_.getOpposite()), screenrectangle.getCenterInAxis(screenaxis1));
            if (p_265687_.isAfter(screenposition1.getCoordinate(screenaxis), screenposition.getCoordinate(screenaxis))) {
               long i = Vector2i.distanceSquared(screenposition.x(), screenposition.y(), screenposition1.x(), screenposition1.y());
               list.add(Pair.of(guieventlistener, i));
            }
         }
      }

      list.sort(Comparator.comparingDouble(Pair::getSecond));

      for(Pair<GuiEventListener, Long> pair : list) {
         ComponentPath componentpath = pair.getFirst().nextFocusPath(p_265048_);
         if (componentpath != null) {
            return componentpath;
         }
      }

      return null;
   }
}