package snownee.cuisine.plugins.hwyla;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import snownee.cuisine.blocks.BlockBasin;
import snownee.cuisine.blocks.BlockCuisineCrops;
import snownee.cuisine.blocks.BlockModLeaves;
import snownee.cuisine.blocks.BlockShearedLeaves;
import snownee.cuisine.tiles.TileWok;

@WailaPlugin
public final class HWYLACompatibility implements IWailaPlugin
{
    static final String KEY_SHOW_CROP_MATURITY = "general.showcrop";

    @Override
    public void register(IWailaRegistrar registrar)
    {
        CuisineCropProvider cropProvider = new CuisineCropProvider();
        registrar.registerBodyProvider(cropProvider, BlockCuisineCrops.class);
        registrar.registerBodyProvider(cropProvider, BlockModLeaves.class);
        registrar.registerBodyProvider(cropProvider, BlockShearedLeaves.class);
        CuisineWokProvider wokProvider = new CuisineWokProvider();
        registrar.registerBodyProvider(wokProvider, TileWok.class);
        registrar.registerNBTProvider(wokProvider, TileWok.class);
        CuisineBasinProvider basinProvider = new CuisineBasinProvider();
        registrar.registerBodyProvider(basinProvider, BlockBasin.class);
        registrar.registerNBTProvider(basinProvider, BlockBasin.class);
    }
}
