package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class BlobFoliagePlacer extends FoliagePlacer {
   public static final Codec<BlobFoliagePlacer> CODEC = RecordCodecBuilder.create((p_68427_) -> {
      return blobParts(p_68427_).apply(p_68427_, BlobFoliagePlacer::new);
   });
   protected final int height;

   protected static <P extends BlobFoliagePlacer> Products.P3<RecordCodecBuilder.Mu<P>, IntProvider, IntProvider, Integer> blobParts(RecordCodecBuilder.Instance<P> p_68414_) {
      return foliagePlacerParts(p_68414_).and(Codec.intRange(0, 16).fieldOf("height").forGetter((p_68412_) -> {
         return p_68412_.height;
      }));
   }

   public BlobFoliagePlacer(IntProvider p_161356_, IntProvider p_161357_, int p_161358_) {
      super(p_161356_, p_161357_);
      this.height = p_161358_;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.BLOB_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader p_273066_, FoliagePlacer.FoliageSetter p_272716_, RandomSource p_273178_, TreeConfiguration p_272850_, int p_273067_, FoliagePlacer.FoliageAttachment p_273711_, int p_273580_, int p_273511_, int p_273685_) {
      for(int i = p_273685_; i >= p_273685_ - p_273580_; --i) {
         int j = Math.max(p_273511_ + p_273711_.radiusOffset() - 1 - i / 2, 0);
         this.placeLeavesRow(p_273066_, p_272716_, p_273178_, p_272850_, p_273711_.pos(), j, i, p_273711_.doubleTrunk());
      }

   }

   public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
      return this.height;
   }

   /**
    * Skips certain positions based on the provided shape, such as rounding corners randomly.
    * The coordinates are passed in as absolute value, and should be within [0, {@code range}].
    */
   protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
      return pLocalX == pRange && pLocalZ == pRange && (pRandom.nextInt(2) == 0 || pLocalY == 0);
   }
}