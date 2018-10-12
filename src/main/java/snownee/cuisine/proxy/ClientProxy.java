package snownee.cuisine.proxy;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.client.renderer.RenderEntitySeed;
import snownee.cuisine.entities.EntitySeed;

public class ClientProxy extends CommonProxy
{
    public static final ResourceLocation EMPTY = new ResourceLocation(Cuisine.MODID, "empty");

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        if (CuisineConfig.GENERAL.bambooBlowpipe)
        {
            RenderingRegistry.registerEntityRenderingHandler(EntitySeed.class, RenderEntitySeed.FACTORY);
        }
    }

    @Override
    public IAnimationStateMachine loadAnimationStateMachine(ResourceLocation identifier, ImmutableMap<String, ITimeValue> parameters)
    {
        return ModelLoaderRegistry.loadASM(identifier, parameters);
    }

}
