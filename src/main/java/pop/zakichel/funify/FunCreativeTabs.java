package pop.zakichel.funify;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import pop.zakichel.funify.item.FunItems;

import static pop.zakichel.funify.Funify.MODID;

public class FunCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> FUNIFY = CREATIVE_MODE_TABS.register("funify", () -> CreativeModeTab.builder()
            .title(Component.literal("Funify"))
            .icon(() -> new ItemStack(FunItems.MOLOTOV.get()))
            .displayItems((params, output) -> {
                output.accept(FunItems.MOLOTOV.get());
            }).build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
