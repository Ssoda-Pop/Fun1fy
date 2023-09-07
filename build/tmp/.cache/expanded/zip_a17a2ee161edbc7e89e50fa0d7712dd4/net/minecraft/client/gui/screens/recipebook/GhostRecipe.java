package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhostRecipe {
   @Nullable
   private Recipe<?> recipe;
   private final List<GhostRecipe.GhostIngredient> ingredients = Lists.newArrayList();
   float time;

   public void clear() {
      this.recipe = null;
      this.ingredients.clear();
      this.time = 0.0F;
   }

   public void addIngredient(Ingredient pIngredient, int pX, int pY) {
      this.ingredients.add(new GhostRecipe.GhostIngredient(pIngredient, pX, pY));
   }

   public GhostRecipe.GhostIngredient get(int pIndex) {
      return this.ingredients.get(pIndex);
   }

   public int size() {
      return this.ingredients.size();
   }

   @Nullable
   public Recipe<?> getRecipe() {
      return this.recipe;
   }

   public void setRecipe(Recipe<?> pRecipe) {
      this.recipe = pRecipe;
   }

   public void render(GuiGraphics p_282081_, Minecraft p_281341_, int p_283169_, int p_282326_, boolean p_282174_, float p_282256_) {
      if (!Screen.hasControlDown()) {
         this.time += p_282256_;
      }

      for(int i = 0; i < this.ingredients.size(); ++i) {
         GhostRecipe.GhostIngredient ghostrecipe$ghostingredient = this.ingredients.get(i);
         int j = ghostrecipe$ghostingredient.getX() + p_283169_;
         int k = ghostrecipe$ghostingredient.getY() + p_282326_;
         if (i == 0 && p_282174_) {
            p_282081_.fill(j - 4, k - 4, j + 20, k + 20, 822018048);
         } else {
            p_282081_.fill(j, k, j + 16, k + 16, 822018048);
         }

         ItemStack itemstack = ghostrecipe$ghostingredient.getItem();
         p_282081_.renderFakeItem(itemstack, j, k);
         p_282081_.fill(RenderType.guiGhostRecipeOverlay(), j, k, j + 16, k + 16, 822083583);
         if (i == 0) {
            p_282081_.renderItemDecorations(p_281341_.font, itemstack, j, k);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public class GhostIngredient {
      private final Ingredient ingredient;
      private final int x;
      private final int y;

      public GhostIngredient(Ingredient pIngredient, int pX, int pY) {
         this.ingredient = pIngredient;
         this.x = pX;
         this.y = pY;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public ItemStack getItem() {
         ItemStack[] aitemstack = this.ingredient.getItems();
         return aitemstack.length == 0 ? ItemStack.EMPTY : aitemstack[Mth.floor(GhostRecipe.this.time / 30.0F) % aitemstack.length];
      }
   }
}