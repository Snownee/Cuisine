package snownee.cuisine.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.animation.AnimationTESR;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.client.model.ChoppingBoardModel;
import snownee.cuisine.client.renderer.TESRBarbecueRack;
import snownee.cuisine.client.renderer.TESRBasin;
import snownee.cuisine.client.renderer.TESRChoppingBoard;
import snownee.cuisine.client.renderer.TESRDrinkroBase;
import snownee.cuisine.client.renderer.TESRDrinkroTank;
import snownee.cuisine.client.renderer.TESRMortar;
import snownee.cuisine.client.renderer.TESRWok;
import snownee.cuisine.fluids.CuisineFluidBlocks;
import snownee.cuisine.fluids.CuisineFluids;
import snownee.cuisine.tiles.TileBarbecueRack;
import snownee.cuisine.tiles.TileBasin;
import snownee.cuisine.tiles.TileChoppingBoard;
import snownee.cuisine.tiles.TileDrinkroBase;
import snownee.cuisine.tiles.TileDrinkroTank;
import snownee.cuisine.tiles.TileMill;
import snownee.cuisine.tiles.TileMortar;
import snownee.cuisine.tiles.TileSqueezer;
import snownee.cuisine.tiles.TileWok;
import snownee.kiwi.client.ModelUtil;

@Mod.EventBusSubscriber(modid = Cuisine.MODID, value = Side.CLIENT)
public final class CuisineBlockRendering
{
    @SubscribeEvent
    public static void onBlockColorsInit(ColorHandlerEvent.Block event)
    {
        BlockColors blockColors = event.getBlockColors();
        blockColors.registerBlockColorHandler((state, blockAccess, pos, tintIndex) -> {
            if (tintIndex == 0)
            {
                return blockAccess != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(blockAccess, pos) : ColorizerFoliage.getFoliageColorBasic();
            }
            if (tintIndex == 1)
            {
                Block block = state.getBlock();
                if (block == CuisineRegistry.LEAVES_CITRON)
                {
                    return 0xDDCC58;
                }
                if (block == CuisineRegistry.LEAVES_GRAPEFRUIT)
                {
                    return 0xF4502B;
                }
                if (block == CuisineRegistry.LEAVES_LEMON)
                {
                    return 0xEBCA4B;
                }
                if (block == CuisineRegistry.LEAVES_LIME)
                {
                    return 0xCADA76;
                }
                if (block == CuisineRegistry.LEAVES_MANDARIN)
                {
                    return 0xF08A19;
                }
                if (block == CuisineRegistry.LEAVES_ORANGE)
                {
                    return 0xF08A19;
                }
                if (block == CuisineRegistry.LEAVES_POMELO)
                {
                    return 0xF7F67E;
                }
            }
            return -1;
        }, CuisineRegistry.BAMBOO_PLANT, CuisineRegistry.BAMBOO, CuisineRegistry.LEAVES_CITRON, CuisineRegistry.LEAVES_GRAPEFRUIT, CuisineRegistry.LEAVES_LEMON, CuisineRegistry.LEAVES_LIME, CuisineRegistry.LEAVES_MANDARIN, CuisineRegistry.LEAVES_MANDARIN, CuisineRegistry.LEAVES_ORANGE, CuisineRegistry.LEAVES_POMELO, CuisineRegistry.SHEARED_LEAVES);
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event)
    {
        OBJLoader.INSTANCE.addDomain(Cuisine.MODID);

        ModelLoaderRegistry.registerLoader(ChoppingBoardModel.Loader.INSTANCE);

        ModelUtil.mapFluidModel(CuisineFluidBlocks.SOY_MILK);
        ModelUtil.mapFluidModel(CuisineFluidBlocks.MILK);
        ModelUtil.mapFluidModel(CuisineFluidBlocks.SOY_SAUCE);
        ModelUtil.mapFluidModel(CuisineFluidBlocks.FRUIT_VINEGAR);
        ModelUtil.mapFluidModel(CuisineFluidBlocks.RICE_VINEGAR);
        ModelUtil.mapFluidModel(CuisineFluidBlocks.EDIBLE_OIL);
        ModelUtil.mapFluidModel(CuisineFluidBlocks.SESAME_OIL);
        ModelUtil.mapFluidModel(CuisineFluidBlocks.SUGARCANE_JUICE);

        ClientRegistry.bindTileEntitySpecialRenderer(TileMortar.class, new TESRMortar());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMill.class, new AnimationTESR<>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileChoppingBoard.class, new TESRChoppingBoard());
        ClientRegistry.bindTileEntitySpecialRenderer(TileWok.class, new TESRWok());
        ClientRegistry.bindTileEntitySpecialRenderer(TileBarbecueRack.class, new TESRBarbecueRack());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSqueezer.class, new AnimationTESR<>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileBasin.class, new TESRBasin());
        ClientRegistry.bindTileEntitySpecialRenderer(TileDrinkroBase.class, new TESRDrinkroBase());
        ClientRegistry.bindTileEntitySpecialRenderer(TileDrinkroTank.class, new TESRDrinkroTank());
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event)
    {
        event.getMap().registerSprite(CuisineFluids.JUICE.getStill());
        event.getMap().registerSprite(CuisineFluids.JUICE.getFlowing());
    }

}
