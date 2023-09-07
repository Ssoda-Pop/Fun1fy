package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.Fluids;

public abstract class FoliagePlacer {
   public static final Codec<FoliagePlacer> CODEC = BuiltInRegistries.FOLIAGE_PLACER_TYPE.byNameCodec().dispatch(FoliagePlacer::type, FoliagePlacerType::codec);
   protected final IntProvider radius;
   protected final IntProvider offset;

   protected static <P extends FoliagePlacer> Products.P2<RecordCodecBuilder.Mu<P>, IntProvider, IntProvider> foliagePlacerParts(RecordCodecBuilder.Instance<P> pInstance) {
      return pInstance.group(IntProvider.codec(0, 16).fieldOf("radius").forGetter((p_161449_) -> {
         return p_161449_.radius;
      }), IntProvider.codec(0, 16).fieldOf("offset").forGetter((p_161447_) -> {
         return p_161447_.offset;
      }));
   }

   public FoliagePlacer(IntProvider pRadius, IntProvider pOffset) {
      this.radius = pRadius;
      this.offset = pOffset;
   }

   protected abstract FoliagePlacerType<?> type();

   public void createFoliage(LevelSimulatedReader p_273526_, FoliagePlacer.FoliageSetter p_273018_, RandomSource p_273425_, TreeConfiguration p_273138_, int p_273282_, FoliagePlacer.FoliageAttachment p_272944_, int p_272930_, int p_272727_) {
      this.createFoliage(p_273526_, p_273018_, p_273425_, p_273138_, p_273282_, p_272944_, p_272930_, p_272727_, this.offset(p_273425_));
   }

   protected abstract void createFoliage(LevelSimulatedReader p_225613_, FoliagePlacer.FoliageSetter p_273598_, RandomSource p_225615_, TreeConfiguration p_225616_, int p_225617_, FoliagePlacer.FoliageAttachment p_225618_, int p_225619_, int p_225620_, int p_225621_);

   public abstract int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig);

   public int foliageRadius(RandomSource pRandom, int pRadius) {
      return this.radius.sample(pRandom);
   }

   private int offset(RandomSource pRandom) {
      return this.offset.sample(pRandom);
   }

   /**
    * Skips certain positions based on the provided shape, such as rounding corners randomly.
    * The coordinates are passed in as absolute value, and should be within [0, {@code range}].
    */
   protected abstract boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge);

   protected boolean shouldSkipLocationSigned(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
      int i;
      int j;
      if (pLarge) {
         i = Math.min(Math.abs(pLocalX), Math.abs(pLocalX - 1));
         j = Math.min(Math.abs(pLocalZ), Math.abs(pLocalZ - 1));
      } else {
         i = Math.abs(pLocalX);
         j = Math.abs(pLocalZ);
      }

      return this.shouldSkipLocation(pRandom, i, pLocalY, j, pRange, pLarge);
   }

   protected void placeLeavesRow(LevelSimulatedReader p_225629_, FoliagePlacer.FoliageSetter p_272772_, RandomSource p_225631_, TreeConfiguration p_225632_, BlockPos p_225633_, int p_225634_, int p_225635_, boolean p_225636_) {
      int i = p_225636_ ? 1 : 0;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int j = -p_225634_; j <= p_225634_ + i; ++j) {
         for(int k = -p_225634_; k <= p_225634_ + i; ++k) {
            if (!this.shouldSkipLocationSigned(p_225631_, j, p_225635_, k, p_225634_, p_225636_)) {
               blockpos$mutableblockpos.setWithOffset(p_225633_, j, p_225635_, k);
               tryPlaceLeaf(p_225629_, p_272772_, p_225631_, p_225632_, blockpos$mutableblockpos);
            }
         }
      }

   }

   protected final void placeLeavesRowWithHangingLeavesBelow(LevelSimulatedReader p_273087_, FoliagePlacer.FoliageSetter p_273225_, RandomSource p_272629_, TreeConfiguration p_272885_, BlockPos p_273412_, int p_272712_, int p_272656_, boolean p_272689_, float p_273464_, float p_273068_) {
      this.placeLeavesRow(p_273087_, p_273225_, p_272629_, p_272885_, p_273412_, p_272712_, p_272656_, p_272689_);
      int i = p_272689_ ? 1 : 0;
      BlockPos blockpos = p_273412_.below();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         Direction direction1 = direction.getClockWise();
         int j = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? p_272712_ + i : p_272712_;
         blockpos$mutableblockpos.setWithOffset(p_273412_, 0, p_272656_ - 1, 0).move(direction1, j).move(direction, -p_272712_);
         int k = -p_272712_;

         while(k < p_272712_ + i) {
            boolean flag = p_273225_.isSet(blockpos$mutableblockpos.move(Direction.UP));
            blockpos$mutableblockpos.move(Direction.DOWN);
            if (flag && tryPlaceExtension(p_273087_, p_273225_, p_272629_, p_272885_, p_273464_, blockpos, blockpos$mutableblockpos)) {
               blockpos$mutableblockpos.move(Direction.DOWN);
               tryPlaceExtension(p_273087_, p_273225_, p_272629_, p_272885_, p_273068_, blockpos, blockpos$mutableblockpos);
               blockpos$mutableblockpos.move(Direction.UP);
            }

            ++k;
            blockpos$mutableblockpos.move(direction);
         }
      }

   }

   private static boolean tryPlaceExtension(LevelSimulatedReader p_277577_, FoliagePlacer.FoliageSetter p_277449_, RandomSource p_277966_, TreeConfiguration p_277897_, float p_277979_, BlockPos p_277833_, BlockPos.MutableBlockPos p_277567_) {
      if (p_277567_.distManhattan(p_277833_) >= 7) {
         return false;
      } else {
         return p_277966_.nextFloat() > p_277979_ ? false : tryPlaceLeaf(p_277577_, p_277449_, p_277966_, p_277897_, p_277567_);
      }
   }

   protected static boolean tryPlaceLeaf(LevelSimulatedReader p_273596_, FoliagePlacer.FoliageSetter p_273054_, RandomSource p_272977_, TreeConfiguration p_273040_, BlockPos p_273406_) {
      if (!TreeFeature.validTreePos(p_273596_, p_273406_)) {
         return false;
      } else {
         BlockState blockstate = p_273040_.foliageProvider.getState(p_272977_, p_273406_);
         if (blockstate.hasProperty(BlockStateProperties.WATERLOGGED)) {
            blockstate = blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(p_273596_.isFluidAtPosition(p_273406_, (p_225638_) -> {
               return p_225638_.isSourceOfType(Fluids.WATER);
            })));
         }

         p_273054_.set(p_273406_, blockstate);
         return true;
      }
   }

   public static final class FoliageAttachment {
      private final BlockPos pos;
      private final int radiusOffset;
      private final boolean doubleTrunk;

      public FoliageAttachment(BlockPos pPos, int pRadiusOffset, boolean pDoubleTrunk) {
         this.pos = pPos;
         this.radiusOffset = pRadiusOffset;
         this.doubleTrunk = pDoubleTrunk;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public int radiusOffset() {
         return this.radiusOffset;
      }

      public boolean doubleTrunk() {
         return this.doubleTrunk;
      }
   }

   public interface FoliageSetter {
      void set(BlockPos p_273742_, BlockState p_273780_);

      boolean isSet(BlockPos p_273118_);
   }
}