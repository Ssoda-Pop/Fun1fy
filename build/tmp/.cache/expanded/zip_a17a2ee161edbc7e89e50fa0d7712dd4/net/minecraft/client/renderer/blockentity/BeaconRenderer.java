package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class BeaconRenderer implements BlockEntityRenderer<BeaconBlockEntity> {
   public static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");
   public static final int MAX_RENDER_Y = 1024;

   public BeaconRenderer(BlockEntityRendererProvider.Context pContext) {
   }

   public void render(BeaconBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
      long i = pBlockEntity.getLevel().getGameTime();
      List<BeaconBlockEntity.BeaconBeamSection> list = pBlockEntity.getBeamSections();
      int j = 0;

      for(int k = 0; k < list.size(); ++k) {
         BeaconBlockEntity.BeaconBeamSection beaconblockentity$beaconbeamsection = list.get(k);
         renderBeaconBeam(pPoseStack, pBufferSource, pPartialTick, i, j, k == list.size() - 1 ? 1024 : beaconblockentity$beaconbeamsection.getHeight(), beaconblockentity$beaconbeamsection.getColor());
         j += beaconblockentity$beaconbeamsection.getHeight();
      }

   }

   private static void renderBeaconBeam(PoseStack pPoseStack, MultiBufferSource pBufferSource, float pPartialTick, long pGameTime, int pYOffset, int pHeight, float[] pColors) {
      renderBeaconBeam(pPoseStack, pBufferSource, BEAM_LOCATION, pPartialTick, 1.0F, pGameTime, pYOffset, pHeight, pColors, 0.2F, 0.25F);
   }

   public static void renderBeaconBeam(PoseStack pPoseStack, MultiBufferSource pBufferSource, ResourceLocation pBeamLocation, float pPartialTick, float pTextureScale, long pGameTime, int pYOffset, int pHeight, float[] pColors, float pBeamRadius, float pGlowRadius) {
      int i = pYOffset + pHeight;
      pPoseStack.pushPose();
      pPoseStack.translate(0.5D, 0.0D, 0.5D);
      float f = (float)Math.floorMod(pGameTime, 40) + pPartialTick;
      float f1 = pHeight < 0 ? f : -f;
      float f2 = Mth.frac(f1 * 0.2F - (float)Mth.floor(f1 * 0.1F));
      float f3 = pColors[0];
      float f4 = pColors[1];
      float f5 = pColors[2];
      pPoseStack.pushPose();
      pPoseStack.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));
      float f6 = 0.0F;
      float f8 = 0.0F;
      float f9 = -pBeamRadius;
      float f10 = 0.0F;
      float f11 = 0.0F;
      float f12 = -pBeamRadius;
      float f13 = 0.0F;
      float f14 = 1.0F;
      float f15 = -1.0F + f2;
      float f16 = (float)pHeight * pTextureScale * (0.5F / pBeamRadius) + f15;
      renderPart(pPoseStack, pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, false)), f3, f4, f5, 1.0F, pYOffset, i, 0.0F, pBeamRadius, pBeamRadius, 0.0F, f9, 0.0F, 0.0F, f12, 0.0F, 1.0F, f16, f15);
      pPoseStack.popPose();
      f6 = -pGlowRadius;
      float f7 = -pGlowRadius;
      f8 = -pGlowRadius;
      f9 = -pGlowRadius;
      f13 = 0.0F;
      f14 = 1.0F;
      f15 = -1.0F + f2;
      f16 = (float)pHeight * pTextureScale + f15;
      renderPart(pPoseStack, pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, true)), f3, f4, f5, 0.125F, pYOffset, i, f6, f7, pGlowRadius, f8, f9, pGlowRadius, pGlowRadius, pGlowRadius, 0.0F, 1.0F, f16, f15);
      pPoseStack.popPose();
   }

   private static void renderPart(PoseStack pPoseStack, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, int pMinY, int pMaxY, float pX0, float pZ0, float pX1, float pZ1, float pX2, float pZ2, float pX3, float pZ3, float pMinU, float pMaxU, float pMinV, float pMaxV) {
      PoseStack.Pose posestack$pose = pPoseStack.last();
      Matrix4f matrix4f = posestack$pose.pose();
      Matrix3f matrix3f = posestack$pose.normal();
      renderQuad(matrix4f, matrix3f, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX0, pZ0, pX1, pZ1, pMinU, pMaxU, pMinV, pMaxV);
      renderQuad(matrix4f, matrix3f, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX3, pZ3, pX2, pZ2, pMinU, pMaxU, pMinV, pMaxV);
      renderQuad(matrix4f, matrix3f, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX1, pZ1, pX3, pZ3, pMinU, pMaxU, pMinV, pMaxV);
      renderQuad(matrix4f, matrix3f, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX2, pZ2, pX0, pZ0, pMinU, pMaxU, pMinV, pMaxV);
   }

   private static void renderQuad(Matrix4f p_253960_, Matrix3f p_254005_, VertexConsumer p_112122_, float p_112123_, float p_112124_, float p_112125_, float p_112126_, int p_112127_, int p_112128_, float p_112129_, float p_112130_, float p_112131_, float p_112132_, float p_112133_, float p_112134_, float p_112135_, float p_112136_) {
      addVertex(p_253960_, p_254005_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112128_, p_112129_, p_112130_, p_112134_, p_112135_);
      addVertex(p_253960_, p_254005_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112127_, p_112129_, p_112130_, p_112134_, p_112136_);
      addVertex(p_253960_, p_254005_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112127_, p_112131_, p_112132_, p_112133_, p_112136_);
      addVertex(p_253960_, p_254005_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112128_, p_112131_, p_112132_, p_112133_, p_112135_);
   }

   private static void addVertex(Matrix4f p_253955_, Matrix3f p_253713_, VertexConsumer p_253894_, float p_253871_, float p_253841_, float p_254568_, float p_254361_, int p_254357_, float p_254451_, float p_254240_, float p_254117_, float p_253698_) {
      p_253894_.vertex(p_253955_, p_254451_, (float)p_254357_, p_254240_).color(p_253871_, p_253841_, p_254568_, p_254361_).uv(p_254117_, p_253698_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p_253713_, 0.0F, 1.0F, 0.0F).endVertex();
   }

   public boolean shouldRenderOffScreen(BeaconBlockEntity pBlockEntity) {
      return true;
   }

   public int getViewDistance() {
      return 256;
   }

   public boolean shouldRender(BeaconBlockEntity pBlockEntity, Vec3 pCameraPos) {
      return Vec3.atCenterOf(pBlockEntity.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(pCameraPos.multiply(1.0D, 0.0D, 1.0D), (double)this.getViewDistance());
   }
}