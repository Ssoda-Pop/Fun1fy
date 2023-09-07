package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;

public interface LightEventListener {
   void checkBlock(BlockPos pPos);

   boolean hasLightWork();

   int runLightUpdates();

   default void updateSectionStatus(BlockPos pPos, boolean pIsQueueEmpty) {
      this.updateSectionStatus(SectionPos.of(pPos), pIsQueueEmpty);
   }

   void updateSectionStatus(SectionPos pPos, boolean pIsQueueEmpty);

   void setLightEnabled(ChunkPos p_164452_, boolean p_164453_);

   void propagateLightSources(ChunkPos p_285263_);
}