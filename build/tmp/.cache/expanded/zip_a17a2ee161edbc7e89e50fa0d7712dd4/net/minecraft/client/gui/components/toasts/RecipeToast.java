package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeToast implements Toast {
   private static final long DISPLAY_TIME = 5000L;
   private static final Component TITLE_TEXT = Component.translatable("recipe.toast.title");
   private static final Component DESCRIPTION_TEXT = Component.translatable("recipe.toast.description");
   private final List<Recipe<?>> recipes = Lists.newArrayList();
   private long lastChanged;
   private boolean changed;

   public RecipeToast(Recipe<?> pRecipe) {
      this.recipes.add(pRecipe);
   }

   public Toast.Visibility render(GuiGraphics p_281667_, ToastComponent p_281321_, long p_281779_) {
      if (this.changed) {
         this.lastChanged = p_281779_;
         this.changed = false;
      }

      if (this.recipes.isEmpty()) {
         return Toast.Visibility.HIDE;
      } else {
         p_281667_.blit(TEXTURE, 0, 0, 0, 32, this.width(), this.height());
         p_281667_.drawString(p_281321_.getMinecraft().font, TITLE_TEXT, 30, 7, -11534256, false);
         p_281667_.drawString(p_281321_.getMinecraft().font, DESCRIPTION_TEXT, 30, 18, -16777216, false);
         Recipe<?> recipe = this.recipes.get((int)((double)p_281779_ / Math.max(1.0D, 5000.0D * p_281321_.getNotificationDisplayTimeMultiplier() / (double)this.recipes.size()) % (double)this.recipes.size()));
         ItemStack itemstack = recipe.getToastSymbol();
         p_281667_.pose().pushPose();
         p_281667_.pose().scale(0.6F, 0.6F, 1.0F);
         p_281667_.renderFakeItem(itemstack, 3, 3);
         p_281667_.pose().popPose();
         p_281667_.renderFakeItem(recipe.getResultItem(p_281321_.getMinecraft().level.registryAccess()), 8, 8);
         return (double)(p_281779_ - this.lastChanged) >= 5000.0D * p_281321_.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
      }
   }

   private void addItem(Recipe<?> pRecipe) {
      this.recipes.add(pRecipe);
      this.changed = true;
   }

   public static void addOrUpdate(ToastComponent pToastGui, Recipe<?> pRecipe) {
      RecipeToast recipetoast = pToastGui.getToast(RecipeToast.class, NO_TOKEN);
      if (recipetoast == null) {
         pToastGui.addToast(new RecipeToast(pRecipe));
      } else {
         recipetoast.addItem(pRecipe);
      }

   }
}