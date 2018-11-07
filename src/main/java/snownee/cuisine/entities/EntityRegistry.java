package snownee.cuisine.entities;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;

@EventBusSubscriber(modid = Cuisine.MODID)
public class EntityRegistry
{
    @SubscribeEvent
    public static void onEntityRegister(RegistryEvent.Register<EntityEntry> event)
    {
        if (CuisineConfig.GENERAL.bambooBlowpipe)
        {
            event.getRegistry().register(EntityEntryBuilder.create().entity(EntitySeed.class).id(new ResourceLocation(Cuisine.MODID, "seed"), 0).name(Cuisine.MODID + ".seed").tracker(160, 20, true).build());
        }
        event.getRegistry().register(EntityEntryBuilder.create().entity(EntityModBoat.class).id(new ResourceLocation(Cuisine.MODID, "boat"), 1).name(Cuisine.MODID + ".boat").tracker(80, 3, true).build());
    }
}
