package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringDecomposer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class Font implements net.minecraftforge.client.extensions.IForgeFont {
   private static final float EFFECT_DEPTH = 0.01F;
   private static final Vector3f SHADOW_OFFSET = new Vector3f(0.0F, 0.0F, 0.03F);
   public static final int ALPHA_CUTOFF = 8;
   public final int lineHeight = 9;
   public final RandomSource random = RandomSource.create();
   private final Function<ResourceLocation, FontSet> fonts;
   final boolean filterFishyGlyphs;
   private final StringSplitter splitter;

   public Font(Function<ResourceLocation, FontSet> pFonts, boolean pFilterFishyGlyphs) {
      this.fonts = pFonts;
      this.filterFishyGlyphs = pFilterFishyGlyphs;
      this.splitter = new StringSplitter((p_92722_, p_92723_) -> {
         return this.getFontSet(p_92723_.getFont()).getGlyphInfo(p_92722_, this.filterFishyGlyphs).getAdvance(p_92723_.isBold());
      });
   }

   FontSet getFontSet(ResourceLocation pFontLocation) {
      return this.fonts.apply(pFontLocation);
   }

   /**
    * Apply Unicode Bidirectional Algorithm to string and return a new possibly reordered string for visual rendering.
    */
   public String bidirectionalShaping(String pText) {
      try {
         Bidi bidi = new Bidi((new ArabicShaping(8)).shape(pText), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException arabicshapingexception) {
         return pText;
      }
   }

   public int drawInBatch(String p_272751_, float p_272661_, float p_273129_, int p_273272_, boolean p_273209_, Matrix4f p_272940_, MultiBufferSource p_273017_, Font.DisplayMode p_272608_, int p_273365_, int p_272755_) {
      return this.drawInBatch(p_272751_, p_272661_, p_273129_, p_273272_, p_273209_, p_272940_, p_273017_, p_272608_, p_273365_, p_272755_, this.isBidirectional());
   }

   public int drawInBatch(String p_272780_, float p_272811_, float p_272610_, int p_273422_, boolean p_273016_, Matrix4f p_273443_, MultiBufferSource p_273387_, Font.DisplayMode p_273551_, int p_272706_, int p_273114_, boolean p_273022_) {
      return this.drawInternal(p_272780_, p_272811_, p_272610_, p_273422_, p_273016_, p_273443_, p_273387_, p_273551_, p_272706_, p_273114_, p_273022_);
   }

   public int drawInBatch(Component p_273032_, float p_273249_, float p_273594_, int p_273714_, boolean p_273050_, Matrix4f p_272974_, MultiBufferSource p_273695_, Font.DisplayMode p_272782_, int p_272603_, int p_273632_) {
      return this.drawInBatch(p_273032_.getVisualOrderText(), p_273249_, p_273594_, p_273714_, p_273050_, p_272974_, p_273695_, p_272782_, p_272603_, p_273632_);
   }

   public int drawInBatch(FormattedCharSequence p_273262_, float p_273006_, float p_273254_, int p_273375_, boolean p_273674_, Matrix4f p_273525_, MultiBufferSource p_272624_, Font.DisplayMode p_273418_, int p_273330_, int p_272981_) {
      return this.drawInternal(p_273262_, p_273006_, p_273254_, p_273375_, p_273674_, p_273525_, p_272624_, p_273418_, p_273330_, p_272981_);
   }

   public void drawInBatch8xOutline(FormattedCharSequence p_168646_, float p_168647_, float p_168648_, int p_168649_, int p_168650_, Matrix4f p_254170_, MultiBufferSource p_168652_, int p_168653_) {
      int i = adjustColor(p_168650_);
      Font.StringRenderOutput font$stringrenderoutput = new Font.StringRenderOutput(p_168652_, 0.0F, 0.0F, i, false, p_254170_, Font.DisplayMode.NORMAL, p_168653_);

      for(int j = -1; j <= 1; ++j) {
         for(int k = -1; k <= 1; ++k) {
            if (j != 0 || k != 0) {
               float[] afloat = new float[]{p_168647_};
               int l = j;
               int i1 = k;
               p_168646_.accept((p_168661_, p_168662_, p_168663_) -> {
                  boolean flag = p_168662_.isBold();
                  FontSet fontset = this.getFontSet(p_168662_.getFont());
                  GlyphInfo glyphinfo = fontset.getGlyphInfo(p_168663_, this.filterFishyGlyphs);
                  font$stringrenderoutput.x = afloat[0] + (float)l * glyphinfo.getShadowOffset();
                  font$stringrenderoutput.y = p_168648_ + (float)i1 * glyphinfo.getShadowOffset();
                  afloat[0] += glyphinfo.getAdvance(flag);
                  return font$stringrenderoutput.accept(p_168661_, p_168662_.withColor(i), p_168663_);
               });
            }
         }
      }

      Font.StringRenderOutput font$stringrenderoutput1 = new Font.StringRenderOutput(p_168652_, p_168647_, p_168648_, adjustColor(p_168649_), false, p_254170_, Font.DisplayMode.POLYGON_OFFSET, p_168653_);
      p_168646_.accept(font$stringrenderoutput1);
      font$stringrenderoutput1.finish(0, p_168647_);
   }

   private static int adjustColor(int pColor) {
      return (pColor & -67108864) == 0 ? pColor | -16777216 : pColor;
   }

   private int drawInternal(String p_273658_, float p_273086_, float p_272883_, int p_273547_, boolean p_272778_, Matrix4f p_272662_, MultiBufferSource p_273012_, Font.DisplayMode p_273381_, int p_272855_, int p_272745_, boolean p_272785_) {
      if (p_272785_) {
         p_273658_ = this.bidirectionalShaping(p_273658_);
      }

      p_273547_ = adjustColor(p_273547_);
      Matrix4f matrix4f = new Matrix4f(p_272662_);
      if (p_272778_) {
         this.renderText(p_273658_, p_273086_, p_272883_, p_273547_, true, p_272662_, p_273012_, p_273381_, p_272855_, p_272745_);
         matrix4f.translate(SHADOW_OFFSET);
      }

      p_273086_ = this.renderText(p_273658_, p_273086_, p_272883_, p_273547_, false, matrix4f, p_273012_, p_273381_, p_272855_, p_272745_);
      return (int)p_273086_ + (p_272778_ ? 1 : 0);
   }

   private int drawInternal(FormattedCharSequence p_273025_, float p_273121_, float p_272717_, int p_273653_, boolean p_273531_, Matrix4f p_273265_, MultiBufferSource p_273560_, Font.DisplayMode p_273342_, int p_273373_, int p_273266_) {
      p_273653_ = adjustColor(p_273653_);
      Matrix4f matrix4f = new Matrix4f(p_273265_);
      if (p_273531_) {
         this.renderText(p_273025_, p_273121_, p_272717_, p_273653_, true, p_273265_, p_273560_, p_273342_, p_273373_, p_273266_);
         matrix4f.translate(SHADOW_OFFSET);
      }

      p_273121_ = this.renderText(p_273025_, p_273121_, p_272717_, p_273653_, false, matrix4f, p_273560_, p_273342_, p_273373_, p_273266_);
      return (int)p_273121_ + (p_273531_ ? 1 : 0);
   }

   private float renderText(String p_273765_, float p_273532_, float p_272783_, int p_273217_, boolean p_273583_, Matrix4f p_272734_, MultiBufferSource p_272595_, Font.DisplayMode p_273610_, int p_273727_, int p_273199_) {
      Font.StringRenderOutput font$stringrenderoutput = new Font.StringRenderOutput(p_272595_, p_273532_, p_272783_, p_273217_, p_273583_, p_272734_, p_273610_, p_273199_);
      StringDecomposer.iterateFormatted(p_273765_, Style.EMPTY, font$stringrenderoutput);
      return font$stringrenderoutput.finish(p_273727_, p_273532_);
   }

   private float renderText(FormattedCharSequence p_273322_, float p_272632_, float p_273541_, int p_273200_, boolean p_273312_, Matrix4f p_273276_, MultiBufferSource p_273392_, Font.DisplayMode p_272625_, int p_273774_, int p_273371_) {
      Font.StringRenderOutput font$stringrenderoutput = new Font.StringRenderOutput(p_273392_, p_272632_, p_273541_, p_273200_, p_273312_, p_273276_, p_272625_, p_273371_);
      p_273322_.accept(font$stringrenderoutput);
      return font$stringrenderoutput.finish(p_273774_, p_272632_);
   }

   void renderChar(BakedGlyph p_254105_, boolean p_254001_, boolean p_254262_, float p_254256_, float p_253753_, float p_253629_, Matrix4f p_254014_, VertexConsumer p_253852_, float p_254317_, float p_253809_, float p_253870_, float p_254287_, int p_253905_) {
      p_254105_.render(p_254262_, p_253753_, p_253629_, p_254014_, p_253852_, p_254317_, p_253809_, p_253870_, p_254287_, p_253905_);
      if (p_254001_) {
         p_254105_.render(p_254262_, p_253753_ + p_254256_, p_253629_, p_254014_, p_253852_, p_254317_, p_253809_, p_253870_, p_254287_, p_253905_);
      }

   }

   /**
    * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
    */
   public int width(String pText) {
      return Mth.ceil(this.splitter.stringWidth(pText));
   }

   public int width(FormattedText pText) {
      return Mth.ceil(this.splitter.stringWidth(pText));
   }

   public int width(FormattedCharSequence pText) {
      return Mth.ceil(this.splitter.stringWidth(pText));
   }

   public String plainSubstrByWidth(String pText, int pMaxWidth, boolean pTail) {
      return pTail ? this.splitter.plainTailByWidth(pText, pMaxWidth, Style.EMPTY) : this.splitter.plainHeadByWidth(pText, pMaxWidth, Style.EMPTY);
   }

   public String plainSubstrByWidth(String pText, int pMaxWidth) {
      return this.splitter.plainHeadByWidth(pText, pMaxWidth, Style.EMPTY);
   }

   public FormattedText substrByWidth(FormattedText pText, int pMaxWidth) {
      return this.splitter.headByWidth(pText, pMaxWidth, Style.EMPTY);
   }

   /**
    * Returns the height (in pixels) of the given string if it is wordwrapped to the given max width.
    */
   public int wordWrapHeight(String pStr, int pMaxWidth) {
      return 9 * this.splitter.splitLines(pStr, pMaxWidth, Style.EMPTY).size();
   }

   public int wordWrapHeight(FormattedText p_239134_, int p_239135_) {
      return 9 * this.splitter.splitLines(p_239134_, p_239135_, Style.EMPTY).size();
   }

   public List<FormattedCharSequence> split(FormattedText pText, int pMaxWidth) {
      return Language.getInstance().getVisualOrder(this.splitter.splitLines(pText, pMaxWidth, Style.EMPTY));
   }

   /**
    * Get bidiFlag that controls if the Unicode Bidirectional Algorithm should be run before rendering any string
    */
   public boolean isBidirectional() {
      return Language.getInstance().isDefaultRightToLeft();
   }

   public StringSplitter getSplitter() {
      return this.splitter;
   }

   @Override public Font self() { return this; }

   @OnlyIn(Dist.CLIENT)
   public static enum DisplayMode {
      NORMAL,
      SEE_THROUGH,
      POLYGON_OFFSET;
   }

   @OnlyIn(Dist.CLIENT)
   class StringRenderOutput implements FormattedCharSink {
      final MultiBufferSource bufferSource;
      private final boolean dropShadow;
      private final float dimFactor;
      private final float r;
      private final float g;
      private final float b;
      private final float a;
      private final Matrix4f pose;
      private final Font.DisplayMode mode;
      private final int packedLightCoords;
      float x;
      float y;
      @Nullable
      private List<BakedGlyph.Effect> effects;

      private void addEffect(BakedGlyph.Effect pEffect) {
         if (this.effects == null) {
            this.effects = Lists.newArrayList();
         }

         this.effects.add(pEffect);
      }

      public StringRenderOutput(MultiBufferSource p_181365_, float p_181366_, float p_181367_, int p_181368_, boolean p_181369_, Matrix4f p_254510_, Font.DisplayMode p_181371_, int p_181372_) {
         this.bufferSource = p_181365_;
         this.x = p_181366_;
         this.y = p_181367_;
         this.dropShadow = p_181369_;
         this.dimFactor = p_181369_ ? 0.25F : 1.0F;
         this.r = (float)(p_181368_ >> 16 & 255) / 255.0F * this.dimFactor;
         this.g = (float)(p_181368_ >> 8 & 255) / 255.0F * this.dimFactor;
         this.b = (float)(p_181368_ & 255) / 255.0F * this.dimFactor;
         this.a = (float)(p_181368_ >> 24 & 255) / 255.0F;
         this.pose = p_254510_;
         this.mode = p_181371_;
         this.packedLightCoords = p_181372_;
      }

      /**
       * Accepts a single code point from from a {@link net.minecraft.util.FormattedCharSequence}.
       * @return {@code true} to accept more characters, {@code false} to stop traversing the sequence.
       * @param pPositionInCurrentSequence Contains the relative position of the character in the current sub-sequence.
       * If multiple formatted char sequences have been combined, this value will reset to {@code 0} after each sequence
       * has been fully consumed.
       */
      public boolean accept(int pPositionInCurrentSequence, Style pStyle, int pCodePoint) {
         FontSet fontset = Font.this.getFontSet(pStyle.getFont());
         GlyphInfo glyphinfo = fontset.getGlyphInfo(pCodePoint, Font.this.filterFishyGlyphs);
         BakedGlyph bakedglyph = pStyle.isObfuscated() && pCodePoint != 32 ? fontset.getRandomGlyph(glyphinfo) : fontset.getGlyph(pCodePoint);
         boolean flag = pStyle.isBold();
         float f3 = this.a;
         TextColor textcolor = pStyle.getColor();
         float f;
         float f1;
         float f2;
         if (textcolor != null) {
            int i = textcolor.getValue();
            f = (float)(i >> 16 & 255) / 255.0F * this.dimFactor;
            f1 = (float)(i >> 8 & 255) / 255.0F * this.dimFactor;
            f2 = (float)(i & 255) / 255.0F * this.dimFactor;
         } else {
            f = this.r;
            f1 = this.g;
            f2 = this.b;
         }

         if (!(bakedglyph instanceof EmptyGlyph)) {
            float f5 = flag ? glyphinfo.getBoldOffset() : 0.0F;
            float f4 = this.dropShadow ? glyphinfo.getShadowOffset() : 0.0F;
            VertexConsumer vertexconsumer = this.bufferSource.getBuffer(bakedglyph.renderType(this.mode));
            Font.this.renderChar(bakedglyph, flag, pStyle.isItalic(), f5, this.x + f4, this.y + f4, this.pose, vertexconsumer, f, f1, f2, f3, this.packedLightCoords);
         }

         float f6 = glyphinfo.getAdvance(flag);
         float f7 = this.dropShadow ? 1.0F : 0.0F;
         if (pStyle.isStrikethrough()) {
            this.addEffect(new BakedGlyph.Effect(this.x + f7 - 1.0F, this.y + f7 + 4.5F, this.x + f7 + f6, this.y + f7 + 4.5F - 1.0F, 0.01F, f, f1, f2, f3));
         }

         if (pStyle.isUnderlined()) {
            this.addEffect(new BakedGlyph.Effect(this.x + f7 - 1.0F, this.y + f7 + 9.0F, this.x + f7 + f6, this.y + f7 + 9.0F - 1.0F, 0.01F, f, f1, f2, f3));
         }

         this.x += f6;
         return true;
      }

      public float finish(int pBackgroundColor, float pX) {
         if (pBackgroundColor != 0) {
            float f = (float)(pBackgroundColor >> 24 & 255) / 255.0F;
            float f1 = (float)(pBackgroundColor >> 16 & 255) / 255.0F;
            float f2 = (float)(pBackgroundColor >> 8 & 255) / 255.0F;
            float f3 = (float)(pBackgroundColor & 255) / 255.0F;
            this.addEffect(new BakedGlyph.Effect(pX - 1.0F, this.y + 9.0F, this.x + 1.0F, this.y - 1.0F, 0.01F, f1, f2, f3, f));
         }

         if (this.effects != null) {
            BakedGlyph bakedglyph = Font.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
            VertexConsumer vertexconsumer = this.bufferSource.getBuffer(bakedglyph.renderType(this.mode));

            for(BakedGlyph.Effect bakedglyph$effect : this.effects) {
               bakedglyph.renderEffect(bakedglyph$effect, this.pose, vertexconsumer, this.packedLightCoords);
            }
         }

         return this.x;
      }
   }
}
