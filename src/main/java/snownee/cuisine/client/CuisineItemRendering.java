package snownee.cuisine.client;

import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.Spice;
import snownee.cuisine.fluids.CuisineFluids;
import snownee.cuisine.internal.CuisineSharedSecrets;

@Mod.EventBusSubscriber(modid = Cuisine.MODID, value = Side.CLIENT)
public final class CuisineItemRendering
{

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onItemColorsInit(ColorHandlerEvent.Item event)
    {
        ItemColors itemColors = event.getItemColors();
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

        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 0 && stack.hasTagCompound())
            {
                NBTTagCompound data = stack.getTagCompound();
                if (data.hasKey(FluidHandlerItemStack.FLUID_NBT_KEY, Constants.NBT.TAG_COMPOUND))
                {
                    data = data.getCompoundTag(FluidHandlerItemStack.FLUID_NBT_KEY);
                    if (data.hasKey("FluidName", Constants.NBT.TAG_STRING) && data.getString("FluidName").equals(CuisineFluids.DRINK.getName()) && data.hasKey("Tag", Constants.NBT.TAG_COMPOUND))
                    {
                        data = data.getCompoundTag("Tag");
                        if (data.hasKey("color", Constants.NBT.TAG_INT))
                        {
                            return data.getInteger("color");
                        }
                    }
                }
            }
            return -1;
        }, CuisineRegistry.BOTTLE);
    }
}
