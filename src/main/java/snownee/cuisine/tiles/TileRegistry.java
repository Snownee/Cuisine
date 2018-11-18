package snownee.cuisine.tiles;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import snownee.cuisine.Cuisine;

@Mod.EventBusSubscriber(modid = Cuisine.MODID)
public final class TileRegistry
{

    // This class is here to pave way for 1.13 update, where we may have TileEntityType
    // as a valid Forge Registry.

    private TileRegistry()
    {
        throw new UnsupportedOperationException("No instance for you");
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event)
    {
        GameRegistry.registerTileEntity(TileMortar.class, new ResourceLocation(Cuisine.MODID, "mortar"));
        GameRegistry.registerTileEntity(TileMill.class, new ResourceLocation(Cuisine.MODID, "mill"));
        GameRegistry.registerTileEntity(TileChoppingBoard.class, new ResourceLocation(Cuisine.MODID, "chopping_board"));
        GameRegistry.registerTileEntity(TileJar.class, new ResourceLocation(Cuisine.MODID, "jar"));
        GameRegistry.registerTileEntity(TileWok.class, new ResourceLocation(Cuisine.MODID, "wok"));
        GameRegistry.registerTileEntity(TileBarbecueRack.class, new ResourceLocation(Cuisine.MODID, "barbecue_rack"));
        GameRegistry.registerTileEntity(TileDish.class, new ResourceLocation(Cuisine.MODID, "placed_dish"));
        GameRegistry.registerTileEntity(TileBasin.class, new ResourceLocation(Cuisine.MODID, "basin"));
        GameRegistry.registerTileEntity(TileBasinHeatable.class, new ResourceLocation(Cuisine.MODID, "basin_heatable"));
        GameRegistry.registerTileEntity(TileDrinkroBase.class, new ResourceLocation(Cuisine.MODID, "drinkro_base"));
        GameRegistry.registerTileEntity(TileDrinkroTank.class, new ResourceLocation(Cuisine.MODID, "drinkro_tank"));
        GameRegistry.registerTileEntity(TileSqueezer.class, new ResourceLocation(Cuisine.MODID, "squeezer"));
        GameRegistry.registerTileEntity(TileFruitTree.class, new ResourceLocation(Cuisine.MODID, "fruit_tree"));
    }
}
