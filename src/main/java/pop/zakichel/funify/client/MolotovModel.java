package pop.zakichel.funify.client;


import net.minecraft.resources.ResourceLocation;
import pop.zakichel.funify.Funify;
import pop.zakichel.funify.entity.MolotovEntity;
import software.bernie.geckolib.model.GeoModel;


public class MolotovModel extends GeoModel<MolotovEntity> {

    @Override
    public ResourceLocation getModelResource(MolotovEntity animatable) {
        return new ResourceLocation(Funify.MODID, "geo/molotov.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MolotovEntity animatable) {
        return new ResourceLocation(Funify.MODID, "textures/entity/molotov.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MolotovEntity animatable) {
        return new ResourceLocation(Funify.MODID, "animations/molotov.animation.json");
    }
}
