package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public abstract class AbstractTreeGrower {
   @Nullable
   protected abstract ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_222910_, boolean p_222911_);

   public boolean growTree(ServerLevel pLevle, ChunkGenerator pGenerator, BlockPos pPos, BlockState pState, RandomSource pRandom) {
      ResourceKey<ConfiguredFeature<?, ?>> resourcekey = this.getConfiguredFeature(pRandom, this.hasFlowers(pLevle, pPos));
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
            BlockState blockstate = pLevle.getFluidState(pPos).createLegacyBlock();
            pLevle.setBlock(pPos, blockstate, 4);
            if (configuredfeature.place(pLevle, pGenerator, pRandom, pPos)) {
               if (pLevle.getBlockState(pPos) == blockstate) {
                  pLevle.sendBlockUpdated(pPos, pState, blockstate, 2);
               }

               return true;
            } else {
               pLevle.setBlock(pPos, pState, 4);
               return false;
            }
         }
      }
   }

   private boolean hasFlowers(LevelAccessor pLevel, BlockPos pPos) {
      for(BlockPos blockpos : BlockPos.MutableBlockPos.betweenClosed(pPos.below().north(2).west(2), pPos.above().south(2).east(2))) {
         if (pLevel.getBlockState(blockpos).is(BlockTags.FLOWERS)) {
            return true;
         }
      }

      return false;
   }
}
