package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.Backup;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsBackupInfoScreen extends RealmsScreen {
   private static final Component UNKNOWN = Component.translatable("mco.backup.unknown");
   private final Screen lastScreen;
   final Backup backup;
   private RealmsBackupInfoScreen.BackupInfoList backupInfoList;

   public RealmsBackupInfoScreen(Screen pLastScreen, Backup pBackup) {
      super(Component.translatable("mco.backup.info.title"));
      this.lastScreen = pLastScreen;
      this.backup = pBackup;
   }

   public void tick() {
   }

   public void init() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_280689_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20).build());
      this.backupInfoList = new RealmsBackupInfoScreen.BackupInfoList(this.minecraft);
      this.addWidget(this.backupInfoList);
      this.magicalSpecialHackyFocus(this.backupInfoList);
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (pKeyCode == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(pKeyCode, pScanCode, pModifiers);
      }
   }

   public void render(GuiGraphics p_282729_, int p_282525_, int p_281883_, float p_281644_) {
      this.renderBackground(p_282729_);
      this.backupInfoList.render(p_282729_, p_282525_, p_281883_, p_281644_);
      p_282729_.drawCenteredString(this.font, this.title, this.width / 2, 10, 16777215);
      super.render(p_282729_, p_282525_, p_281883_, p_281644_);
   }

   Component checkForSpecificMetadata(String pKey, String pValue) {
      String s = pKey.toLowerCase(Locale.ROOT);
      if (s.contains("game") && s.contains("mode")) {
         return this.gameModeMetadata(pValue);
      } else {
         return (Component)(s.contains("game") && s.contains("difficulty") ? this.gameDifficultyMetadata(pValue) : Component.literal(pValue));
      }
   }

   private Component gameDifficultyMetadata(String pValue) {
      try {
         return RealmsSlotOptionsScreen.DIFFICULTIES.get(Integer.parseInt(pValue)).getDisplayName();
      } catch (Exception exception) {
         return UNKNOWN;
      }
   }

   private Component gameModeMetadata(String pValue) {
      try {
         return RealmsSlotOptionsScreen.GAME_MODES.get(Integer.parseInt(pValue)).getShortDisplayName();
      } catch (Exception exception) {
         return UNKNOWN;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupInfoList extends ObjectSelectionList<RealmsBackupInfoScreen.BackupInfoListEntry> {
      public BackupInfoList(Minecraft pMinecraft) {
         super(pMinecraft, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
         this.setRenderSelection(false);
         if (RealmsBackupInfoScreen.this.backup.changeList != null) {
            RealmsBackupInfoScreen.this.backup.changeList.forEach((p_88084_, p_88085_) -> {
               this.addEntry(RealmsBackupInfoScreen.this.new BackupInfoListEntry(p_88084_, p_88085_));
            });
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupInfoListEntry extends ObjectSelectionList.Entry<RealmsBackupInfoScreen.BackupInfoListEntry> {
      private static final Component TEMPLATE_NAME = Component.translatable("mco.backup.entry.templateName");
      private static final Component GAME_DIFFICULTY = Component.translatable("mco.backup.entry.gameDifficulty");
      private static final Component NAME = Component.translatable("mco.backup.entry.name");
      private static final Component GAME_SERVER_VERSION = Component.translatable("mco.backup.entry.gameServerVersion");
      private static final Component UPLOADED = Component.translatable("mco.backup.entry.uploaded");
      private static final Component ENABLED_PACK = Component.translatable("mco.backup.entry.enabledPack");
      private static final Component DESCRIPTION = Component.translatable("mco.backup.entry.description");
      private static final Component GAME_MODE = Component.translatable("mco.backup.entry.gameMode");
      private static final Component SEED = Component.translatable("mco.backup.entry.seed");
      private static final Component WORLD_TYPE = Component.translatable("mco.backup.entry.worldType");
      private static final Component UNDEFINED = Component.translatable("mco.backup.entry.undefined");
      private final String key;
      private final String value;

      public BackupInfoListEntry(String pKey, String pValue) {
         this.key = pKey;
         this.value = pValue;
      }

      public void render(GuiGraphics p_282911_, int p_281482_, int p_283643_, int p_282795_, int p_283291_, int p_282540_, int p_282181_, int p_283535_, boolean p_281916_, float p_282116_) {
         p_282911_.drawString(RealmsBackupInfoScreen.this.font, this.translateKey(this.key), p_282795_, p_283643_, 10526880);
         p_282911_.drawString(RealmsBackupInfoScreen.this.font, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.key, this.value), p_282795_, p_283643_ + 12, 16777215);
      }

      private Component translateKey(String p_287652_) {
         Component component;
         switch (p_287652_) {
            case "template_name":
               component = TEMPLATE_NAME;
               break;
            case "game_difficulty":
               component = GAME_DIFFICULTY;
               break;
            case "name":
               component = NAME;
               break;
            case "game_server_version":
               component = GAME_SERVER_VERSION;
               break;
            case "uploaded":
               component = UPLOADED;
               break;
            case "enabled_pack":
               component = ENABLED_PACK;
               break;
            case "description":
               component = DESCRIPTION;
               break;
            case "game_mode":
               component = GAME_MODE;
               break;
            case "seed":
               component = SEED;
               break;
            case "world_type":
               component = WORLD_TYPE;
               break;
            default:
               component = UNDEFINED;
         }

         return component;
      }

      public Component getNarration() {
         return Component.translatable("narrator.select", this.key + " " + this.value);
      }
   }
}