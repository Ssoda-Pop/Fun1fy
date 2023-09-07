package pop.zakichel.funify.item.custom;


import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import pop.zakichel.funify.entity.FunEntities;
import pop.zakichel.funify.entity.MolotovEntity;

public class MolotovItem extends Item {
    public MolotovItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pInteractionHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pInteractionHand);
        pLevel.playSound((Player)null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!pLevel.isClientSide) {
            MolotovEntity molly = new MolotovEntity(FunEntities.THROWN_MOLOTOV.get(),pLevel);
            molly.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0, 1.5F, 0.0F);

            pLevel.addFreshEntity(molly);
        }
        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        if (!pPlayer.getAbilities().instabuild) {
            itemstack.shrink(1);
        }


        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }
}
