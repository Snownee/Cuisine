package snownee.cuisine.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import snownee.cuisine.Cuisine;

public class FluidOil extends Fluid
{
    public FluidOil(String name)
    {
        super(name, new ResourceLocation(Cuisine.MODID, "block/" + name + "_still"), new ResourceLocation(Cuisine.MODID, "block/" + name + "_flow"), 0x88FFFFFF);
        setDensity(800);
    }

}
