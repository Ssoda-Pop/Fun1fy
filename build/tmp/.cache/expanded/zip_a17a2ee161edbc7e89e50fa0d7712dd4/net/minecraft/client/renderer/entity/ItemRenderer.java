package net.minecraft.client.renderer.entity;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRenderer implements ResourceManagerReloadListener {
   public static final ResourceLocation ENCHANTED_GLINT_ENTITY = new ResourceLocation("textures/misc/enchanted_glint_entity.png");
   public static final ResourceLocation ENCHANTED_GLINT_ITEM = new ResourceLocation("textures/misc/enchanted_glint_item.png");
   private static final Set<Item> IGNORED = Sets.newHashSet(Items.AIR);
   public static final int GUI_SLOT_CENTER_X = 8;
   public static final int GUI_SLOT_CENTER_Y = 8;
   public static final int ITEM_COUNT_BLIT_OFFSET = 200;
   public static final float COMPASS_FOIL_UI_SCALE = 0.5F;
   public static final float COMPASS_FOIL_FIRST_PERSON_SCALE = 0.75F;
   public static final float COMPASS_FOIL_TEXTURE_SCALE = 0.0078125F;
   private static final ModelResourceLocation TRIDENT_MODEL = ModelResourceLocation.vanilla("trident", "inventory");
   public static final ModelResourceLocation TRIDENT_IN_HAND_MODEL = ModelResourceLocation.vanilla("trident_in_hand", "inventory");
   private static final ModelResourceLocation SPYGLASS_MODEL = ModelResourceLocation.vanilla("spyglass", "inventory");
   public static final ModelResourceLocation SPYGLASS_IN_HAND_MODEL = ModelResourceLocation.vanilla("spyglass_in_hand", "inventory");
   private final Minecraft minecraft;
   private final ItemModelShaper itemModelShaper;
   private final TextureManager textureManager;
   private final ItemColors itemColors;
   private final BlockEntityWithoutLevelRenderer blockEntityRenderer;

   public ItemRenderer(Minecraft p_266926_, TextureManager p_266774_, ModelManager p_266850_, ItemColors p_267016_, BlockEntityWithoutLevelRenderer p_267049_) {
      this.minecraft = p_266926_;
      this.textureManager = p_266774_;
      this.itemModelShaper = new net.minecraftforge.client.model.ForgeItemModelShaper(p_266850_);
      this.blockEntityRenderer = p_267049_;

      for(Item item : BuiltInRegistries.ITEM) {
         if (!IGNORED.contains(item)) {
            this.itemModelShaper.register(item, new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(item), "inventory"));
         }
      }

      this.itemColors = p_267016_;
   }

   public ItemModelShaper getItemModelShaper() {
      return this.itemModelShaper;
   }

   public void renderModelLists(BakedModel pModel, ItemStack pStack, int pCombinedLight, int pCombinedOverlay, PoseStack pMatrixStack, VertexConsumer pBuffer) {
      RandomSource randomsource = RandomSource.create();
      long i = 42L;

      for(Direction direction : Direction.values()) {
         randomsource.setSeed(42L);
         this.renderQuadList(pMatrixStack, pBuffer, pModel.getQuads((BlockState)null, direction, randomsource), pStack, pCombinedLight, pCombinedOverlay);
      }

      randomsource.setSeed(42L);
      this.renderQuadList(pMatrixStack, pBuffer, pModel.getQuads((BlockState)null, (Direction)null, randomsource), pStack, pCombinedLight, pCombinedOverlay);
   }

   public void render(ItemStack p_115144_, ItemDisplayContext p_270188_, boolean p_115146_, PoseStack p_115147_, MultiBufferSource p_115148_, int p_115149_, int p_115150_, BakedModel p_115151_) {
      if (!p_115144_.isEmpty()) {
         p_115147_.pushPose();
         boolean flag = p_270188_ == ItemDisplayContext.GUI || p_270188_ == ItemDisplayContext.GROUND || p_270188_ == ItemDisplayContext.FIXED;
         if (flag) {
            if (p_115144_.is(Items.TRIDENT)) {
               p_115151_ = this.itemModelShaper.getModelManager().getModel(TRIDENT_MODEL);
            } else if (p_115144_.is(Items.SPYGLASS)) {
               p_115151_ = this.itemModelShaper.getModelManager().getModel(SPYGLASS_MODEL);
            }
         }

         p_115151_ = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(p_115147_, p_115151_, p_270188_, p_115146_);
         p_115147_.translate(-0.5F, -0.5F, -0.5F);
         if (!p_115151_.isCustomRenderer() && (!p_115144_.is(Items.TRIDENT) || flag)) {
            boolean flag1;
            if (p_270188_ != ItemDisplayContext.GUI && !p_270188_.firstPerson() && p_115144_.getItem() instanceof BlockItem) {
               Block block = ((BlockItem)p_115144_.getItem()).getBlock();
               flag1 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
            } else {
               flag1 = true;
            }
            for (var model : p_115151_.getRenderPasses(p_115144_, flag1)) {
            for (var rendertype : model.getRenderTypes(p_115144_, flag1)) {
            VertexConsumer vertexconsumer;
            if (hasAnimatedTexture(p_115144_) && p_115144_.hasFoil()) {
               p_115147_.pushPose();
               PoseStack.Pose posestack$pose = p_115147_.last();
               if (p_270188_ == ItemDisplayContext.GUI) {
                  MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.5F);
               } else if (p_270188_.firstPerson()) {
                  MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.75F);
               }

               if (flag1) {
                  vertexconsumer = getCompassFoilBufferDirect(p_115148_, rendertype, posestack$pose);
               } else {
                  vertexconsumer = getCompassFoilBuffer(p_115148_, rendertype, posestack$pose);
               }

               p_115147_.popPose();
            } else if (flag1) {
               vertexconsumer = getFoilBufferDirect(p_115148_, rendertype, true, p_115144_.hasFoil());
            } else {
               vertexconsumer = getFoilBuffer(p_115148_, rendertype, true, p_115144_.hasFoil());
            }

            this.renderModelLists(model, p_115144_, p_115149_, p_115150_, p_115147_, vertexconsumer);
            }
            }
         } else {
            net.minecraftforge.client.extensions.common.IClientItemExtensions.of(p_115144_).getCustomRenderer().renderByItem(p_115144_, p_270188_, p_115147_, p_115148_, p_115149_, p_115150_);
         }

         p_115147_.popPose();
      }
   }

   private static boolean hasAnimatedTexture(ItemStack p_286353_) {
      return p_286353_.is(ItemTags.COMPASSES) || p_286353_.is(Items.CLOCK);
   }

   public static VertexConsumer getArmorFoilBuffer(MultiBufferSource pBuffer, RenderType pRenderType, boolean pNoEntity, boolean pWithGlint) {
      return pWithGlint ? VertexMultiConsumer.create(pBuffer.getBuffer(pNoEntity ? RenderType.armorGlint() : RenderType.armorEntityGlint()), pBuffer.getBuffer(pRenderType)) : pBuffer.getBuffer(pRenderType);
   }

   public static VertexConsumer getCompassFoilBuffer(MultiBufferSource pBuffer, RenderType pRenderType, PoseStack.Pose pMatrixEntry) {
      return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(pBuffer.getBuffer(RenderType.glint()), pMatrixEntry.pose(), pMatrixEntry.normal(), 0.0078125F), pBuffer.getBuffer(pRenderType));
   }

   public static VertexConsumer getCompassFoilBufferDirect(MultiBufferSource pBuffer, RenderType pRenderType, PoseStack.Pose pMatrixEntry) {
      return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(pBuffer.getBuffer(RenderType.glintDirect()), pMatrixEntry.pose(), pMatrixEntry.normal(), 0.0078125F), pBuffer.getBuffer(pRenderType));
   }

   public static VertexConsumer getFoilBuffer(MultiBufferSource pBuffer, RenderType pRenderType, boolean pIsItem, boolean pGlint) {
      if (pGlint) {
         return Minecraft.useShaderTransparency() && pRenderType == Sheets.translucentItemSheet() ? VertexMultiConsumer.create(pBuffer.getBuffer(RenderType.glintTranslucent()), pBuffer.getBuffer(pRenderType)) : VertexMultiConsumer.create(pBuffer.getBuffer(pIsItem ? RenderType.glint() : RenderType.entityGlint()), pBuffer.getBuffer(pRenderType));
      } else {
         return pBuffer.getBuffer(pRenderType);
      }
   }

   public static VertexConsumer getFoilBufferDirect(MultiBufferSource pBuffer, RenderType pRenderType, boolean pNoEntity, boolean pWithGlint) {
      return pWithGlint ? VertexMultiConsumer.create(pBuffer.getBuffer(pNoEntity ? RenderType.glintDirect() : RenderType.entityGlintDirect()), pBuffer.getBuffer(pRenderType)) : pBuffer.getBuffer(pRenderType);
   }

   public void renderQuadList(PoseStack pPoseStack, VertexConsumer pBuffer, List<BakedQuad> pQuads, ItemStack pItemStack, int pCombinedLight, int pCombinedOverlay) {
      boolean flag = !pItemStack.isEmpty();
      PoseStack.Pose posestack$pose = pPoseStack.last();

      for(BakedQuad bakedquad : pQuads) {
         int i = -1;
         if (flag && bakedquad.isTinted()) {
            i = this.itemColors.getColor(pItemStack, bakedquad.getTintIndex());
         }

         float f = (float)(i >> 16 & 255) / 255.0F;
         float f1 = (float)(i >> 8 & 255) / 255.0F;
         float f2 = (float)(i & 255) / 255.0F;
         pBuffer.putBulkData(posestack$pose, bakedquad, f, f1, f2, 1.0F, pCombinedLight, pCombinedOverlay, true);
      }

   }

   public BakedModel getModel(ItemStack p_174265_, @Nullable Level pLevel, @Nullable LivingEntity p_174267_, int p_174268_) {
      BakedModel bakedmodel;
      if (p_174265_.is(Items.TRIDENT)) {
         bakedmodel = this.itemModelShaper.getModelManager().getModel(TRIDENT_IN_HAND_MODEL);
      } else if (p_174265_.is(Items.SPYGLASS)) {
         bakedmodel = this.itemModelShaper.getModelManager().getModel(SPYGLASS_IN_HAND_MODEL);
      } else {
         bakedmodel = this.itemModelShaper.getItemModel(p_174265_);
      }

      ClientLevel clientlevel = pLevel instanceof ClientLevel ? (ClientLevel)pLevel : null;
      BakedModel bakedmodel1 = bakedmodel.getOverrides().resolve(bakedmodel, p_174265_, clientlevel, p_174267_, p_174268_);
      return bakedmodel1 == null ? this.itemModelShaper.getModelManager().getMissingModel() : bakedmodel1;
   }

   public void renderStatic(ItemStack p_270761_, ItemDisplayContext p_270648_, int p_270410_, int p_270894_, PoseStack p_270430_, MultiBufferSource p_270457_, @Nullable Level p_270149_, int p_270509_) {
      this.renderStatic((LivingEntity)null, p_270761_, p_270648_, false, p_270430_, p_270457_, p_270149_, p_270410_, p_270894_, p_270509_);
   }

   public void renderStatic(@Nullable LivingEntity p_270101_, ItemStack p_270637_, ItemDisplayContext p_270437_, boolean p_270434_, PoseStack p_270230_, MultiBufferSource p_270411_, @Nullable Level p_270641_, int p_270595_, int p_270927_, int p_270845_) {
      if (!p_270637_.isEmpty()) {
         BakedModel bakedmodel = this.getModel(p_270637_, p_270641_, p_270101_, p_270845_);
         this.render(p_270637_, p_270437_, p_270434_, p_270230_, p_270411_, p_270595_, p_270927_, bakedmodel);
      }
   }

   public void onResourceManagerReload(ResourceManager pResourceManager) {
      this.itemModelShaper.rebuildCache();
   }

   public BlockEntityWithoutLevelRenderer getBlockEntityRenderer() {
       return blockEntityRenderer;
   }
}
