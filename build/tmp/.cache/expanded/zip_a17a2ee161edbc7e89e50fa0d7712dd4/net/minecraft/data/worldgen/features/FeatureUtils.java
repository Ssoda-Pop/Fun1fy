package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class FeatureUtils {
   public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> p_255869_) {
      AquaticFeatures.bootstrap(p_255869_);
      CaveFeatures.bootstrap(p_255869_);
      EndFeatures.bootstrap(p_255869_);
      MiscOverworldFeatures.bootstrap(p_255869_);
      NetherFeatures.bootstrap(p_255869_);
      OreFeatures.bootstrap(p_255869_);
      PileFeatures.bootstrap(p_255869_);
      TreeFeatures.bootstrap(p_255869_);
      VegetationFeatures.bootstrap(p_255869_);
   }

   private static BlockPredicate simplePatchPredicate(List<Block> pBlocks) {
      BlockPredicate blockpredicate;
      if (!pBlocks.isEmpty()) {
         blockpredicate = BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), pBlocks));
      } else {
         blockpredicate = BlockPredicate.ONLY_IN_AIR_PREDICATE;
      }

      return blockpredicate;
   }

   public static RandomPatchConfiguration simpleRandomPatchConfiguration(int pTries, Holder<PlacedFeature> pFeature) {
      return new RandomPatchConfiguration(pTries, 7, 3, pFeature);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(F pFeature, FC pConfig, List<Block> p_206483_, int pTries) {
      return simpleRandomPatchConfiguration(pTries, PlacementUtils.filtered(pFeature, pConfig, simplePatchPredicate(p_206483_)));
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(F pFeature, FC pConfig, List<Block> p_206479_) {
      return simplePatchConfiguration(pFeature, pConfig, p_206479_, 96);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(F pFeature, FC pConfig) {
      return simplePatchConfiguration(pFeature, pConfig, List.of(), 96);
   }

   public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String p_255643_) {
      return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(p_255643_));
   }

   public static void register(BootstapContext<ConfiguredFeature<?, ?>> p_256637_, ResourceKey<ConfiguredFeature<?, ?>> p_256555_, Feature<NoneFeatureConfiguration> p_255921_) {
      register(p_256637_, p_256555_, p_255921_, FeatureConfiguration.NONE);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> p_256315_, ResourceKey<ConfiguredFeature<?, ?>> p_255983_, F p_255949_, FC p_256398_) {
      p_256315_.register(p_255983_, new ConfiguredFeature(p_255949_, p_256398_));
   }
}