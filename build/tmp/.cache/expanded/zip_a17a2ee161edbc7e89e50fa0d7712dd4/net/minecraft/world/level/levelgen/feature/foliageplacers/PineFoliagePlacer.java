package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class PineFoliagePlacer extends FoliagePlacer {
   public static final Codec<PineFoliagePlacer> CODEC = RecordCodecBuilder.create((p_68698_) -> {
      return foliagePlacerParts(p_68698_).and(IntProvider.codec(0, 24).fieldOf("height").forGetter((p_161500_) -> {
         return p_161500_.height;
      })).apply(p_68698_, PineFoliagePlacer::new);
   });
   private final IntProvider height;

   public PineFoliagePlacer(IntProvider p_161486_, IntProvider p_161487_, IntProvider p_161488_) {
      super(p_161486_, p_161487_);
      this.height = p_161488_;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.PINE_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader p_225702_, FoliagePlacer.FoliageSetter p_272791_, RandomSource p_225704_, TreeConfiguration p_225705_, int p_225706_, FoliagePlacer.FoliageAttachment p_225707_, int p_225708_, int p_225709_, int p_225710_) {
      int i = 0;

      for(int j = p_225710_; j >= p_225710_ - p_225708_; --j) {
         this.placeLeavesRow(p_225702_, p_272791_, p_225704_, p_225705_, p_225707_.pos(), i, j, p_225707_.doubleTrunk());
         if (i >= 1 && j == p_225710_ - p_225708_ + 1) {
            --i;
         } else if (i < p_225709_ + p_225707_.radiusOffset()) {
            ++i;
         }
      }

   }

   public int foliageRadius(RandomSource pRandom, int pRadius) {
      return super.foliageRadius(pRandom, pRadius) + pRandom.nextInt(Math.max(pRadius + 1, 1));
   }

   public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
      return this.height.sample(pRandom);
   }

   /**
    * Skips certain positions based on the provided shape, such as rounding corners randomly.
    * The coordinates are passed in as absolute value, and should be within [0, {@code range}].
    */
   protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
      return pLocalX == pRange && pLocalZ == pRange && pRange > 0;
   }
}