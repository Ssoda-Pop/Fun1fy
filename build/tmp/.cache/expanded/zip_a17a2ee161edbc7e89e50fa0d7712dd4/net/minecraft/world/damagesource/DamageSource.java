package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class DamageSource {
   private final Holder<DamageType> type;
   @Nullable
   private final Entity causingEntity;
   @Nullable
   private final Entity directEntity;
   @Nullable
   private final Vec3 damageSourcePosition;

   public String toString() {
      return "DamageSource (" + this.type().msgId() + ")";
   }

   /**
    * How much satiate(food) is consumed by this DamageSource
    */
   public float getFoodExhaustion() {
      return this.type().exhaustion();
   }

   public boolean isIndirect() {
      return this.causingEntity != this.directEntity;
   }

   public DamageSource(Holder<DamageType> p_270906_, @Nullable Entity p_270796_, @Nullable Entity p_270459_, @Nullable Vec3 p_270623_) {
      this.type = p_270906_;
      this.causingEntity = p_270459_;
      this.directEntity = p_270796_;
      this.damageSourcePosition = p_270623_;
   }

   public DamageSource(Holder<DamageType> p_270818_, @Nullable Entity p_270162_, @Nullable Entity p_270115_) {
      this(p_270818_, p_270162_, p_270115_, (Vec3)null);
   }

   public DamageSource(Holder<DamageType> p_270690_, Vec3 p_270579_) {
      this(p_270690_, (Entity)null, (Entity)null, p_270579_);
   }

   public DamageSource(Holder<DamageType> p_270811_, @Nullable Entity p_270660_) {
      this(p_270811_, p_270660_, p_270660_);
   }

   public DamageSource(Holder<DamageType> p_270475_) {
      this(p_270475_, (Entity)null, (Entity)null, (Vec3)null);
   }

   /**
    * Retrieves the immediate causer of the damage, e.g. the arrow entity, not its shooter
    */
   @Nullable
   public Entity getDirectEntity() {
      return this.directEntity;
   }

   /**
    * Retrieves the true causer of the damage, e.g. the player who fired an arrow, the shulker who fired the bullet,
    * etc.
    */
   @Nullable
   public Entity getEntity() {
      return this.causingEntity;
   }

   /**
    * Gets the death message that is displayed when the player dies
    */
   public Component getLocalizedDeathMessage(LivingEntity pLivingEntity) {
      String s = "death.attack." + this.type().msgId();
      if (this.causingEntity == null && this.directEntity == null) {
         LivingEntity livingentity1 = pLivingEntity.getKillCredit();
         String s1 = s + ".player";
         return livingentity1 != null ? Component.translatable(s1, pLivingEntity.getDisplayName(), livingentity1.getDisplayName()) : Component.translatable(s, pLivingEntity.getDisplayName());
      } else {
         Component component = this.causingEntity == null ? this.directEntity.getDisplayName() : this.causingEntity.getDisplayName();
         Entity entity = this.causingEntity;
         ItemStack itemstack1;
         if (entity instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity;
            itemstack1 = livingentity.getMainHandItem();
         } else {
            itemstack1 = ItemStack.EMPTY;
         }

         ItemStack itemstack = itemstack1;
         return !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? Component.translatable(s + ".item", pLivingEntity.getDisplayName(), component, itemstack.getDisplayName()) : Component.translatable(s, pLivingEntity.getDisplayName(), component);
      }
   }

   /**
    * Return the name of damage type.
    */
   public String getMsgId() {
      return this.type().msgId();
   }

   /**
    * Return whether this damage source will have its damage amount scaled based on the current difficulty.
    */
   public boolean scalesWithDifficulty() {
      boolean flag;
      switch (this.type().scaling()) {
         case NEVER:
            flag = false;
            break;
         case WHEN_CAUSED_BY_LIVING_NON_PLAYER:
            flag = this.causingEntity instanceof LivingEntity && !(this.causingEntity instanceof Player);
            break;
         case ALWAYS:
            flag = true;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return flag;
   }

   public boolean isCreativePlayer() {
      Entity entity = this.getEntity();
      if (entity instanceof Player player) {
         if (player.getAbilities().instabuild) {
            return true;
         }
      }

      return false;
   }

   /**
    * Gets the location from which the damage originates.
    */
   @Nullable
   public Vec3 getSourcePosition() {
      if (this.damageSourcePosition != null) {
         return this.damageSourcePosition;
      } else {
         return this.directEntity != null ? this.directEntity.position() : null;
      }
   }

   @Nullable
   public Vec3 sourcePositionRaw() {
      return this.damageSourcePosition;
   }

   public boolean is(TagKey<DamageType> p_270890_) {
      return this.type.is(p_270890_);
   }

   public boolean is(ResourceKey<DamageType> p_276108_) {
      return this.type.is(p_276108_);
   }

   public DamageType type() {
      return this.type.value();
   }

   public Holder<DamageType> typeHolder() {
      return this.type;
   }
}