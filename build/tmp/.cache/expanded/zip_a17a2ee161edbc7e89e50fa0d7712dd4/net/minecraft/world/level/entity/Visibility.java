package net.minecraft.world.level.entity;

import net.minecraft.server.level.FullChunkStatus;

public enum Visibility {
   HIDDEN(false, false),
   TRACKED(true, false),
   TICKING(true, true);

   private final boolean accessible;
   private final boolean ticking;

   private Visibility(boolean pAccessible, boolean pTicking) {
      this.accessible = pAccessible;
      this.ticking = pTicking;
   }

   public boolean isTicking() {
      return this.ticking;
   }

   public boolean isAccessible() {
      return this.accessible;
   }

   public static Visibility fromFullChunkStatus(FullChunkStatus p_287651_) {
      if (p_287651_.isOrAfter(FullChunkStatus.ENTITY_TICKING)) {
         return TICKING;
      } else {
         return p_287651_.isOrAfter(FullChunkStatus.FULL) ? TRACKED : HIDDEN;
      }
   }
}