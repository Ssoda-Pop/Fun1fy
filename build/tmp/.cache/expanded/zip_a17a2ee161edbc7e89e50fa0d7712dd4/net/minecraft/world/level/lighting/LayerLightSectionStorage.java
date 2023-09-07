package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;

public abstract class LayerLightSectionStorage<M extends DataLayerStorageMap<M>> {
   private final LightLayer layer;
   protected final LightChunkGetter chunkSource;
   protected final Long2ByteMap sectionStates = new Long2ByteOpenHashMap();
   private final LongSet columnsWithSources = new LongOpenHashSet();
   protected volatile M visibleSectionData;
   protected final M updatingSectionData;
   protected final LongSet changedSections = new LongOpenHashSet();
   protected final LongSet sectionsAffectedByLightUpdates = new LongOpenHashSet();
   protected final Long2ObjectMap<DataLayer> queuedSections = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());
   /**
    * Section column positions (section positions with Y=0) that need to be kept even if some of their sections could
    * otherwise be removed.
    */
   private final LongSet columnsToRetainQueuedDataFor = new LongOpenHashSet();
   /** Set of section positions that can be removed, because their light won't affect any blocks. */
   private final LongSet toRemove = new LongOpenHashSet();
   protected volatile boolean hasInconsistencies;

   protected LayerLightSectionStorage(LightLayer pLayer, LightChunkGetter pChunkSource, M pUpdatingSectionData) {
      this.layer = pLayer;
      this.chunkSource = pChunkSource;
      this.updatingSectionData = pUpdatingSectionData;
      this.visibleSectionData = pUpdatingSectionData.copy();
      this.visibleSectionData.disableCache();
      this.sectionStates.defaultReturnValue((byte)0);
   }

   protected boolean storingLightForSection(long pSectionPos) {
      return this.getDataLayer(pSectionPos, true) != null;
   }

   @Nullable
   protected DataLayer getDataLayer(long pSectionPos, boolean pCached) {
      return this.getDataLayer((M)(pCached ? this.updatingSectionData : this.visibleSectionData), pSectionPos);
   }

   @Nullable
   protected DataLayer getDataLayer(M pMap, long pSectionPos) {
      return pMap.getLayer(pSectionPos);
   }

   @Nullable
   protected DataLayer getDataLayerToWrite(long p_285278_) {
      DataLayer datalayer = this.updatingSectionData.getLayer(p_285278_);
      if (datalayer == null) {
         return null;
      } else {
         if (this.changedSections.add(p_285278_)) {
            datalayer = datalayer.copy();
            this.updatingSectionData.setLayer(p_285278_, datalayer);
            this.updatingSectionData.clearCache();
         }

         return datalayer;
      }
   }

   @Nullable
   public DataLayer getDataLayerData(long pSectionPos) {
      DataLayer datalayer = this.queuedSections.get(pSectionPos);
      return datalayer != null ? datalayer : this.getDataLayer(pSectionPos, false);
   }

   protected abstract int getLightValue(long pLevelPos);

   protected int getStoredLevel(long pLevelPos) {
      long i = SectionPos.blockToSection(pLevelPos);
      DataLayer datalayer = this.getDataLayer(i, true);
      return datalayer.get(SectionPos.sectionRelative(BlockPos.getX(pLevelPos)), SectionPos.sectionRelative(BlockPos.getY(pLevelPos)), SectionPos.sectionRelative(BlockPos.getZ(pLevelPos)));
   }

   protected void setStoredLevel(long pLevelPos, int pLightLevel) {
      long i = SectionPos.blockToSection(pLevelPos);
      DataLayer datalayer;
      if (this.changedSections.add(i)) {
         datalayer = this.updatingSectionData.copyDataLayer(i);
      } else {
         datalayer = this.getDataLayer(i, true);
      }

      datalayer.set(SectionPos.sectionRelative(BlockPos.getX(pLevelPos)), SectionPos.sectionRelative(BlockPos.getY(pLevelPos)), SectionPos.sectionRelative(BlockPos.getZ(pLevelPos)), pLightLevel);
      SectionPos.aroundAndAtBlockPos(pLevelPos, this.sectionsAffectedByLightUpdates::add);
   }

   protected void markSectionAndNeighborsAsAffected(long p_281610_) {
      int i = SectionPos.x(p_281610_);
      int j = SectionPos.y(p_281610_);
      int k = SectionPos.z(p_281610_);

      for(int l = -1; l <= 1; ++l) {
         for(int i1 = -1; i1 <= 1; ++i1) {
            for(int j1 = -1; j1 <= 1; ++j1) {
               this.sectionsAffectedByLightUpdates.add(SectionPos.asLong(i + i1, j + j1, k + l));
            }
         }
      }

   }

   protected DataLayer createDataLayer(long pSectionPos) {
      DataLayer datalayer = this.queuedSections.get(pSectionPos);
      return datalayer != null ? datalayer : new DataLayer();
   }

   protected boolean hasInconsistencies() {
      return this.hasInconsistencies;
   }

   protected void markNewInconsistencies(LightEngine<M, ?> p_285081_) {
      if (this.hasInconsistencies) {
         this.hasInconsistencies = false;

         for(long i : this.toRemove) {
            DataLayer datalayer = this.queuedSections.remove(i);
            DataLayer datalayer1 = this.updatingSectionData.removeLayer(i);
            if (this.columnsToRetainQueuedDataFor.contains(SectionPos.getZeroNode(i))) {
               if (datalayer != null) {
                  this.queuedSections.put(i, datalayer);
               } else if (datalayer1 != null) {
                  this.queuedSections.put(i, datalayer1);
               }
            }
         }

         this.updatingSectionData.clearCache();

         for(long k : this.toRemove) {
            this.onNodeRemoved(k);
            this.changedSections.add(k);
         }

         this.toRemove.clear();
         ObjectIterator<Long2ObjectMap.Entry<DataLayer>> objectiterator = Long2ObjectMaps.fastIterator(this.queuedSections);

         while(objectiterator.hasNext()) {
            Long2ObjectMap.Entry<DataLayer> entry = objectiterator.next();
            long j = entry.getLongKey();
            if (this.storingLightForSection(j)) {
               DataLayer datalayer2 = entry.getValue();
               if (this.updatingSectionData.getLayer(j) != datalayer2) {
                  this.updatingSectionData.setLayer(j, datalayer2);
                  this.changedSections.add(j);
               }

               objectiterator.remove();
            }
         }

         this.updatingSectionData.clearCache();
      }
   }

   protected void onNodeAdded(long pSectionPos) {
   }

   protected void onNodeRemoved(long pSectionPos) {
   }

   protected void setLightEnabled(long p_285065_, boolean p_284938_) {
      if (p_284938_) {
         this.columnsWithSources.add(p_285065_);
      } else {
         this.columnsWithSources.remove(p_285065_);
      }

   }

   protected boolean lightOnInSection(long p_285433_) {
      long i = SectionPos.getZeroNode(p_285433_);
      return this.columnsWithSources.contains(i);
   }

   public void retainData(long pSectionColumnPos, boolean pRetain) {
      if (pRetain) {
         this.columnsToRetainQueuedDataFor.add(pSectionColumnPos);
      } else {
         this.columnsToRetainQueuedDataFor.remove(pSectionColumnPos);
      }

   }

   protected void queueSectionData(long p_285403_, @Nullable DataLayer p_285498_) {
      if (p_285498_ != null) {
         this.queuedSections.put(p_285403_, p_285498_);
         this.hasInconsistencies = true;
      } else {
         this.queuedSections.remove(p_285403_);
      }

   }

   protected void updateSectionStatus(long pSectionPos, boolean pIsEmpty) {
      byte b0 = this.sectionStates.get(pSectionPos);
      byte b1 = LayerLightSectionStorage.SectionState.hasData(b0, !pIsEmpty);
      if (b0 != b1) {
         this.putSectionState(pSectionPos, b1);
         int i = pIsEmpty ? -1 : 1;

         for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
               for(int l = -1; l <= 1; ++l) {
                  if (j != 0 || k != 0 || l != 0) {
                     long i1 = SectionPos.offset(pSectionPos, j, k, l);
                     byte b2 = this.sectionStates.get(i1);
                     this.putSectionState(i1, LayerLightSectionStorage.SectionState.neighborCount(b2, LayerLightSectionStorage.SectionState.neighborCount(b2) + i));
                  }
               }
            }
         }

      }
   }

   protected void putSectionState(long p_285451_, byte p_285078_) {
      if (p_285078_ != 0) {
         if (this.sectionStates.put(p_285451_, p_285078_) == 0) {
            this.initializeSection(p_285451_);
         }
      } else if (this.sectionStates.remove(p_285451_) != 0) {
         this.removeSection(p_285451_);
      }

   }

   private void initializeSection(long p_285124_) {
      if (!this.toRemove.remove(p_285124_)) {
         this.updatingSectionData.setLayer(p_285124_, this.createDataLayer(p_285124_));
         this.changedSections.add(p_285124_);
         this.onNodeAdded(p_285124_);
         this.markSectionAndNeighborsAsAffected(p_285124_);
         this.hasInconsistencies = true;
      }

   }

   private void removeSection(long p_285477_) {
      this.toRemove.add(p_285477_);
      this.hasInconsistencies = true;
   }

   protected void swapSectionMap() {
      if (!this.changedSections.isEmpty()) {
         M m = this.updatingSectionData.copy();
         m.disableCache();
         this.visibleSectionData = m;
         this.changedSections.clear();
      }

      if (!this.sectionsAffectedByLightUpdates.isEmpty()) {
         LongIterator longiterator = this.sectionsAffectedByLightUpdates.iterator();

         while(longiterator.hasNext()) {
            long i = longiterator.nextLong();
            this.chunkSource.onLightUpdate(this.layer, SectionPos.of(i));
         }

         this.sectionsAffectedByLightUpdates.clear();
      }

   }

   public LayerLightSectionStorage.SectionType getDebugSectionType(long p_285114_) {
      return LayerLightSectionStorage.SectionState.type(this.sectionStates.get(p_285114_));
   }

   protected static class SectionState {
      public static final byte EMPTY = 0;
      private static final int MIN_NEIGHBORS = 0;
      private static final int MAX_NEIGHBORS = 26;
      private static final byte HAS_DATA_BIT = 32;
      private static final byte NEIGHBOR_COUNT_BITS = 31;

      public static byte hasData(byte p_284954_, boolean p_285420_) {
         return (byte)(p_285420_ ? p_284954_ | 32 : p_284954_ & -33);
      }

      public static byte neighborCount(byte p_285516_, int p_285426_) {
         if (p_285426_ >= 0 && p_285426_ <= 26) {
            return (byte)(p_285516_ & -32 | p_285426_ & 31);
         } else {
            throw new IllegalArgumentException("Neighbor count was not within range [0; 26]");
         }
      }

      public static boolean hasData(byte p_285105_) {
         return (p_285105_ & 32) != 0;
      }

      public static int neighborCount(byte p_285437_) {
         return p_285437_ & 31;
      }

      public static LayerLightSectionStorage.SectionType type(byte p_285064_) {
         if (p_285064_ == 0) {
            return LayerLightSectionStorage.SectionType.EMPTY;
         } else {
            return hasData(p_285064_) ? LayerLightSectionStorage.SectionType.LIGHT_AND_DATA : LayerLightSectionStorage.SectionType.LIGHT_ONLY;
         }
      }
   }

   public static enum SectionType {
      EMPTY("2"),
      LIGHT_ONLY("1"),
      LIGHT_AND_DATA("0");

      private final String display;

      private SectionType(String p_285063_) {
         this.display = p_285063_;
      }

      public String display() {
         return this.display;
      }
   }
}