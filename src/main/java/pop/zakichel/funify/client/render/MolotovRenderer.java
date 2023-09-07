package pop.zakichel.funify.client.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import pop.zakichel.funify.entity.FunEntities;
import pop.zakichel.funify.entity.MolotovEntity;

import javax.swing.*;

public class MolotovRenderer extends EntityRenderer<MolotovEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("models/entity/molotov.json");
    public static final ListModel<MolotovEntity> MODEL = getModelWithLocation(FunEntities.THROWN_MOLOTOV.get());
    public MolotovRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(MolotovEntity pEntity) {
        return TEXTURE;
    }

}
