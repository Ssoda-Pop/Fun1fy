package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class AbstractCookingRecipe implements Recipe<Container> {
   protected final RecipeType<?> type;
   protected final ResourceLocation id;
   private final CookingBookCategory category;
   protected final String group;
   protected final Ingredient ingredient;
   protected final ItemStack result;
   protected final float experience;
   protected final int cookingTime;

   public AbstractCookingRecipe(RecipeType<?> p_250197_, ResourceLocation p_249379_, String p_249518_, CookingBookCategory p_250891_, Ingredient p_251354_, ItemStack p_252185_, float p_252165_, int p_250256_) {
      this.type = p_250197_;
      this.category = p_250891_;
      this.id = p_249379_;
      this.group = p_249518_;
      this.ingredient = p_251354_;
      this.result = p_252185_;
      this.experience = p_252165_;
      this.cookingTime = p_250256_;
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(Container pInv, Level pLevel) {
      return this.ingredient.test(pInv.getItem(0));
   }

   public ItemStack assemble(Container p_43746_, RegistryAccess p_267063_) {
      return this.result.copy();
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canCraftInDimensions(int pWidth, int pHeight) {
      return true;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> nonnulllist = NonNullList.create();
      nonnulllist.add(this.ingredient);
      return nonnulllist;
   }

   /**
    * Gets the experience of this recipe
    */
   public float getExperience() {
      return this.experience;
   }

   public ItemStack getResultItem(RegistryAccess p_266851_) {
      return this.result;
   }

   /**
    * Recipes with equal group are combined into one button in the recipe book
    */
   public String getGroup() {
      return this.group;
   }

   /**
    * Gets the cook time in ticks
    */
   public int getCookingTime() {
      return this.cookingTime;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public RecipeType<?> getType() {
      return this.type;
   }

   public CookingBookCategory category() {
      return this.category;
   }
}