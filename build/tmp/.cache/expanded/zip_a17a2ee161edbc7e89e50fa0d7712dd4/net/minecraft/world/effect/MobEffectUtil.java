package net.minecraft.world.effect;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class MobEffectUtil {
   public static Component formatDuration(MobEffectInstance p_268116_, float p_268280_) {
      if (p_268116_.isInfiniteDuration()) {
         return Component.translatable("effect.duration.infinite");
      } else {
         int i = Mth.floor((float)p_268116_.getDuration() * p_268280_);
         return Component.literal(StringUtil.formatTickDuration(i));
      }
   }

   public static boolean hasDigSpeed(LivingEntity pEntity) {
      return pEntity.hasEffect(MobEffects.DIG_SPEED) || pEntity.hasEffect(MobEffects.CONDUIT_POWER);
   }

   public static int getDigSpeedAmplification(LivingEntity pEntity) {
      int i = 0;
      int j = 0;
      if (pEntity.hasEffect(MobEffects.DIG_SPEED)) {
         i = pEntity.getEffect(MobEffects.DIG_SPEED).getAmplifier();
      }

      if (pEntity.hasEffect(MobEffects.CONDUIT_POWER)) {
         j = pEntity.getEffect(MobEffects.CONDUIT_POWER).getAmplifier();
      }

      return Math.max(i, j);
   }

   public static boolean hasWaterBreathing(LivingEntity pEntity) {
      return pEntity.hasEffect(MobEffects.WATER_BREATHING) || pEntity.hasEffect(MobEffects.CONDUIT_POWER);
   }

   public static List<ServerPlayer> addEffectToPlayersAround(ServerLevel pLevel, @Nullable Entity p_216948_, Vec3 p_216949_, double p_216950_, MobEffectInstance p_216951_, int p_216952_) {
      MobEffect mobeffect = p_216951_.getEffect();
      List<ServerPlayer> list = pLevel.getPlayers((p_267925_) -> {
         return p_267925_.gameMode.isSurvival() && (p_216948_ == null || !p_216948_.isAlliedTo(p_267925_)) && p_216949_.closerThan(p_267925_.position(), p_216950_) && (!p_267925_.hasEffect(mobeffect) || p_267925_.getEffect(mobeffect).getAmplifier() < p_216951_.getAmplifier() || p_267925_.getEffect(mobeffect).endsWithin(p_216952_ - 1));
      });
      list.forEach((p_238232_) -> {
         p_238232_.addEffect(new MobEffectInstance(p_216951_), p_216948_);
      });
      return list;
   }
}