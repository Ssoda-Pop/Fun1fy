package pop.zakichel.funify.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pop.zakichel.funify.Funify;
import pop.zakichel.funify.item.custom.MolotovItem;

public class FunItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Funify.MODID);
    public static final RegistryObject<Item> MOLOTOV = ITEMS.register("molotov",
            ()->new MolotovItem(new Item.Properties().stacksTo(16)));



    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
}
}
