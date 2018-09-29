package snownee.cuisine.client;

import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.Spice;
import snownee.cuisine.internal.CuisineSharedSecrets;

@SuppressWarnings("deprecation")
@Mod.EventBusSubscriber(modid = Cuisine.MODID, value = Side.CLIENT)
public final class CuisineItemRendering
{

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onItemColorsInit(ColorHandlerEvent.Item event)
    {
        ItemColors itemColors = event.getItemColors();
        // BlockColors blockColors = event.getBlockColors();

        //        if (!CuisineConfig.GENERAL.disableEssence)
        //        {
        //        itemColors.registerItemColorHandler((stack, tintIndex) ->
        //        {
        //            IBlockState iblockstate = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
        //            return blockColors.colorMultiplier(iblockstate, null, null, tintIndex);
        //        }, Item.getItemFromBlock(CuisineRegistry.GARDEN));
        //        }

        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 0)
            {
                NBTTagCompound data = stack.getTagCompound();
                if (data != null)
                {
                    Material material = CulinaryHub.API_INSTANCE.findMaterial(data.getString(CuisineSharedSecrets.KEY_MATERIAL));
                    if (material != null)
                    {
                        return material.getRawColorCode();
                    }
                }

                return -1;
            }
            else
            {
                return -1;
            }
        }, CuisineRegistry.INGREDIENT);

        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 0 && CuisineRegistry.SPICE_BOTTLE.hasItem(stack))
            {
                Spice spice = CuisineRegistry.SPICE_BOTTLE.getSpice(stack);
                if (spice != null)
                {
                    return spice.getColorCode();
                }
            }
            return -1;
        }, CuisineRegistry.SPICE_BOTTLE);
    }
}
