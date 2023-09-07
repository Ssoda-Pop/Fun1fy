package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ParticleProvider<T extends ParticleOptions> {
   @Nullable
   Particle createParticle(T pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed);

   @OnlyIn(Dist.CLIENT)
   public interface Sprite<T extends ParticleOptions> {
      @Nullable
      TextureSheetParticle createParticle(T p_273550_, ClientLevel p_273071_, double p_273160_, double p_273576_, double p_272710_, double p_273652_, double p_273457_, double p_272840_);
   }
}