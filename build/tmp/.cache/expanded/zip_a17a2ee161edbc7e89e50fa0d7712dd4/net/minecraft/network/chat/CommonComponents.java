package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Collection;

public class CommonComponents {
   public static final Component EMPTY = Component.empty();
   public static final Component OPTION_ON = Component.translatable("options.on");
   public static final Component OPTION_OFF = Component.translatable("options.off");
   public static final Component GUI_DONE = Component.translatable("gui.done");
   public static final Component GUI_CANCEL = Component.translatable("gui.cancel");
   public static final Component GUI_YES = Component.translatable("gui.yes");
   public static final Component GUI_NO = Component.translatable("gui.no");
   public static final Component GUI_OK = Component.translatable("gui.ok");
   public static final Component GUI_PROCEED = Component.translatable("gui.proceed");
   public static final Component GUI_CONTINUE = Component.translatable("gui.continue");
   public static final Component GUI_BACK = Component.translatable("gui.back");
   public static final Component GUI_TO_TITLE = Component.translatable("gui.toTitle");
   public static final Component GUI_ACKNOWLEDGE = Component.translatable("gui.acknowledge");
   public static final Component GUI_OPEN_IN_BROWSER = Component.translatable("chat.link.open");
   public static final Component GUI_COPY_LINK_TO_CLIPBOARD = Component.translatable("gui.copy_link_to_clipboard");
   public static final Component CONNECT_FAILED = Component.translatable("connect.failed");
   public static final Component NEW_LINE = Component.literal("\n");
   public static final Component NARRATION_SEPARATOR = Component.literal(". ");
   public static final Component ELLIPSIS = Component.literal("...");
   public static final Component SPACE = space();

   public static MutableComponent space() {
      return Component.literal(" ");
   }

   public static MutableComponent days(long p_239423_) {
      return Component.translatable("gui.days", p_239423_);
   }

   public static MutableComponent hours(long p_240042_) {
      return Component.translatable("gui.hours", p_240042_);
   }

   public static MutableComponent minutes(long p_239878_) {
      return Component.translatable("gui.minutes", p_239878_);
   }

   public static Component optionStatus(boolean pIsEnabled) {
      return pIsEnabled ? OPTION_ON : OPTION_OFF;
   }

   public static MutableComponent optionStatus(Component pMessage, boolean pComposed) {
      return Component.translatable(pComposed ? "options.on.composed" : "options.off.composed", pMessage);
   }

   public static MutableComponent optionNameValue(Component pCaption, Component pValueMessage) {
      return Component.translatable("options.generic_value", pCaption, pValueMessage);
   }

   public static MutableComponent joinForNarration(Component... p_267948_) {
      MutableComponent mutablecomponent = Component.empty();

      for(int i = 0; i < p_267948_.length; ++i) {
         mutablecomponent.append(p_267948_[i]);
         if (i != p_267948_.length - 1) {
            mutablecomponent.append(NARRATION_SEPARATOR);
         }
      }

      return mutablecomponent;
   }

   public static Component joinLines(Component... pLines) {
      return joinLines(Arrays.asList(pLines));
   }

   public static Component joinLines(Collection<? extends Component> pLines) {
      return ComponentUtils.formatList(pLines, NEW_LINE);
   }
}