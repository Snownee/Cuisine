package snownee.cuisine.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.client.renderer.RenderEntitySeed;
import snownee.cuisine.client.renderer.RenderModBoat;
import snownee.cuisine.entities.EntityModBoat;
import snownee.cuisine.entities.EntitySeed;

@Mod.EventBusSubscriber(modid = Cuisine.MODID, value = Side.CLIENT)
public final class CuisineEntityRendering
{

    @SubscribeEvent
    public static void entityRendererRegistration(ModelRegistryEvent event)
    {
        if (CuisineConfig.GENERAL.bambooBlowpipe)
        {
            RenderingRegistry.registerEntityRenderingHandler(EntitySeed.class, m -> new RenderEntitySeed(m, Minecraft.getMinecraft().getRenderItem()));
        }
        RenderingRegistry.registerEntityRenderingHandler(EntityModBoat.class, RenderModBoat::new);

    }
}
