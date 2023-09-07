package pop.zakichel.funify.client.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import pop.zakichel.funify.entity.MolotovEntity;

public class MolotovRenderer extends EntityRenderer<MolotovEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("models/entity/molotov.json");

    public MolotovRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(MolotovEntity pEntity) {
        return TEXTURE;
    }

}
