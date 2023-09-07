package net.minecraft.util;

public class FastColor {
   public static class ABGR32 {
      public static int alpha(int p_267257_) {
         return p_267257_ >>> 24;
      }

      public static int red(int p_267160_) {
         return p_267160_ & 255;
      }

      public static int green(int p_266784_) {
         return p_266784_ >> 8 & 255;
      }

      public static int blue(int p_267087_) {
         return p_267087_ >> 16 & 255;
      }

      public static int transparent(int p_267248_) {
         return p_267248_ & 16777215;
      }

      public static int opaque(int p_268288_) {
         return p_268288_ | -16777216;
      }

      public static int color(int p_267196_, int p_266895_, int p_266779_, int p_267206_) {
         return p_267196_ << 24 | p_266895_ << 16 | p_266779_ << 8 | p_267206_;
      }

      public static int color(int p_267230_, int p_266708_) {
         return p_267230_ << 24 | p_266708_ & 16777215;
      }
   }

   public static class ARGB32 {
      public static int alpha(int pPackedColor) {
         return pPackedColor >>> 24;
      }

      public static int red(int pPackedColor) {
         return pPackedColor >> 16 & 255;
      }

      public static int green(int pPackedColor) {
         return pPackedColor >> 8 & 255;
      }

      public static int blue(int pPackedColor) {
         return pPackedColor & 255;
      }

      public static int color(int pAlpha, int pRed, int pGreen, int pBlue) {
         return pAlpha << 24 | pRed << 16 | pGreen << 8 | pBlue;
      }

      public static int multiply(int pPackedColourOne, int pPackedColorTwo) {
         return color(alpha(pPackedColourOne) * alpha(pPackedColorTwo) / 255, red(pPackedColourOne) * red(pPackedColorTwo) / 255, green(pPackedColourOne) * green(pPackedColorTwo) / 255, blue(pPackedColourOne) * blue(pPackedColorTwo) / 255);
      }

      public static int lerp(float p_270972_, int p_270081_, int p_270150_) {
         int i = Mth.lerpInt(p_270972_, alpha(p_270081_), alpha(p_270150_));
         int j = Mth.lerpInt(p_270972_, red(p_270081_), red(p_270150_));
         int k = Mth.lerpInt(p_270972_, green(p_270081_), green(p_270150_));
         int l = Mth.lerpInt(p_270972_, blue(p_270081_), blue(p_270150_));
         return color(i, j, k, l);
      }
   }
}