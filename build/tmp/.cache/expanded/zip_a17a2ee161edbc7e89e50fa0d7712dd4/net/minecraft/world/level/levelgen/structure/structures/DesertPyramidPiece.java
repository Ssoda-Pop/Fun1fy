package net.minecraft.world.level.levelgen.structure.structures;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class DesertPyramidPiece extends ScatteredFeaturePiece {
   public static final int WIDTH = 21;
   public static final int DEPTH = 21;
   private final boolean[] hasPlacedChest = new boolean[4];
   private final List<BlockPos> potentialSuspiciousSandWorldPositions = new ArrayList<>();
   private BlockPos randomCollapsedRoofPos = BlockPos.ZERO;

   public DesertPyramidPiece(RandomSource pRandom, int pX, int pZ) {
      super(StructurePieceType.DESERT_PYRAMID_PIECE, pX, 64, pZ, 21, 15, 21, getRandomHorizontalDirection(pRandom));
   }

   public DesertPyramidPiece(CompoundTag pTag) {
      super(StructurePieceType.DESERT_PYRAMID_PIECE, pTag);
      this.hasPlacedChest[0] = pTag.getBoolean("hasPlacedChest0");
      this.hasPlacedChest[1] = pTag.getBoolean("hasPlacedChest1");
      this.hasPlacedChest[2] = pTag.getBoolean("hasPlacedChest2");
      this.hasPlacedChest[3] = pTag.getBoolean("hasPlacedChest3");
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext pContext, CompoundTag pTag) {
      super.addAdditionalSaveData(pContext, pTag);
      pTag.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
      pTag.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
      pTag.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
      pTag.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
   }

   public void postProcess(WorldGenLevel pLevel, StructureManager pStructureManager, ChunkGenerator pGenerator, RandomSource pRandom, BoundingBox pBox, ChunkPos pChunkPos, BlockPos pPos) {
      if (this.updateHeightPositionToLowestGroundHeight(pLevel, -pRandom.nextInt(3))) {
         this.generateBox(pLevel, pBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);

         for(int i = 1; i <= 9; ++i) {
            this.generateBox(pLevel, pBox, i, i, i, this.width - 1 - i, i, this.depth - 1 - i, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox(pLevel, pBox, i + 1, i, i + 1, this.width - 2 - i, i, this.depth - 2 - i, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         }

         for(int k1 = 0; k1 < this.width; ++k1) {
            for(int j = 0; j < this.depth; ++j) {
               int k = -5;
               this.fillColumnDown(pLevel, Blocks.SANDSTONE.defaultBlockState(), k1, -5, j, pBox);
            }
         }

         BlockState blockstate1 = Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         BlockState blockstate2 = Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
         BlockState blockstate3 = Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
         BlockState blockstate = Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
         this.generateBox(pLevel, pBox, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.placeBlock(pLevel, blockstate1, 2, 10, 0, pBox);
         this.placeBlock(pLevel, blockstate2, 2, 10, 4, pBox);
         this.placeBlock(pLevel, blockstate3, 0, 10, 2, pBox);
         this.placeBlock(pLevel, blockstate, 4, 10, 2, pBox);
         this.generateBox(pLevel, pBox, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.placeBlock(pLevel, blockstate1, this.width - 3, 10, 0, pBox);
         this.placeBlock(pLevel, blockstate2, this.width - 3, 10, 4, pBox);
         this.placeBlock(pLevel, blockstate3, this.width - 5, 10, 2, pBox);
         this.placeBlock(pLevel, blockstate, this.width - 1, 10, 2, pBox);
         this.generateBox(pLevel, pBox, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 9, 1, 0, 11, 3, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 1, 1, pBox);
         this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 2, 1, pBox);
         this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 3, 1, pBox);
         this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, 3, 1, pBox);
         this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 3, 1, pBox);
         this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 2, 1, pBox);
         this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 1, 1, pBox);
         this.generateBox(pLevel, pBox, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 4, 1, 2, 8, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 12, 1, 2, 16, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 9, 4, 9, 11, 4, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), 5, 5, 10, pBox);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), 5, 6, 10, pBox);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), 6, 6, 10, pBox);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), this.width - 6, 5, 10, pBox);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), this.width - 6, 6, 10, pBox);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), this.width - 7, 6, 10, pBox);
         this.generateBox(pLevel, pBox, 2, 4, 4, 2, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(pLevel, blockstate1, 2, 4, 5, pBox);
         this.placeBlock(pLevel, blockstate1, 2, 3, 4, pBox);
         this.placeBlock(pLevel, blockstate1, this.width - 3, 4, 5, pBox);
         this.placeBlock(pLevel, blockstate1, this.width - 3, 3, 4, pBox);
         this.generateBox(pLevel, pBox, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.placeBlock(pLevel, Blocks.SANDSTONE.defaultBlockState(), 1, 1, 2, pBox);
         this.placeBlock(pLevel, Blocks.SANDSTONE.defaultBlockState(), this.width - 2, 1, 2, pBox);
         this.placeBlock(pLevel, Blocks.SANDSTONE_SLAB.defaultBlockState(), 1, 2, 2, pBox);
         this.placeBlock(pLevel, Blocks.SANDSTONE_SLAB.defaultBlockState(), this.width - 2, 2, 2, pBox);
         this.placeBlock(pLevel, blockstate, 2, 1, 2, pBox);
         this.placeBlock(pLevel, blockstate3, this.width - 3, 1, 2, pBox);
         this.generateBox(pLevel, pBox, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 3, 1, 5, 4, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

         for(int l = 5; l <= 17; l += 2) {
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), 4, 1, l, pBox);
            this.placeBlock(pLevel, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 4, 2, l, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), this.width - 5, 1, l, pBox);
            this.placeBlock(pLevel, Blocks.CHISELED_SANDSTONE.defaultBlockState(), this.width - 5, 2, l, pBox);
         }

         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 7, pBox);
         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 8, pBox);
         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 9, pBox);
         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 9, pBox);
         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 8, 0, 10, pBox);
         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 12, 0, 10, pBox);
         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 7, 0, 10, pBox);
         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 13, 0, 10, pBox);
         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 11, pBox);
         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 11, pBox);
         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 12, pBox);
         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 13, pBox);
         this.placeBlock(pLevel, Blocks.BLUE_TERRACOTTA.defaultBlockState(), 10, 0, 10, pBox);

         for(int l1 = 0; l1 <= this.width - 1; l1 += this.width - 1) {
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 2, 1, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 2, 2, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 2, 3, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 3, 1, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 3, 2, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 3, 3, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 4, 1, pBox);
            this.placeBlock(pLevel, Blocks.CHISELED_SANDSTONE.defaultBlockState(), l1, 4, 2, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 4, 3, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 5, 1, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 5, 2, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 5, 3, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 6, 1, pBox);
            this.placeBlock(pLevel, Blocks.CHISELED_SANDSTONE.defaultBlockState(), l1, 6, 2, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 6, 3, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 7, 1, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 7, 2, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 7, 3, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 8, 1, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 8, 2, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 8, 3, pBox);
         }

         for(int i2 = 2; i2 <= this.width - 3; i2 += this.width - 3 - 2) {
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 - 1, 2, 0, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2, 2, 0, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 + 1, 2, 0, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 - 1, 3, 0, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2, 3, 0, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 + 1, 3, 0, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 - 1, 4, 0, pBox);
            this.placeBlock(pLevel, Blocks.CHISELED_SANDSTONE.defaultBlockState(), i2, 4, 0, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 + 1, 4, 0, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 - 1, 5, 0, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2, 5, 0, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 + 1, 5, 0, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 - 1, 6, 0, pBox);
            this.placeBlock(pLevel, Blocks.CHISELED_SANDSTONE.defaultBlockState(), i2, 6, 0, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 + 1, 6, 0, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 - 1, 7, 0, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2, 7, 0, pBox);
            this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 + 1, 7, 0, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 - 1, 8, 0, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), i2, 8, 0, pBox);
            this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 + 1, 8, 0, pBox);
         }

         this.generateBox(pLevel, pBox, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), 8, 6, 0, pBox);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), 12, 6, 0, pBox);
         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 5, 0, pBox);
         this.placeBlock(pLevel, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, 5, 0, pBox);
         this.placeBlock(pLevel, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 5, 0, pBox);
         this.generateBox(pLevel, pBox, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.defaultBlockState(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(pLevel, pBox, 9, -11, 9, 11, -1, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(pLevel, Blocks.STONE_PRESSURE_PLATE.defaultBlockState(), 10, -11, 10, pBox);
         this.generateBox(pLevel, pBox, 9, -13, 9, 11, -13, 11, Blocks.TNT.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), 8, -11, 10, pBox);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), 8, -10, 10, pBox);
         this.placeBlock(pLevel, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 7, -10, 10, pBox);
         this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), 7, -11, 10, pBox);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), 12, -11, 10, pBox);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), 12, -10, 10, pBox);
         this.placeBlock(pLevel, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 13, -10, 10, pBox);
         this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), 13, -11, 10, pBox);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), 10, -11, 8, pBox);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), 10, -10, 8, pBox);
         this.placeBlock(pLevel, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 7, pBox);
         this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 7, pBox);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), 10, -11, 12, pBox);
         this.placeBlock(pLevel, Blocks.AIR.defaultBlockState(), 10, -10, 12, pBox);
         this.placeBlock(pLevel, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 13, pBox);
         this.placeBlock(pLevel, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 13, pBox);

         for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (!this.hasPlacedChest[direction.get2DDataValue()]) {
               int i1 = direction.getStepX() * 2;
               int j1 = direction.getStepZ() * 2;
               this.hasPlacedChest[direction.get2DDataValue()] = this.createChest(pLevel, pBox, pRandom, 10 + i1, -11, 10 + j1, BuiltInLootTables.DESERT_PYRAMID);
            }
         }

         this.addCellar(pLevel, pBox);
      }
   }

   private void addCellar(WorldGenLevel p_272769_, BoundingBox p_273155_) {
      BlockPos blockpos = new BlockPos(16, -4, 13);
      this.addCellarStairs(blockpos, p_272769_, p_273155_);
      this.addCellarRoom(blockpos, p_272769_, p_273155_);
   }

   private void addCellarStairs(BlockPos p_272997_, WorldGenLevel p_272699_, BoundingBox p_273559_) {
      int i = p_272997_.getX();
      int j = p_272997_.getY();
      int k = p_272997_.getZ();
      BlockState blockstate = Blocks.SANDSTONE_STAIRS.defaultBlockState();
      this.placeBlock(p_272699_, blockstate.rotate(Rotation.COUNTERCLOCKWISE_90), 13, -1, 17, p_273559_);
      this.placeBlock(p_272699_, blockstate.rotate(Rotation.COUNTERCLOCKWISE_90), 14, -2, 17, p_273559_);
      this.placeBlock(p_272699_, blockstate.rotate(Rotation.COUNTERCLOCKWISE_90), 15, -3, 17, p_273559_);
      BlockState blockstate1 = Blocks.SAND.defaultBlockState();
      BlockState blockstate2 = Blocks.SANDSTONE.defaultBlockState();
      boolean flag = p_272699_.getRandom().nextBoolean();
      this.placeBlock(p_272699_, blockstate1, i - 4, j + 4, k + 4, p_273559_);
      this.placeBlock(p_272699_, blockstate1, i - 3, j + 4, k + 4, p_273559_);
      this.placeBlock(p_272699_, blockstate1, i - 2, j + 4, k + 4, p_273559_);
      this.placeBlock(p_272699_, blockstate1, i - 1, j + 4, k + 4, p_273559_);
      this.placeBlock(p_272699_, blockstate1, i, j + 4, k + 4, p_273559_);
      this.placeBlock(p_272699_, blockstate1, i - 2, j + 3, k + 4, p_273559_);
      this.placeBlock(p_272699_, flag ? blockstate1 : blockstate2, i - 1, j + 3, k + 4, p_273559_);
      this.placeBlock(p_272699_, !flag ? blockstate1 : blockstate2, i, j + 3, k + 4, p_273559_);
      this.placeBlock(p_272699_, blockstate1, i - 1, j + 2, k + 4, p_273559_);
      this.placeBlock(p_272699_, blockstate2, i, j + 2, k + 4, p_273559_);
      this.placeBlock(p_272699_, blockstate1, i, j + 1, k + 4, p_273559_);
   }

   private void addCellarRoom(BlockPos p_272733_, WorldGenLevel p_273390_, BoundingBox p_273517_) {
      int i = p_272733_.getX();
      int j = p_272733_.getY();
      int k = p_272733_.getZ();
      BlockState blockstate = Blocks.CUT_SANDSTONE.defaultBlockState();
      BlockState blockstate1 = Blocks.CHISELED_SANDSTONE.defaultBlockState();
      this.generateBox(p_273390_, p_273517_, i - 3, j + 1, k - 3, i - 3, j + 1, k + 2, blockstate, blockstate, true);
      this.generateBox(p_273390_, p_273517_, i + 3, j + 1, k - 3, i + 3, j + 1, k + 2, blockstate, blockstate, true);
      this.generateBox(p_273390_, p_273517_, i - 3, j + 1, k - 3, i + 3, j + 1, k - 2, blockstate, blockstate, true);
      this.generateBox(p_273390_, p_273517_, i - 3, j + 1, k + 3, i + 3, j + 1, k + 3, blockstate, blockstate, true);
      this.generateBox(p_273390_, p_273517_, i - 3, j + 2, k - 3, i - 3, j + 2, k + 2, blockstate1, blockstate1, true);
      this.generateBox(p_273390_, p_273517_, i + 3, j + 2, k - 3, i + 3, j + 2, k + 2, blockstate1, blockstate1, true);
      this.generateBox(p_273390_, p_273517_, i - 3, j + 2, k - 3, i + 3, j + 2, k - 2, blockstate1, blockstate1, true);
      this.generateBox(p_273390_, p_273517_, i - 3, j + 2, k + 3, i + 3, j + 2, k + 3, blockstate1, blockstate1, true);
      this.generateBox(p_273390_, p_273517_, i - 3, -1, k - 3, i - 3, -1, k + 2, blockstate, blockstate, true);
      this.generateBox(p_273390_, p_273517_, i + 3, -1, k - 3, i + 3, -1, k + 2, blockstate, blockstate, true);
      this.generateBox(p_273390_, p_273517_, i - 3, -1, k - 3, i + 3, -1, k - 2, blockstate, blockstate, true);
      this.generateBox(p_273390_, p_273517_, i - 3, -1, k + 3, i + 3, -1, k + 3, blockstate, blockstate, true);
      this.placeSandBox(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2);
      this.placeCollapsedRoof(p_273390_, p_273517_, i - 2, j + 4, k - 2, i + 2, k + 2);
      BlockState blockstate2 = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
      BlockState blockstate3 = Blocks.BLUE_TERRACOTTA.defaultBlockState();
      this.placeBlock(p_273390_, blockstate3, i, j, k, p_273517_);
      this.placeBlock(p_273390_, blockstate2, i + 1, j, k - 1, p_273517_);
      this.placeBlock(p_273390_, blockstate2, i + 1, j, k + 1, p_273517_);
      this.placeBlock(p_273390_, blockstate2, i - 1, j, k - 1, p_273517_);
      this.placeBlock(p_273390_, blockstate2, i - 1, j, k + 1, p_273517_);
      this.placeBlock(p_273390_, blockstate2, i + 2, j, k, p_273517_);
      this.placeBlock(p_273390_, blockstate2, i - 2, j, k, p_273517_);
      this.placeBlock(p_273390_, blockstate2, i, j, k + 2, p_273517_);
      this.placeBlock(p_273390_, blockstate2, i, j, k - 2, p_273517_);
      this.placeBlock(p_273390_, blockstate2, i + 3, j, k, p_273517_);
      this.placeSand(i + 3, j + 1, k);
      this.placeSand(i + 3, j + 2, k);
      this.placeBlock(p_273390_, blockstate, i + 4, j + 1, k, p_273517_);
      this.placeBlock(p_273390_, blockstate1, i + 4, j + 2, k, p_273517_);
      this.placeBlock(p_273390_, blockstate2, i - 3, j, k, p_273517_);
      this.placeSand(i - 3, j + 1, k);
      this.placeSand(i - 3, j + 2, k);
      this.placeBlock(p_273390_, blockstate, i - 4, j + 1, k, p_273517_);
      this.placeBlock(p_273390_, blockstate1, i - 4, j + 2, k, p_273517_);
      this.placeBlock(p_273390_, blockstate2, i, j, k + 3, p_273517_);
      this.placeSand(i, j + 1, k + 3);
      this.placeSand(i, j + 2, k + 3);
      this.placeBlock(p_273390_, blockstate2, i, j, k - 3, p_273517_);
      this.placeSand(i, j + 1, k - 3);
      this.placeSand(i, j + 2, k - 3);
      this.placeBlock(p_273390_, blockstate, i, j + 1, k - 4, p_273517_);
      this.placeBlock(p_273390_, blockstate1, i, -2, k - 4, p_273517_);
   }

   private void placeSand(int p_279401_, int p_279451_, int p_279265_) {
      BlockPos blockpos = this.getWorldPos(p_279401_, p_279451_, p_279265_);
      this.potentialSuspiciousSandWorldPositions.add(blockpos);
   }

   private void placeSandBox(int p_279483_, int p_279321_, int p_279271_, int p_279471_, int p_279229_, int p_279111_) {
      for(int i = p_279321_; i <= p_279229_; ++i) {
         for(int j = p_279483_; j <= p_279471_; ++j) {
            for(int k = p_279271_; k <= p_279111_; ++k) {
               this.placeSand(j, i, k);
            }
         }
      }

   }

   private void placeCollapsedRoofPiece(WorldGenLevel p_272965_, int p_272618_, int p_273415_, int p_273110_, BoundingBox p_272645_) {
      if (p_272965_.getRandom().nextFloat() < 0.33F) {
         BlockState blockstate = Blocks.SANDSTONE.defaultBlockState();
         this.placeBlock(p_272965_, blockstate, p_272618_, p_273415_, p_273110_, p_272645_);
      } else {
         BlockState blockstate1 = Blocks.SAND.defaultBlockState();
         this.placeBlock(p_272965_, blockstate1, p_272618_, p_273415_, p_273110_, p_272645_);
      }

   }

   private void placeCollapsedRoof(WorldGenLevel p_273438_, BoundingBox p_273058_, int p_272638_, int p_272826_, int p_273026_, int p_272750_, int p_272639_) {
      for(int i = p_272638_; i <= p_272750_; ++i) {
         for(int j = p_273026_; j <= p_272639_; ++j) {
            this.placeCollapsedRoofPiece(p_273438_, i, p_272826_, j, p_273058_);
         }
      }

      RandomSource randomsource = RandomSource.create(p_273438_.getSeed()).forkPositional().at(this.getWorldPos(p_272638_, p_272826_, p_273026_));
      int l = randomsource.nextIntBetweenInclusive(p_272638_, p_272750_);
      int k = randomsource.nextIntBetweenInclusive(p_273026_, p_272639_);
      this.randomCollapsedRoofPos = new BlockPos(this.getWorldX(l, k), this.getWorldY(p_272826_), this.getWorldZ(l, k));
   }

   public List<BlockPos> getPotentialSuspiciousSandWorldPositions() {
      return this.potentialSuspiciousSandWorldPositions;
   }

   public BlockPos getRandomCollapsedRoofPos() {
      return this.randomCollapsedRoofPos;
   }
}