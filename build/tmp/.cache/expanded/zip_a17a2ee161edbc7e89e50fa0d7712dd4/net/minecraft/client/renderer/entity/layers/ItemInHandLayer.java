package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemInHandLayer<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M> {
   private final ItemInHandRenderer itemInHandRenderer;

   public ItemInHandLayer(RenderLayerParent<T, M> p_234846_, ItemInHandRenderer p_234847_) {
      super(p_234846_);
      this.itemInHandRenderer = p_234847_;
   }

   public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      boolean flag = pLivingEntity.getMainArm() == HumanoidArm.RIGHT;
      ItemStack itemstack = flag ? pLivingEntity.getOffhandItem() : pLivingEntity.getMainHandItem();
      ItemStack itemstack1 = flag ? pLivingEntity.getMainHandItem() : pLivingEntity.getOffhandItem();
      if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
         pMatrixStack.pushPose();
         if (this.getParentModel().young) {
            float f = 0.5F;
            pMatrixStack.translate(0.0F, 0.75F, 0.0F);
            pMatrixStack.scale(0.5F, 0.5F, 0.5F);
         }

         this.renderArmWithItem(pLivingEntity, itemstack1, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, pMatrixStack, pBuffer, pPackedLight);
         this.renderArmWithItem(pLivingEntity, itemstack, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, pMatrixStack, pBuffer, pPackedLight);
         pMatrixStack.popPose();
      }
   }

   protected void renderArmWithItem(LivingEntity p_117185_, ItemStack p_117186_, ItemDisplayContext p_270970_, HumanoidArm p_117188_, PoseStack p_117189_, MultiBufferSource p_117190_, int p_117191_) {
      if (!p_117186_.isEmpty()) {
         p_117189_.pushPose();
         this.getParentModel().translateToHand(p_117188_, p_117189_);
         p_117189_.mulPose(Axis.XP.rotationDegrees(-90.0F));
         p_117189_.mulPose(Axis.YP.rotationDegrees(180.0F));
         boolean flag = p_117188_ == HumanoidArm.LEFT;
         p_117189_.translate((float)(flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
         this.itemInHandRenderer.renderItem(p_117185_, p_117186_, p_270970_, flag, p_117189_, p_117190_, p_117191_);
         p_117189_.popPose();
      }
   }
}