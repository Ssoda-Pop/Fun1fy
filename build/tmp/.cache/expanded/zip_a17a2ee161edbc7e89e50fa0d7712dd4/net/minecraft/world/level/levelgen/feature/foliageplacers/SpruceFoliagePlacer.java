package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class SpruceFoliagePlacer extends FoliagePlacer {
   public static final Codec<SpruceFoliagePlacer> CODEC = RecordCodecBuilder.create((p_68735_) -> {
      return foliagePlacerParts(p_68735_).and(IntProvider.codec(0, 24).fieldOf("trunk_height").forGetter((p_161553_) -> {
         return p_161553_.trunkHeight;
      })).apply(p_68735_, SpruceFoliagePlacer::new);
   });
   private final IntProvider trunkHeight;

   public SpruceFoliagePlacer(IntProvider p_161539_, IntProvider p_161540_, IntProvider p_161541_) {
      super(p_161539_, p_161540_);
      this.trunkHeight = p_161541_;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.SPRUCE_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader p_225744_, FoliagePlacer.FoliageSetter p_273256_, RandomSource p_225746_, TreeConfiguration p_225747_, int p_225748_, FoliagePlacer.FoliageAttachment p_225749_, int p_225750_, int p_225751_, int p_225752_) {
      BlockPos blockpos = p_225749_.pos();
      int i = p_225746_.nextInt(2);
      int j = 1;
      int k = 0;

      for(int l = p_225752_; l >= -p_225750_; --l) {
         this.placeLeavesRow(p_225744_, p_273256_, p_225746_, p_225747_, blockpos, i, l, p_225749_.doubleTrunk());
         if (i >= j) {
            i = k;
            k = 1;
            j = Math.min(j + 1, p_225751_ + p_225749_.radiusOffset());
         } else {
            ++i;
         }
      }

   }

   public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
      return Math.max(4, pHeight - this.trunkHeight.sample(pRandom));
   }

   /**
    * Skips certain positions based on the provided shape, such as rounding corners randomly.
    * The coordinates are passed in as absolute value, and should be within [0, {@code range}].
    */
   protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
      return pLocalX == pRange && pLocalZ == pRange && pRange > 0;
   }
}