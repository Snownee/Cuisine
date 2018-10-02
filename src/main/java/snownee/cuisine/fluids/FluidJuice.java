package snownee.cuisine.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.Cuisine;

public class FluidJuice extends VaporizableFluid
{

    public FluidJuice(String name)
    {
        super(name, new ResourceLocation(Cuisine.MODID, "block/" + name + "_still"), new ResourceLocation(Cuisine.MODID, "block/" + name + "_flow"));
    }

    @Override
    public int getColor(FluidStack stack)
    {
        return mixColor(stack);
    }

    public static void scaleIngredients(FluidStack fluidStack)
    {
    }

    public static int mixColor(FluidStack stack)
    {
        return 0;
    }
}
