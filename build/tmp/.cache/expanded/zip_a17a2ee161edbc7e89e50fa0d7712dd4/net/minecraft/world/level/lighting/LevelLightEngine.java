package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;

public class LevelLightEngine implements LightEventListener {
   public static final int LIGHT_SECTION_PADDING = 1;
   protected final LevelHeightAccessor levelHeightAccessor;
   @Nullable
   private final LightEngine<?, ?> blockEngine;
   @Nullable
   private final LightEngine<?, ?> skyEngine;

   public LevelLightEngine(LightChunkGetter pLightChunkGetter, boolean p_75806_, boolean p_75807_) {
      this.levelHeightAccessor = pLightChunkGetter.getLevel();
      this.blockEngine = p_75806_ ? new BlockLightEngine(pLightChunkGetter) : null;
      this.skyEngine = p_75807_ ? new SkyLightEngine(pLightChunkGetter) : null;
   }

   public void checkBlock(BlockPos pPos) {
      if (this.blockEngine != null) {
         this.blockEngine.checkBlock(pPos);
      }

      if (this.skyEngine != null) {
         this.skyEngine.checkBlock(pPos);
      }

   }

   public boolean hasLightWork() {
      if (this.skyEngine != null && this.skyEngine.hasLightWork()) {
         return true;
      } else {
         return this.blockEngine != null && this.blockEngine.hasLightWork();
      }
   }

   public int runLightUpdates() {
      int i = 0;
      if (this.blockEngine != null) {
         i += this.blockEngine.runLightUpdates();
      }

      if (this.skyEngine != null) {
         i += this.skyEngine.runLightUpdates();
      }

      return i;
   }

   public void updateSectionStatus(SectionPos pPos, boolean pIsEmpty) {
      if (this.blockEngine != null) {
         this.blockEngine.updateSectionStatus(pPos, pIsEmpty);
      }

      if (this.skyEngine != null) {
         this.skyEngine.updateSectionStatus(pPos, pIsEmpty);
      }

   }

   public void setLightEnabled(ChunkPos p_285439_, boolean p_285012_) {
      if (this.blockEngine != null) {
         this.blockEngine.setLightEnabled(p_285439_, p_285012_);
      }

      if (this.skyEngine != null) {
         this.skyEngine.setLightEnabled(p_285439_, p_285012_);
      }

   }

   public void propagateLightSources(ChunkPos p_284998_) {
      if (this.blockEngine != null) {
         this.blockEngine.propagateLightSources(p_284998_);
      }

      if (this.skyEngine != null) {
         this.skyEngine.propagateLightSources(p_284998_);
      }

   }

   public LayerLightEventListener getLayerListener(LightLayer pType) {
      if (pType == LightLayer.BLOCK) {
         return (LayerLightEventListener)(this.blockEngine == null ? LayerLightEventListener.DummyLightLayerEventListener.INSTANCE : this.blockEngine);
      } else {
         return (LayerLightEventListener)(this.skyEngine == null ? LayerLightEventListener.DummyLightLayerEventListener.INSTANCE : this.skyEngine);
      }
   }

   public String getDebugData(LightLayer p_75817_, SectionPos p_75818_) {
      if (p_75817_ == LightLayer.BLOCK) {
         if (this.blockEngine != null) {
            return this.blockEngine.getDebugData(p_75818_.asLong());
         }
      } else if (this.skyEngine != null) {
         return this.skyEngine.getDebugData(p_75818_.asLong());
      }

      return "n/a";
   }

   public LayerLightSectionStorage.SectionType getDebugSectionType(LightLayer p_285008_, SectionPos p_285336_) {
      if (p_285008_ == LightLayer.BLOCK) {
         if (this.blockEngine != null) {
            return this.blockEngine.getDebugSectionType(p_285336_.asLong());
         }
      } else if (this.skyEngine != null) {
         return this.skyEngine.getDebugSectionType(p_285336_.asLong());
      }

      return LayerLightSectionStorage.SectionType.EMPTY;
   }

   public void queueSectionData(LightLayer p_285328_, SectionPos p_284962_, @Nullable DataLayer p_285035_) {
      if (p_285328_ == LightLayer.BLOCK) {
         if (this.blockEngine != null) {
            this.blockEngine.queueSectionData(p_284962_.asLong(), p_285035_);
         }
      } else if (this.skyEngine != null) {
         this.skyEngine.queueSectionData(p_284962_.asLong(), p_285035_);
      }

   }

   public void retainData(ChunkPos pPos, boolean pRetain) {
      if (this.blockEngine != null) {
         this.blockEngine.retainData(pPos, pRetain);
      }

      if (this.skyEngine != null) {
         this.skyEngine.retainData(pPos, pRetain);
      }

   }

   public int getRawBrightness(BlockPos pBlockPos, int pAmount) {
      int i = this.skyEngine == null ? 0 : this.skyEngine.getLightValue(pBlockPos) - pAmount;
      int j = this.blockEngine == null ? 0 : this.blockEngine.getLightValue(pBlockPos);
      return Math.max(j, i);
   }

   public boolean lightOnInSection(SectionPos p_285319_) {
      long i = p_285319_.asLong();
      return this.blockEngine == null || this.blockEngine.storage.lightOnInSection(i) && (this.skyEngine == null || this.skyEngine.storage.lightOnInSection(i));
   }

   public int getLightSectionCount() {
      return this.levelHeightAccessor.getSectionsCount() + 2;
   }

   public int getMinLightSection() {
      return this.levelHeightAccessor.getMinSection() - 1;
   }

   public int getMaxLightSection() {
      return this.getMinLightSection() + this.getLightSectionCount();
   }
}