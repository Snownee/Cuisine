package snownee.cuisine.fluids;

import net.minecraft.util.ResourceLocation;
import snownee.cuisine.Cuisine;

public class FluidJuice extends VaporizableFluid
{

    public FluidJuice(String name)
    {
        super(name, new ResourceLocation(Cuisine.MODID, "block/" + name + "_still"), new ResourceLocation(Cuisine.MODID, "block/" + name + "_flow"));
    }

}
