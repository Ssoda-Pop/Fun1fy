package pop.zakichel.funify.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pop.zakichel.funify.Funify;

public class FunEntities {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
            Funify.MODID);
    public static final RegistryObject<EntityType<MolotovEntity>> THROWN_MOLOTOV = ENTITY_TYPES.register("molotov",
            () -> EntityType.Builder.of(MolotovEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).build("molotov"));


    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}

