package snownee.cuisine.plugins.hwyla;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import snownee.cuisine.blocks.BlockBasin;
import snownee.cuisine.blocks.BlockCuisineCrops;
import snownee.cuisine.blocks.BlockModLeaves;
import snownee.cuisine.blocks.BlockShearedLeaves;
import snownee.cuisine.tiles.heat.TileFirePit;
import snownee.cuisine.tiles.utensils.TileWok;

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
        CuisineHeatProvider heatProvider = new CuisineHeatProvider();
        registrar.registerBodyProvider(heatProvider, TileWok.class);
        registrar.registerNBTProvider(heatProvider, TileWok.class);
        CuisineHeatFuelProvider heatFuelProvider = new CuisineHeatFuelProvider();
        registrar.registerBodyProvider(heatFuelProvider, TileFirePit.class);
        registrar.registerNBTProvider(heatFuelProvider, TileFirePit.class);
        CuisineBasinProvider basinProvider = new CuisineBasinProvider();
        registrar.registerBodyProvider(basinProvider, BlockBasin.class);
        registrar.registerNBTProvider(basinProvider, BlockBasin.class);
    }
}
