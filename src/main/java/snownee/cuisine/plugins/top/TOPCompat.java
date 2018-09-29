package snownee.cuisine.plugins.top;

import java.util.function.Function;

import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import snownee.cuisine.Cuisine;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;

@KiwiModule(modid = Cuisine.MODID, name = "theoneprobe", dependency = "theoneprobe", optional = true)
public class TOPCompat implements IModule
{
    @Override
    public void preInit()
    {
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "snownee.cuisine.plugins.top.TOPCompat$GetTheOneProbe");
    }

    public static class GetTheOneProbe implements Function<ITheOneProbe, Void>
    {

        @Override
        public Void apply(ITheOneProbe probe)
        {
            probe.registerProvider(new CuisineCropProvider());
            probe.registerProvider(new CuisineMachineProvider());
            return null;
        }

    }
}
