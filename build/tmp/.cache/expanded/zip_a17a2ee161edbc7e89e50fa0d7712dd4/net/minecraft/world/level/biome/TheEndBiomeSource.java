package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.levelgen.DensityFunction;

public class TheEndBiomeSource extends BiomeSource {
   public static final Codec<TheEndBiomeSource> CODEC = RecordCodecBuilder.create((p_255555_) -> {
      return p_255555_.group(RegistryOps.retrieveElement(Biomes.THE_END), RegistryOps.retrieveElement(Biomes.END_HIGHLANDS), RegistryOps.retrieveElement(Biomes.END_MIDLANDS), RegistryOps.retrieveElement(Biomes.SMALL_END_ISLANDS), RegistryOps.retrieveElement(Biomes.END_BARRENS)).apply(p_255555_, p_255555_.stable(TheEndBiomeSource::new));
   });
   private final Holder<Biome> end;
   private final Holder<Biome> highlands;
   private final Holder<Biome> midlands;
   private final Holder<Biome> islands;
   private final Holder<Biome> barrens;

   public static TheEndBiomeSource create(HolderGetter<Biome> p_256561_) {
      return new TheEndBiomeSource(p_256561_.getOrThrow(Biomes.THE_END), p_256561_.getOrThrow(Biomes.END_HIGHLANDS), p_256561_.getOrThrow(Biomes.END_MIDLANDS), p_256561_.getOrThrow(Biomes.SMALL_END_ISLANDS), p_256561_.getOrThrow(Biomes.END_BARRENS));
   }

   private TheEndBiomeSource(Holder<Biome> pEnd, Holder<Biome> pHighlands, Holder<Biome> pMidlands, Holder<Biome> pIslands, Holder<Biome> pBarrens) {
      this.end = pEnd;
      this.highlands = pHighlands;
      this.midlands = pMidlands;
      this.islands = pIslands;
      this.barrens = pBarrens;
   }

   protected Stream<Holder<Biome>> collectPossibleBiomes() {
      return Stream.of(this.end, this.highlands, this.midlands, this.islands, this.barrens);
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public Holder<Biome> getNoiseBiome(int pX, int pY, int pZ, Climate.Sampler pSampler) {
      int i = QuartPos.toBlock(pX);
      int j = QuartPos.toBlock(pY);
      int k = QuartPos.toBlock(pZ);
      int l = SectionPos.blockToSectionCoord(i);
      int i1 = SectionPos.blockToSectionCoord(k);
      if ((long)l * (long)l + (long)i1 * (long)i1 <= 4096L) {
         return this.end;
      } else {
         int j1 = (SectionPos.blockToSectionCoord(i) * 2 + 1) * 8;
         int k1 = (SectionPos.blockToSectionCoord(k) * 2 + 1) * 8;
         double d0 = pSampler.erosion().compute(new DensityFunction.SinglePointContext(j1, j, k1));
         if (d0 > 0.25D) {
            return this.highlands;
         } else if (d0 >= -0.0625D) {
            return this.midlands;
         } else {
            return d0 < -0.21875D ? this.islands : this.barrens;
         }
      }
   }
}