package pop.zakichel.funify.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import pop.zakichel.funify.Funify;
import pop.zakichel.funify.client.MolotovModel;
import pop.zakichel.funify.entity.MolotovEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MolotovRenderer extends GeoEntityRenderer<MolotovEntity> {
    public MolotovRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MolotovModel());
    }

    @Override
    public ResourceLocation getTextureLocation(MolotovEntity animatable) {
        return new ResourceLocation(Funify.MODID,"textures/entity/molotov.png");
    }

}
