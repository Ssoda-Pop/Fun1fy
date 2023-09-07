package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public abstract class AbstractMegaTreeGrower extends AbstractTreeGrower {
   public boolean growTree(ServerLevel pLevle, ChunkGenerator pGenerator, BlockPos pPos, BlockState pState, RandomSource pRandom) {
      for(int i = 0; i >= -1; --i) {
         for(int j = 0; j >= -1; --j) {
            if (isTwoByTwoSapling(pState, pLevle, pPos, i, j)) {
               return this.placeMega(pLevle, pGenerator, pPos, pState, pRandom, i, j);
            }
         }
      }

      return super.growTree(pLevle, pGenerator, pPos, pState, pRandom);
   }

   @Nullable
   protected abstract ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource p_222904_);

   public boolean placeMega(ServerLevel pLevle, ChunkGenerator pGenerator, BlockPos pPos, BlockState pState, RandomSource pRandom, int pBranchX, int pBranchY) {
      ResourceKey<ConfiguredFeature<?, ?>> resourcekey = this.getConfiguredMegaFeature(pRandom);
      if (resourcekey == null) {
         return false;
      } else {
         Holder<ConfiguredFeature<?, ?>> holder = pLevle.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(resourcekey).orElse((Holder.Reference<ConfiguredFeature<?, ?>>)null);
         var event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(pLevle, pRandom, pPos, holder);
         holder = event.getFeature();
         if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return false;
         if (holder == null) {
            return false;
         } else {
            ConfiguredFeature<?, ?> configuredfeature = holder.value();
            BlockState blockstate = Blocks.AIR.defaultBlockState();
            pLevle.setBlock(pPos.offset(pBranchX, 0, pBranchY), blockstate, 4);
            pLevle.setBlock(pPos.offset(pBranchX + 1, 0, pBranchY), blockstate, 4);
            pLevle.setBlock(pPos.offset(pBranchX, 0, pBranchY + 1), blockstate, 4);
            pLevle.setBlock(pPos.offset(pBranchX + 1, 0, pBranchY + 1), blockstate, 4);
            if (configuredfeature.place(pLevle, pGenerator, pRandom, pPos.offset(pBranchX, 0, pBranchY))) {
               return true;
            } else {
               pLevle.setBlock(pPos.offset(pBranchX, 0, pBranchY), pState, 4);
               pLevle.setBlock(pPos.offset(pBranchX + 1, 0, pBranchY), pState, 4);
               pLevle.setBlock(pPos.offset(pBranchX, 0, pBranchY + 1), pState, 4);
               pLevle.setBlock(pPos.offset(pBranchX + 1, 0, pBranchY + 1), pState, 4);
               return false;
            }
         }
      }
   }

   public static boolean isTwoByTwoSapling(BlockState pBlockUnder, BlockGetter pLevel, BlockPos pPos, int pXOffset, int pZOffset) {
      Block block = pBlockUnder.getBlock();
      return pLevel.getBlockState(pPos.offset(pXOffset, 0, pZOffset)).is(block) && pLevel.getBlockState(pPos.offset(pXOffset + 1, 0, pZOffset)).is(block) && pLevel.getBlockState(pPos.offset(pXOffset, 0, pZOffset + 1)).is(block) && pLevel.getBlockState(pPos.offset(pXOffset + 1, 0, pZOffset + 1)).is(block);
   }
}
