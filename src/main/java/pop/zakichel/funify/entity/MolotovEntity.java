package pop.zakichel.funify.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;
import pop.zakichel.funify.item.FunItems;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class MolotovEntity extends ThrowableItemProjectile implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);



    public MolotovEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Override
    protected Item getDefaultItem() {
        return FunItems.MOLOTOV.get();
    }

    @Override
    protected float getGravity() {
        return 0.03f;
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        pResult.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 3.0F);
        double X = pResult.getEntity().getX();
        double Y = pResult.getEntity().getY();
        double Z = pResult.getEntity().getZ();
        int timeonfire = pResult.getEntity().getRemainingFireTicks();
        pResult.getEntity().setRemainingFireTicks(timeonfire+10);
        pResult.getEntity().level().explode(this,X,Y,Z,4.0f, Level.ExplosionInteraction.NONE);
        this.kill();
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        BlockPos hitblock = pResult.getBlockPos();
        double X = hitblock.getX();
        double Y = hitblock.getY();
        double Z = hitblock.getZ();
        this.level().explode(this,X,Y,Z,4.0f, Level.ExplosionInteraction.NONE);
        this.kill();
    }
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(pSource.getEntity() != null){
            double X = pSource.getEntity().getX();
            double Y = pSource.getDirectEntity().getY();
            double Z = pSource.getDirectEntity().getZ();
            pSource.getEntity().level().explode(this, X, Y, Z, 4.0f, Level.ExplosionInteraction.NONE);
            int timeonfire = pSource.getEntity().getRemainingFireTicks();
            pSource.getEntity().setRemainingFireTicks(timeonfire + 10);
            this.kill();
        }
        return true;
    }
    public boolean isPickable() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller",0,this::predicate));

    }

    private PlayState predicate(AnimationState<MolotovEntity> molotovEntityAnimationState) {

            molotovEntityAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.Molly.fly", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}










