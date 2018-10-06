package snownee.cuisine.proxy;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import snownee.cuisine.Cuisine;

public class ClientProxy extends CommonProxy
{
    public static final ResourceLocation EMPTY = new ResourceLocation(Cuisine.MODID, "empty");

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        // MinecraftForge.EVENT_BUS.register(new CuisineParticles());
        OBJLoader.INSTANCE.addDomain(Cuisine.MODID);
    }

    @Override
    public IAnimationStateMachine loadAnimationStateMachine(ResourceLocation identifier, ImmutableMap<String, ITimeValue> parameters)
    {
        return ModelLoaderRegistry.loadASM(identifier, parameters);
    }

}
