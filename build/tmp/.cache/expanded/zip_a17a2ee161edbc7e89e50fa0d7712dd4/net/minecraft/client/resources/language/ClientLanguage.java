package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientLanguage extends Language {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<String, String> storage;
   private final boolean defaultRightToLeft;

   private ClientLanguage(Map<String, String> pStorage, boolean pDefaultRightToLeft) {
      this.storage = pStorage;
      this.defaultRightToLeft = pDefaultRightToLeft;
   }

   public static ClientLanguage loadFrom(ResourceManager p_265765_, List<String> p_265743_, boolean p_265470_) {
      Map<String, String> map = Maps.newHashMap();

      for(String s : p_265743_) {
         String s1 = String.format(Locale.ROOT, "lang/%s.json", s);

         for(String s2 : p_265765_.getNamespaces()) {
            try {
               ResourceLocation resourcelocation = new ResourceLocation(s2, s1);
               appendFrom(s, p_265765_.getResourceStack(resourcelocation), map);
            } catch (Exception exception) {
               LOGGER.warn("Skipped language file: {}:{} ({})", s2, s1, exception.toString());
            }
         }
      }

      return new ClientLanguage(ImmutableMap.copyOf(map), p_265470_);
   }

   private static void appendFrom(String pLanguageName, List<Resource> pResources, Map<String, String> pDestinationMap) {
      for(Resource resource : pResources) {
         try (InputStream inputstream = resource.open()) {
            Language.loadFromJson(inputstream, pDestinationMap::put);
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to load translations for {} from pack {}", pLanguageName, resource.sourcePackId(), ioexception);
         }
      }

   }

   public String getOrDefault(String p_118920_, String p_265273_) {
      return this.storage.getOrDefault(p_118920_, p_265273_);
   }

   public boolean has(String pId) {
      return this.storage.containsKey(pId);
   }

   public boolean isDefaultRightToLeft() {
      return this.defaultRightToLeft;
   }

   public FormattedCharSequence getVisualOrder(FormattedText pText) {
      return FormattedBidiReorder.reorder(pText, this.defaultRightToLeft);
   }

   @Override
   public Map<String, String> getLanguageData() {
      return storage;
   }
}
