package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerBlock extends BaseEntityBlock {
   public SpawnerBlock(BlockBehaviour.Properties pProperties) {
      super(pProperties);
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new SpawnerBlockEntity(pPos, pState);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return createTickerHelper(pBlockEntityType, BlockEntityType.MOB_SPAWNER, pLevel.isClientSide ? SpawnerBlockEntity::clientTick : SpawnerBlockEntity::serverTick);
   }

   /**
    * Perform side-effects from block dropping, such as creating silverfish
    */
   public void spawnAfterBreak(BlockState pState, ServerLevel pLevel, BlockPos pPos, ItemStack pStack, boolean p_222481_) {
      super.spawnAfterBreak(pState, pLevel, pPos, pStack, p_222481_);

   }

   @Override
   public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader world, net.minecraft.util.RandomSource randomSource, BlockPos pos, int fortune, int silktouch) {
      return 15 + randomSource.nextInt(15) + randomSource.nextInt(15);
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehavior.BlockStateBase#getRenderShape}
    * whenever possible. Implementing/overriding is fine.
    */
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   public void appendHoverText(ItemStack p_255714_, @Nullable BlockGetter p_255801_, List<Component> p_255708_, TooltipFlag p_255667_) {
      super.appendHoverText(p_255714_, p_255801_, p_255708_, p_255667_);
      Optional<Component> optional = this.getSpawnEntityDisplayName(p_255714_);
      if (optional.isPresent()) {
         p_255708_.add(optional.get());
      } else {
         p_255708_.add(CommonComponents.EMPTY);
         p_255708_.add(Component.translatable("block.minecraft.spawner.desc1").withStyle(ChatFormatting.GRAY));
         p_255708_.add(CommonComponents.space().append(Component.translatable("block.minecraft.spawner.desc2").withStyle(ChatFormatting.BLUE)));
      }

   }

   private Optional<Component> getSpawnEntityDisplayName(ItemStack p_256057_) {
      CompoundTag compoundtag = BlockItem.getBlockEntityData(p_256057_);
      if (compoundtag != null && compoundtag.contains("SpawnData", 10)) {
         String s = compoundtag.getCompound("SpawnData").getCompound("entity").getString("id");
         ResourceLocation resourcelocation = ResourceLocation.tryParse(s);
         if (resourcelocation != null) {
            return BuiltInRegistries.ENTITY_TYPE.getOptional(resourcelocation).map((p_255782_) -> {
               return Component.translatable(p_255782_.getDescriptionId()).withStyle(ChatFormatting.GRAY);
            });
         }
      }

      return Optional.empty();
   }
}
