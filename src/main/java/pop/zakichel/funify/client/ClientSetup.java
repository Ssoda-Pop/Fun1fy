package pop.zakichel.funify.client;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import pop.zakichel.funify.Funify;
import pop.zakichel.funify.client.render.MolotovRenderer;
import pop.zakichel.funify.entity.FunEntities;


@Mod.EventBusSubscriber(modid = Funify.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public class ClientSetup {
        @SubscribeEvent
        public static void doSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(FunEntities.THROWN_MOLOTOV.get(), MolotovRenderer::new);
        }
    }

