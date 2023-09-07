package pop.zakichel.funify.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class MolotovEntity extends AbstractHurtingProjectile  {

    public MolotovEntity(EntityType<? extends AbstractHurtingProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        BlockPos hitblock = pResult.getBlockPos();
        double X = hitblock.getX();
        double Y = hitblock.getY();
        double Z = hitblock.getZ();
        this.level().explode(this,X,Y,Z,4.0f, Level.ExplosionInteraction.NONE);

    }
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(pSource.getEntity() != null){
            double X = pSource.getEntity().getX();
            double Y = pSource.getDirectEntity().getY();
            double Z = pSource.getDirectEntity().getZ();
            pSource.getEntity().level().explode(this, X, Y, Z, 4.0f, Level.ExplosionInteraction.NONE);
            int timeonfire = pSource.getEntity().getRemainingFireTicks();
            pSource.getEntity().setRemainingFireTicks(timeonfire + 10);
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
    
}










