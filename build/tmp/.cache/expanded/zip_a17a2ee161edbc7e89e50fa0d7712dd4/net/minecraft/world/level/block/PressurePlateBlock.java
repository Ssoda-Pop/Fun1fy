package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class PressurePlateBlock extends BasePressurePlateBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private final PressurePlateBlock.Sensitivity sensitivity;

   public PressurePlateBlock(PressurePlateBlock.Sensitivity p_273523_, BlockBehaviour.Properties p_273571_, BlockSetType p_273284_) {
      super(p_273571_, p_273284_);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.valueOf(false)));
      this.sensitivity = p_273523_;
   }

   protected int getSignalForState(BlockState pState) {
      return pState.getValue(POWERED) ? 15 : 0;
   }

   protected BlockState setSignalForState(BlockState pState, int pStrength) {
      return pState.setValue(POWERED, Boolean.valueOf(pStrength > 0));
   }

   protected int getSignalStrength(Level pLevel, BlockPos pPos) {
      Class<? extends Entity> oclass1;
      switch (this.sensitivity) {
         case EVERYTHING:
            oclass1 = Entity.class;
            break;
         case MOBS:
            oclass1 = LivingEntity.class;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      Class oclass = oclass1;
      return getEntityCount(pLevel, TOUCH_AABB.move(pPos), oclass) > 0 ? 15 : 0;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
      pBuilder.add(POWERED);
   }

   public static enum Sensitivity {
      EVERYTHING,
      MOBS;
   }
}