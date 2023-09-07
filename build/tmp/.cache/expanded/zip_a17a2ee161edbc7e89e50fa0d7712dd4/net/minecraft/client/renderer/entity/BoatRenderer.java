package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ChestRaftModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class BoatRenderer extends EntityRenderer<Boat> {
   private final Map<Boat.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

   public BoatRenderer(EntityRendererProvider.Context p_234563_, boolean p_234564_) {
      super(p_234563_);
      this.shadowRadius = 0.8F;
      this.boatResources = Stream.of(Boat.Type.values()).collect(ImmutableMap.toImmutableMap((p_173938_) -> {
         return p_173938_;
      }, (p_247941_) -> {
         return Pair.of(new ResourceLocation(getTextureLocation(p_247941_, p_234564_)), this.createBoatModel(p_234563_, p_247941_, p_234564_));
      }));
   }

   private ListModel<Boat> createBoatModel(EntityRendererProvider.Context p_248834_, Boat.Type p_249317_, boolean p_250093_) {
      ModelLayerLocation modellayerlocation = p_250093_ ? ModelLayers.createChestBoatModelName(p_249317_) : ModelLayers.createBoatModelName(p_249317_);
      ModelPart modelpart = p_248834_.bakeLayer(modellayerlocation);
      if (p_249317_ == Boat.Type.BAMBOO) {
         return (ListModel<Boat>)(p_250093_ ? new ChestRaftModel(modelpart) : new RaftModel(modelpart));
      } else {
         return (ListModel<Boat>)(p_250093_ ? new ChestBoatModel(modelpart) : new BoatModel(modelpart));
      }
   }

   private static String getTextureLocation(Boat.Type p_234566_, boolean p_234567_) {
      return p_234567_ ? "textures/entity/chest_boat/" + p_234566_.getName() + ".png" : "textures/entity/boat/" + p_234566_.getName() + ".png";
   }

   public void render(Boat pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      pMatrixStack.pushPose();
      pMatrixStack.translate(0.0F, 0.375F, 0.0F);
      pMatrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - pEntityYaw));
      float f = (float)pEntity.getHurtTime() - pPartialTicks;
      float f1 = pEntity.getDamage() - pPartialTicks;
      if (f1 < 0.0F) {
         f1 = 0.0F;
      }

      if (f > 0.0F) {
         pMatrixStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float)pEntity.getHurtDir()));
      }

      float f2 = pEntity.getBubbleAngle(pPartialTicks);
      if (!Mth.equal(f2, 0.0F)) {
         pMatrixStack.mulPose((new Quaternionf()).setAngleAxis(pEntity.getBubbleAngle(pPartialTicks) * ((float)Math.PI / 180F), 1.0F, 0.0F, 1.0F));
      }

      Pair<ResourceLocation, ListModel<Boat>> pair = getModelWithLocation(pEntity);
      ResourceLocation resourcelocation = pair.getFirst();
      ListModel<Boat> listmodel = pair.getSecond();
      pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
      pMatrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));
      listmodel.setupAnim(pEntity, pPartialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
      VertexConsumer vertexconsumer = pBuffer.getBuffer(listmodel.renderType(resourcelocation));
      listmodel.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      if (!pEntity.isUnderWater()) {
         VertexConsumer vertexconsumer1 = pBuffer.getBuffer(RenderType.waterMask());
         if (listmodel instanceof WaterPatchModel) {
            WaterPatchModel waterpatchmodel = (WaterPatchModel)listmodel;
            waterpatchmodel.waterPatch().render(pMatrixStack, vertexconsumer1, pPackedLight, OverlayTexture.NO_OVERLAY);
         }
      }

      pMatrixStack.popPose();
      super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
   }

   /**
    * Returns the location of an entity's texture.
    */
   @Deprecated // forge: override getModelWithLocation to change the texture / model
   public ResourceLocation getTextureLocation(Boat pEntity) {
      return getModelWithLocation(pEntity).getFirst();
   }

   public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) { return this.boatResources.get(boat.getVariant()); }
}