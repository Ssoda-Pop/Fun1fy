package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.crafting.Ingredient;

public interface ArmorMaterial {
   int getDurabilityForType(ArmorItem.Type p_266807_);

   int getDefenseForType(ArmorItem.Type p_267168_);

   int getEnchantmentValue();

   SoundEvent getEquipSound();

   Ingredient getRepairIngredient();

   String getName();

   float getToughness();

   /**
    * Gets the percentage of knockback resistance provided by armor of the material.
    */
   float getKnockbackResistance();
}