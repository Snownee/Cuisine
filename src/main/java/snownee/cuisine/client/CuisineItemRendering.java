package snownee.cuisine.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerFoliage;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.Spice;
import snownee.cuisine.internal.CuisineSharedSecrets;
import snownee.cuisine.internal.food.Drink;

@Mod.EventBusSubscriber(modid = Cuisine.MODID, value = Side.CLIENT)
public final class CuisineItemRendering
{
    public static final ResourceLocation EMPTY_MODEL = new ResourceLocation(Cuisine.MODID, "empty");

    private CuisineItemRendering()
    {
        throw new UnsupportedOperationException("No instance for you");
    }

    @SubscribeEvent
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
                        int doneness = 0;
                        if (data.hasKey(CuisineSharedSecrets.KEY_DONENESS, Constants.NBT.TAG_INT))
                        {
                            doneness = data.getInteger(CuisineSharedSecrets.KEY_DONENESS);
                        }
                        int raw = material.getRawColorCode();
                        int cooked = material.getCookedColorCode();

                        //debug code
                        doneness = (int) (Minecraft.getSystemTime() / 10 % 200);

                        if (doneness < 100)
                        {
                            return mixColor(raw, cooked, doneness / 100f);
                        }
                        else
                        {
                            return mixColor(cooked, 0, (doneness - 100) / 100f);
                        }
                    }
                }
            }
            return -1;
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
            else if (tintIndex == 1 && CuisineRegistry.SPICE_BOTTLE.hasFluid(stack))
            {
                IFluidHandlerItem handler = CuisineRegistry.SPICE_BOTTLE.getFluidHandler(stack);
                if (handler != null)
                {
                    FluidStack fluid = handler.drain(Integer.MAX_VALUE, false);
                    if (fluid != null)
                    {
                        return fluid.getFluid().getColor(fluid);
                    }
                }
            }
            return -1;
        }, CuisineRegistry.SPICE_BOTTLE);

        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 0)
            {
                stack = ItemHandlerHelper.copyStackWithSize(stack, 1);
                IFluidHandlerItem handlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                if (handlerItem != null)
                {
                    FluidStack fluid = handlerItem.drain(Integer.MAX_VALUE, false);
                    if (fluid != null)
                    {
                        return fluid.getFluid().getColor(fluid);
                    }
                }
            }
            return -1;
        }, CuisineRegistry.BOTTLE);

        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 1 && stack.hasCapability(CulinaryCapabilities.FOOD_CONTAINER, null))
            {
                FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
                CompositeFood food = container.get();
                if (food != null && food.getClass() == Drink.class)
                {
                    return ((Drink) food).getColor();
                }
            }
            return -1;
        }, CuisineRegistry.DRINK);

        itemColors.registerItemColorHandler((
                stack, tintIndex
        ) -> tintIndex == 0 ? ColorizerFoliage.getFoliageColorBasic() : -1, CuisineRegistry.SHEARED_LEAVES);
    }

    public static int mixColor(int color1, int color2, float weight)
    {
        if (weight <= 0)
        {
            return color1;
        }
        else if (weight >= 1)
        {
            return color2;
        }
        else
        {
            float a = (color1 >> 24 & 255) / 255.0F * weight;
            float r = (color1 >> 16 & 255) / 255.0F * weight;
            float g = (color1 >> 8 & 255) / 255.0F * weight;
            float b = (color1 & 255) / 255.0F * weight;
            weight = 1 - weight;
            a += (color2 >> 24 & 255) / 255.0F * weight;
            r += (color2 >> 16 & 255) / 255.0F * weight;
            g += (color2 >> 8 & 255) / 255.0F * weight;
            b += (color2 & 255) / 255.0F * weight;
            a = MathHelper.clamp(a, 0, 1) * 255;
            r = MathHelper.clamp(r, 0, 1) * 255;
            g = MathHelper.clamp(g, 0, 1) * 255;
            b = MathHelper.clamp(b, 0, 1) * 255;
            return (int) a << 24 | (int) r << 16 | (int) g << 8 | (int) b;
        }
    }
}
