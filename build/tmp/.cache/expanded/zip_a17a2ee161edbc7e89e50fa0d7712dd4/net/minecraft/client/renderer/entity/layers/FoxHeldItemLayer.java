package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxHeldItemLayer extends RenderLayer<Fox, FoxModel<Fox>> {
   private final ItemInHandRenderer itemInHandRenderer;

   public FoxHeldItemLayer(RenderLayerParent<Fox, FoxModel<Fox>> p_234838_, ItemInHandRenderer p_234839_) {
      super(p_234838_);
      this.itemInHandRenderer = p_234839_;
   }

   public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, Fox pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      boolean flag = pLivingEntity.isSleeping();
      boolean flag1 = pLivingEntity.isBaby();
      pMatrixStack.pushPose();
      if (flag1) {
         float f = 0.75F;
         pMatrixStack.scale(0.75F, 0.75F, 0.75F);
         pMatrixStack.translate(0.0F, 0.5F, 0.209375F);
      }

      pMatrixStack.translate((this.getParentModel()).head.x / 16.0F, (this.getParentModel()).head.y / 16.0F, (this.getParentModel()).head.z / 16.0F);
      float f1 = pLivingEntity.getHeadRollAngle(pPartialTicks);
      pMatrixStack.mulPose(Axis.ZP.rotation(f1));
      pMatrixStack.mulPose(Axis.YP.rotationDegrees(pNetHeadYaw));
      pMatrixStack.mulPose(Axis.XP.rotationDegrees(pHeadPitch));
      if (pLivingEntity.isBaby()) {
         if (flag) {
            pMatrixStack.translate(0.4F, 0.26F, 0.15F);
         } else {
            pMatrixStack.translate(0.06F, 0.26F, -0.5F);
         }
      } else if (flag) {
         pMatrixStack.translate(0.46F, 0.26F, 0.22F);
      } else {
         pMatrixStack.translate(0.06F, 0.27F, -0.5F);
      }

      pMatrixStack.mulPose(Axis.XP.rotationDegrees(90.0F));
      if (flag) {
         pMatrixStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
      }

      ItemStack itemstack = pLivingEntity.getItemBySlot(EquipmentSlot.MAINHAND);
      this.itemInHandRenderer.renderItem(pLivingEntity, itemstack, ItemDisplayContext.GROUND, false, pMatrixStack, pBuffer, pPackedLight);
      pMatrixStack.popPose();
   }
}