package snownee.cuisine.plugins.hwyla;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import snownee.cuisine.blocks.BlockCuisineCrops;
import snownee.cuisine.blocks.BlockFirePit;

@WailaPlugin
public final class HWYLACompatibility implements IWailaPlugin
{
    static final String KEY_SHOW_CROP_MATURITY = "general.showcrop";

    @Override
    public void register(IWailaRegistrar registrar)
    {
        registrar.registerBodyProvider(new CuisineCropBodyInfoProvider(), BlockCuisineCrops.class);
        registrar.registerBodyProvider(new CuisineMachineBodyInfoProvider(), BlockFirePit.class);
    }
}
